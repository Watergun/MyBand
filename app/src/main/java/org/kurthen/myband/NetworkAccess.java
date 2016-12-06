package org.kurthen.myband;

import android.net.Network;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Leonhard on 05.10.2016.
 */

public class NetworkAccess{

    protected static String mNetworkError;
    protected static String mServerName = "https://home.stusta.de/~010576/";

    public NetworkAccess(){}

    /*
    @Override
    public void run(){}
    */

    /*
     * General method to make a GET request
     *      -> string parameter: full URL appendix
     *
     */
    protected static String performGetRequest(String serverScript, String args){

        String response = "";

        //Establish HTTP connection
        try {
            URL serverUrl = new URL(mServerName + serverScript + "?" + args);
            ////mServerUrl = new URL("http://requestb.in/16ury2w1");
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            int code = conn.getResponseCode();
            Log.d("CODE", Integer.toString(code));

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine = "";
            while((inputLine = in.readLine()) != null) {
                response += inputLine;
            }

            //mHttpConnection.disconnect();
            return response;
        }
        catch(MalformedURLException e){
            response =  "Failed to build URL. ".concat(e.getMessage());
        }
        catch(IOException e){
            response =  "Failed to reach server. ".concat(e.getMessage());
        }
        finally {
            return response;
        }
    }


    /*
     * Parameter: String serverScript: Defines the script name that is being called by this POST request
     */
    protected static String performPostRequest(String serverScript, String args){
        //A POST request without further parameters makes no sense

        String response = "";

        Log.d("REQUEST", args);

        //Establish HTTP connection
        try {
            URL serverUrl = new URL(mServerName + serverScript);
            //mServerUrl = new URL("http://requestb.in/16ury2w1");
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            byte[]out = args.getBytes("UTF-8");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(out.length));
            //mHttpConnection.setFixedLengthStreamingMode(out.length);
            DataOutputStream dataOut = new DataOutputStream(conn.getOutputStream());
            dataOut.write(out);
            dataOut.flush();
            dataOut.close();
            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine = "";
            while((inputLine = r.readLine()) != null) {
                response += inputLine;
            }

            return response;
        }
        catch(MalformedURLException e){
            response =  "Failed to build URL. ".concat(e.getMessage());
        }
        catch(IOException e){
            response =  "Failed to reach server. ".concat(e.getMessage());
        }
        finally {
            return response;
        }
    }
}
