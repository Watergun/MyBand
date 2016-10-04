package org.kurthen.myband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        SharedPreferences localStatus = getSharedPreferences("local_instance", MODE_PRIVATE);

        if(localStatus.getBoolean(getString(R.string.local_toggle), false))
            startMainActivity(localStatus.getString("email", ""), localStatus.getString("password", ""));
        else
            startAuthActivity();
        finish();
    }

    //Separate main activity start
    public void startMainActivity(String email, String password){
        Intent mainAct = new Intent(this, HomeActivity.class);

        User loggedUser = DataObjectManager.getInstance().createUser("", email, password);
        CurrentProfile.getInstance().setUser(loggedUser);
        Log.d("STATUS", "Local USer found: " + email);

        startActivity(mainAct);
    }

    //Separate authentication
    public void startAuthActivity(){
        Log.d("STATUS", "No local user found");
        Intent AuthAct = new Intent(this, AuthenticationActivity.class);
        startActivity(AuthAct);
    }
}
