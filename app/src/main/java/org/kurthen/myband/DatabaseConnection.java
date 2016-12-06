package org.kurthen.myband;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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

    private boolean mBound = false;

    /* This class is a Singleton */
    private static DatabaseConnection instance = new DatabaseConnection();
    public static DatabaseConnection getInstance() {return instance; }

    public DatabaseConnection(){
    }

    public void setContext(Context c){
        mContext = c;
    }

    public String postRegistration(String name, String email, String pass){

        String appendix = "name=" + name +
                "&email=" + email +
                "&pw=" + pass;

        Intent syncService = new Intent(mContext, SynchronizingService.class);
        mContext.bindService(syncService, mServiceConn, Context.BIND_IMPORTANT);
        if(mBound){
            Handler h = new Handler();

        }

        String status = NetworkAccess.performPostRequest("registration.php", appendix);

        //DEBUG
        Log.d("RESULT", status);

        return status;
    }

    public String checkUserCredentials(String email, String pass){
        String appendix = "email=" + email + "&pw=" + pass;

        PostData request = new PostData();
        String answer = request.doInBackground("log_user.php", appendix);

        Log.d("RESULT", answer);

        return answer;
    }

    public void addBand(Band band){

    }

    public void addUpdate(Update update){

    }

    public void addEvent(Event event){

    }

    public void addTransaction(Transaction transaction){

    }

    public void addSong(Song song){

    }

    /*
     * Receive all user data from the online database by a user key being saved
     * in the CurrentProfile class.
     *
     */
    public void fetchUserData(){

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
    }

}
