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
            return "Deprecated";
        }
    }
}
