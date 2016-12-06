package org.kurthen.myband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            // Simulate network access.
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }

        //Start the background network service that is syncing our user data
        Intent networkService = new Intent(this, SynchronizingService.class);
        startService(networkService);

        SharedPreferences localStatus = getSharedPreferences("local_instance", MODE_PRIVATE);

        if(localStatus.getBoolean(getString(R.string.local_toggle), false))
            startMainActivity(localStatus.getString(getString(R.string.local_key), ""),
                            localStatus.getString("password", ""));
        else
            startAuthActivity();
        finish();
    }

    //Separate main activity start
    public void startMainActivity(String email, String password){
        Log.d("STATUS", "Local User found: " + email);

        //Even though a local user has been found, authentication is still necessary
        AutoLoginTask autoLogin = new AutoLoginTask(email, password);
        autoLogin.execute();
    }

    //Separate authentication
    public void startAuthActivity(){
        Log.d("STATUS", "No local user found");
        Intent AuthAct = new Intent(this, AuthenticationActivity.class);
        startActivity(AuthAct);
    }

    public class AutoLoginTask extends AsyncTask<Void, Void, String>{
        String mEmail;
        String mPassword;
        String[] mCredentials;

        public AutoLoginTask(String email, String password){
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params){
            return DatabaseConnection.getInstance().checkUserCredentials(mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(String status){
            if(!TextUtils.equals(status, "Login successful")){  // case of unsuccessful login
                // This means that the local user credentials are incorrect, which is quite uncommon,
                // but results in a new login and the deletion of any local preferences
                Log.d("ERROR", "Local user is incorrect! Deleting credentials...");
                SharedPreferences sp = getSharedPreferences("local_instance", MODE_PRIVATE);
                sp.edit().putBoolean(getString(R.string.local_toggle), false).commit();

                // Start a new authentication
                startAuthActivity();
            }
            else{   // case of successful login
                CurrentProfile.getInstance().setUserCredentials(mEmail, mPassword);

                Intent homeAct = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeAct);
            }
        }
    }
}
