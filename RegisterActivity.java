package org.kurthen.myband;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {


    private EditText mFirstName = (EditText) findViewById(R.id.editFirstName);
    private EditText mLastName = (EditText) findViewById(R.id.editLastName);
    private EditText mEmail = (EditText) findViewById(R.id.editEmail);
    private EditText mPassword = (EditText) findViewById(R.id.editPassword);

    private Button bRegister = (Button) findViewById(R.id.buttonReg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

    }
}
