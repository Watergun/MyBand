package org.kurthen.myband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        SharedPreferences localStatus = getPreferences(MODE_PRIVATE);

        if(localStatus.getBoolean("registered", false))
            startMainActivity(savedInstanceState);
        else
            startAuthActivity();
        finish();
    }

    //Separate main activity start
    public void startMainActivity(Bundle state){
        Intent mainAct = new Intent(this, HomeActivity.class);
        startActivity(mainAct);
    }

    //Separate authentication
    public void startAuthActivity(){
        Intent AuthAct = new Intent(this, AuthenticationActivity.class);
        startActivity(AuthAct);
    }
}
