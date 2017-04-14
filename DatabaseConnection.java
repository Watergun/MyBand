package org.kurthen.myband;

import android.os.AsyncTask;
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
import android.text.TextUtils;

/**
 * Created by Leonhard on 15.09.2016.
 */
public class DatabaseConnection{
    private URL mServerUrl;
    private HttpURLConnection mHttpConnection;

    /* This class is a Singleton */
    private static DatabaseConnection instance = new DatabaseConnection();
    public static DatabaseConnection getInstance() {return instance; }

    private DatabaseConnection(){
    }

    public String postRegistration(User newUser){

        String appendix = "name=" + newUser.getFirstName() +
                "&email=" + newUser.getEmail() +
                "&pw=" + newUser.getPassword();

        Log.d("POST", appendix);

        PostData request = new PostData();
        String status = request.doInBackground("registration.php", appendix);

        //DEBUG
        Log.d("RESULT", status);

        return status;
    }

    public String checkUserCredentials(String email, String password){
        String appendix = "email=" + email + "&pw=" + password;

        Log.d("POST", appendix);

        PostData request = new PostData();
        String answer = request.doInBackground("log_user.php", appendix);

        Log.d("RESULT", answer);

        return answer;
    }

    public void receiveCurrentUserInformation(User userKey){
        String auth = "email=" + userKey.getEmail() + "&pw=" + userKey.getPassword();

        //GetData request = new GetData();

        //First step is to fill the user information
        String appendix = auth + "&obj=User&objid";

        //String answer = request.doInBackground("receive_userdata.php");
    }

    private class GetData extends AsyncTask<String, Void, String>{
        @Override
        /*
         * General method to make a GET request
         *      -> string parameter: full URL appendix
         *
         */
        protected String doInBackground(String... strings){
            if(strings.length < 1 || strings.length > 2)
                return "GET request could not be done.";

            String response = "";

            //Establish HTTP connection
            try {
                mServerUrl = new URL("http://www.mb-serve.esy.es/".concat(strings[0]));
                ////mServerUrl = new URL("http://requestb.in/16ury2w1");
                mHttpConnection = (HttpURLConnection) mServerUrl.openConnection();
                mHttpConnection.setRequestMethod("GET");

                mHttpConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                int code = mHttpConnection.getResponseCode();
                Log.d("CODE", Integer.toString(code));

                BufferedReader in = new BufferedReader(new InputStreamReader(
                                            mHttpConnection.getInputStream()));
                String inputLine = "";
                while((inputLine = in.readLine()) != null) {
                    response += inputLine;
                    Log.d("RESPONSE", inputLine);
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
    }

    private class PostData extends AsyncTask<String, Void, String>{
        @Override
        /*
         * Parameter: Strings... strings
         * Array of strings, where:
         *      -> first String defining which serverscript is being called
         *      -> second string is preformatted parameters
         */
        protected String doInBackground(String... strings){
            //A POST request without further parameters makes no sense
            if(strings.length != 2)
                return "POST request could not be done.";

            String response = "";

            //Establish HTTP connection
            try {
                mServerUrl = new URL("http://www.mb-serve.esy.es/".concat(strings[0]));
                //mServerUrl = new URL("http://requestb.in/16ury2w1");
                mHttpConnection = (HttpURLConnection) mServerUrl.openConnection();
                mHttpConnection.setRequestMethod("POST");
                mHttpConnection.setDoOutput(true);

                byte[]out = strings[1].getBytes("UTF-8");
                mHttpConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                mHttpConnection.setRequestProperty("Content-Length", Integer.toString(out.length));
                //mHttpConnection.setFixedLengthStreamingMode(out.length);
                DataOutputStream dataOut = new DataOutputStream(mHttpConnection.getOutputStream());
                dataOut.write(out);
                dataOut.flush();
                dataOut.close();
                BufferedReader r = new BufferedReader(new InputStreamReader(mHttpConnection.getInputStream()));

                String inputLine = "";
                while((inputLine = r.readLine()) != null) {
                    Log.d("RESPONSE", inputLine);
                    response += inputLine;
                }

                //response = mHttpConnection.getResponseMessage();
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
    }
}
