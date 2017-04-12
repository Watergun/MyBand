package org.kurthen.myband;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by Leonhard on 15.09.2016.
 */
public class DatabaseConnection{
    private HttpURLConnection mHttpConnection;
    private Context mContext;
    private SynchronizingService mService;

    private NetworkThread mNetworkThread;
    private boolean mBound = false;

    /* This class is a Singleton */
    private static DatabaseConnection instance = new DatabaseConnection();
    public static DatabaseConnection getInstance() {return instance; }

    public DatabaseConnection(){
        /*mNetworkThread = new HandlerThread("Network Thread");
        mNetworkThread.start();*/

    }

    public void setContext(Context c){
        mContext = c;
    }

    public Handler startNetworking(Handler uiHandler){
        if(uiHandler != null) {
            mNetworkThread = new NetworkThread(uiHandler);
            mNetworkThread.start();
            Handler h =  mNetworkThread.prepareHandler();
            h.sendEmptyMessage(R.integer.MSG_SYNC);
            return h;
        }
        else
            return null;
    }

    public void stopNetworking(){
        mNetworkThread.quit();
    }

    public void addBand(Band band){

    }

    public void addUpdate(Update update){

    }

    public void addEvent(Event event){

    }

    public void addTransaction(Transaction transaction){

    }

    public void addContact(Contact contact){

    }

    /*
     * Receive all user data from the online database by a user key being saved
     * in the CurrentProfile class.
     *
     */
    public void fetchUserData(){

    }

    public void fetchUserImage(int id){
        //Log.d("REQUEST", "Requesting image file: " + id);
    }

    public void fetchBandImage(int id){
        //Log.d("REQUEST", "Requesting image file: " + id);
    }

    private class SynchronizationTask implements Runnable{

        public SynchronizationTask() {}

        public void run(){
            String json_encoded_data = NetworkAccess.performPostRequest("receive_band_data.php",
                                        "t=" + CurrentProfile.getInstance().getToken());
            //Log.d("RESPONSE", json_encoded_data);
            try {
                /*
                JSONObject jsUser = new JSONObject(json_encoded_data);
                u.setId(Integer.parseInt(jsUser.getString("id")));
                u.setFirstName(jsUser.getString("name"));
                u.setInstruments(jsUser.getString("instruments").split(","));
                u.setPictureThumbnail(ImageManager.getInstance().
                        createResourceFromBase64(jsUser.getString("picture"), 85, 80));
                */

                // Create all bands
                JSONArray jsBands = new JSONArray(json_encoded_data);
                Band[] bands = new Band[jsBands.length()];

                for(int i = 0; i < jsBands.length(); i++) {
                    JSONObject jsBand = jsBands.getJSONObject(i);

                    Band band = new Band();
                    band.setName(jsBand.getString("name"));
                    band.setId(jsBand.getInt("id"));

                    if(!band.comparePictureHash(jsBand.getString("picture_hash"))){
                        fetchBandImage(band.getId());
                    }
                    //String picture_encoded = jsBand.getString("picture");
                    //band.setPictureThumbnail();

                    band.setTransactionSum(Float.parseFloat(jsBand.getString("cash_sum")));

                    JSONArray jsMembers = jsBand.getJSONArray("members");
                    User[] bandMembers = new User[jsMembers.length()];
                    for(int j = 0; j < jsMembers.length(); j++){
                        JSONObject jsMember = jsMembers.getJSONObject(j);
                        User member = new User();
                        member.setId(Integer.parseInt(jsMember.getString("id")));
                        member.setFirstName(jsMember.getString("name"));
                        member.setEmail(jsMember.getString("email"));
                        //member.setInstruments(jsMember.getString("instruments").split(","));
                        if(!member.comparePictureHash(jsMember.getString("picture_hash"))){
                            fetchUserImage(member.getId());
                        }
                        //        createResourceFromBase64(jsMember.getString("picture"), 65, 60));
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

                CurrentProfile.getInstance().getUser().setBands(bands);

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
        }

    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SynchronizingService.ActiveSynchronization binder =
                    (SynchronizingService.ActiveSynchronization) service;
            mService = ((SynchronizingService.ActiveSynchronization) service).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private class NetworkThread extends HandlerThread{

        private boolean mPaused = false;
        private boolean mRunning = true;
        private Handler mUIHandler;
        private Handler mNwHandler;

        public NetworkThread(Handler uiHandler){
            super("Network thread", Process.THREAD_PRIORITY_BACKGROUND);
            mUIHandler = uiHandler;
        }

        public Handler prepareHandler(){
            if(this.getLooper() != null) {
                mNwHandler = new Handler(this.getLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what == R.integer.MSG_PAUSE){
                            Log.d("THREADSTAT", "Network thread paused");
                            mPaused = true;
                        }
                        else if(msg.what == R.integer.MSG_STOP){
                            Log.d("THREADSTAT", "Network therad stopped");
                            mRunning = false;
                        }
                        else if(msg.what == R.integer.MSG_SYNC){
                            //Log.d("SYNCSTAT", "Processing sync message...");

                            if(!mPaused){
                                //Log.d("SYNCSTAT", "Posting sync task...");
                                mNwHandler.post(new SynchronizationTask());

                            }

                            //Log.d("SYNCSTAT", "Messaging UI Thread...");
                            mUIHandler.sendEmptyMessageDelayed((R.integer.MSG_REFRESH), 500);

                            if(mRunning) {
                                //Log.d("SYNCSTAT", "Messaging own thread in 2 secs...");
                                mNwHandler.sendEmptyMessageDelayed(R.integer.MSG_SYNC, 2000);
                            }
                        }
                        super.handleMessage(msg);
                    }
                };
                return mNwHandler;
            }
            else {
                //Log.d("THREADSTAT", "Critical_ could not receive thread looper!");
                return null;
            }
        }
/*
        public void run() {
            super.run();
        }
*/
    }

}
