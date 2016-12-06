package org.kurthen.myband;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class SynchronizingService extends Service {

    private final static int TASK_AUTHENTICATTION = 1;
    private final static int TASK_ACTIVE_SYNC = 2;
    private final static int TASK_PASSIVE_SYNC = 3;


    private NotificationManager mNtMngr;

    private IBinder activeSync = new ActiveSynchronization();
    private WorkHandler mWorker;

    private String[] mCredentials;

    public SynchronizingService() {
    }

    @Override
    public void onCreate(){
        HandlerThread passiveSync = new HandlerThread("Synchronizer", Process.THREAD_PRIORITY_BACKGROUND);
        passiveSync.start();

        mWorker = new WorkHandler(passiveSync.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Message msg = mWorker.obtainMessage();
        msg.what = TASK_PASSIVE_SYNC;
        mWorker.sendMessage(msg);

        return Service.START_STICKY;
    }

    public void dispatchActiveSync(String target, String args, Handler handler){
        Message msg = mWorker.obtainMessage();
        msg.what = TASK_ACTIVE_SYNC;
        Bundle b = new Bundle();
        b.putString("target", target);
        b.putString("args", args);
        msg.setData(b);
        mWorker.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return activeSync;
    }

    /* Binding interface to this service that provides a local instance
     *
     */
    public class ActiveSynchronization extends Binder{
        public SynchronizingService getService() {
            return SynchronizingService.this;
        }
    }

    // Handler class to perform the network tasks
    private final class WorkHandler extends Handler{

        public boolean mFetched = false;
        private String mResponse;

        public WorkHandler(Looper looper){
            super(looper);
        }

        public String getResponse(){
            mFetched = true;
            return mResponse;
        }

        @Override
        public void handleMessage(Message msg){
            switch(msg.what){

                case TASK_ACTIVE_SYNC:
                    mResponse = syncUserData(msg.getData().getString("target"),
                                msg.getData().getString("args"));
                    mFetched = false;
                    break;

                case TASK_PASSIVE_SYNC:
                    mResponse = syncBufferedUserNotifications();
                    break;

                default:
                    break;
            }
        }

        public String syncBufferedUserNotifications(){
            if(mCredentials == null){
                return "";
            }

            CurrentProfile profile = CurrentProfile.getInstance();

            String json_encoded_data = NetworkAccess.performPostRequest("receive_notifications.php",
                    "");

            return "in work";
        }

        public String syncUserData(String target, String args){
            User u = CurrentProfile.getInstance().getUser();
            String auth = "email=" + u.getEmail() + "&pw=" + u.getPassword();

            String json_encoded_data = NetworkAccess.performPostRequest(target, args);

            try {
                JSONObject jsUser = new JSONObject(json_encoded_data);
                u.setId(Integer.parseInt(jsUser.getString("id")));
                u.setFirstName(jsUser.getString("name"));
                u.setInstruments(jsUser.getString("instruments").split(","));
                u.setPictureThumbnail(ImageManager.getInstance().
                        createResourceFromBase64(jsUser.getString("picture"), 85, 80));

                // Create all bands
                JSONArray jsBands = jsUser.getJSONArray("bands");
                Band[] bands = new Band[jsBands.length()];

                for(int i = 0; i < jsBands.length(); i++) {
                    JSONObject jsBand = jsBands.getJSONObject(i);

                    Band band = new Band();
                    band.setName(jsBand.getString("name"));

                    String picture_encoded = jsBand.getString("picture");
                    band.setPictureThumbnail(ImageManager.getInstance().
                            createResourceFromBase64(picture_encoded, 55, 50));

                    band.setTransactionSum(Float.parseFloat(jsBand.getString("cash_sum")));

                    JSONArray jsMembers = jsBand.getJSONArray("members");
                    User[] bandMembers = new User[jsMembers.length()];
                    for(int j = 0; j < jsMembers.length(); j++){
                        JSONObject jsMember = jsMembers.getJSONObject(j);
                        User member = new User();
                        member.setId(Integer.parseInt(jsMember.getString("id")));
                        member.setFirstName(jsMember.getString("name"));
                        member.setEmail(jsMember.getString("email"));
                        member.setInstruments(jsMember.getString("instruments").split(","));
                        member.setPictureThumbnail(ImageManager.getInstance().
                                createResourceFromBase64(jsMember.getString("picture"), 65, 60));
                        bandMembers[j] = member;
                    }
                    band.setMembers(bandMembers);


                    JSONArray jsUpdates = jsBand.getJSONArray("updates");
                    Update[] bandUpdates = new Update[jsUpdates.length()];
                    for(int j = 0; j < jsUpdates.length(); j++){
                        JSONObject jsUpdate = jsUpdates.getJSONObject(j);
                        Update update = new Update();
                        update.setId(Integer.parseInt(jsUpdate.getString("id")));
                        update.setTitle(jsUpdate.getString("title"));
                        update.setNotficationCounter(Integer.parseInt(jsUpdate.getString("notification_counter")));

                        for (User member : bandMembers){
                            if(member.getId() == Integer.parseInt(jsUpdate.getString("creator_id")))
                                update.setCreator(member);
                        }

                        bandUpdates[j] = update;
                    }
                    band.setUpdates(bandUpdates);

                    JSONArray jsEvents = jsBand.getJSONArray("events");
                    Event[] bandEvents = new Event[jsEvents.length()];
                    for(int j = 0; j < jsEvents.length(); j++){
                        JSONObject jsEvent = jsEvents.getJSONObject(j);
                        Event event = new Event();
                        event.setId(Integer.parseInt(jsEvent.getString("id")));
                        event.setTitle(jsEvent.getString("title"));
                        event.setLocation(jsEvent.getString("location"));
                        event.setStarttime(Timestamp.valueOf(jsEvent.getString("starttime")));
                        event.setEndtime(Timestamp.valueOf(jsEvent.getString("endtime")));
                        event.setReminder(Integer.parseInt(jsEvent.getString("reminder")));
                        event.setPay(Float.parseFloat(jsEvent.getString("pay")));
                        event.setNotficationCounter(Integer.parseInt(jsEvent.getString("notification_counter")));

                        for (User member : bandMembers){
                            if(member.getId() == Integer.parseInt(jsEvent.getString("creator_id")))
                                event.setCreator(member);
                        }

                        bandEvents[j] = event;
                    }
                    band.setEvents(bandEvents);

                    JSONArray jsTransactions = jsBand.getJSONArray("transactions");
                    Transaction[] bandTransactions = new Transaction[jsTransactions.length()];
                    for(int j = 0; j < jsTransactions.length(); j++){
                        JSONObject jsTransaction = jsTransactions.getJSONObject(j);
                        Transaction trans = new Transaction();
                        trans.setId(Integer.parseInt("id"));
                        trans.setTitle(jsTransaction.getString("title"));
                        trans.setValue(Float.parseFloat(jsTransaction.getString("value")));
                        trans.setDescription(jsTransaction.getString("description"));
                        trans.setNotficationCounter(Integer.parseInt(jsTransaction.getString("notification_counter")));

                        for (User member : bandMembers){
                            if(member.getId() == Integer.parseInt(jsTransaction.getString("creator_id")))
                                trans.setCreator(member);
                        }

                        for(Event event : bandEvents){
                            if(event.getId() == Integer.parseInt(jsTransaction.getString("event_id")))
                                trans.setEvent(event);
                        }

                        bandTransactions[j] = trans;
                    }
                    band.setTransactions(bandTransactions);

                    bands[i] = band;
                }

                u.setBands(bands);

                Log.d("STATUS", "Successfully fetched data from account server!");
            }
            catch(JSONException e){
                Log.d("ERROR", "JSON Exception occured! " + e.getMessage());
                e.printStackTrace();
                Log.d("STATUS", "DatabaseConnection: Leaving user data as before!");
            }
            catch(NumberFormatException e){
                Log.d("ERROR", "Number format error encountered while fetching data from the server! " + e.getMessage());
                e.printStackTrace();
                Log.d("STATUS", "DatabaseConnection: Leaving user data as before!");
            }
            catch(IllegalArgumentException e){
                Log.d("ERROR", "Illegal argument encountered while fetching data from the server! " + e.getMessage());
                e.printStackTrace();
                Log.d("STATUS", "DatabaseConnection: Leaving user data as before!");
            }
            return null;
        }
    }
}
