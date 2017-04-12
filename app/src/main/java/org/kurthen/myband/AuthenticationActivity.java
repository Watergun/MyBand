package org.kurthen.myband;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
//public class AuthenticationActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,
public class AuthenticationActivity extends AppCompatActivity implements
        RegistrationFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    //private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction startRegistration = fragManager.beginTransaction();
        RegistrationFragment frag = RegistrationFragment.newInstance();
        startRegistration.add(R.id.authContainer, frag);
        startRegistration.commit();

        /* Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        */
        mProgressView = findViewById(R.id.login_progress);


    }
/*
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
*/

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    /*
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Intent homeAct = new Intent(this, HomeActivity.class);
            mAuthTask = new UserLoginTask(email, password, homeAct);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
*/
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        View loginForm = (View) findViewById(R.id.login_form);
        View registrationForm = (View) findViewById(R.id.registration_form);
        final View formView = (loginForm == null) ? registrationForm : loginForm;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AuthenticationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
*/
    public void onLogin(String email, String password){
        //Show progress in foreground
        showProgress(true);

        UserLoginTask login = new UserLoginTask(email, password);
        login.execute();
    }



    public void onRegistration(String name, String email, String password){
        //Show progress in foreground
        showProgress(true);

        UserRegistrationTask registration = new UserRegistrationTask(name, email, password);
        registration.execute();
    }

    /*
    Callback methods to switch between registration and login
     */
    public void onSwitchToRegistration(){
        FragmentTransaction startRegistration = getSupportFragmentManager().beginTransaction();
        startRegistration.replace(R.id.authContainer, RegistrationFragment.newInstance());
        startRegistration.commit();
    }

    public void onSwitchToLogin(){
        FragmentTransaction startLogin = getSupportFragmentManager().beginTransaction();
        startLogin.replace(R.id.authContainer, LoginFragment.newInstance());
        startLogin.commit();
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private String[] mCredentials;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            //Create an irreversible hash of this password
            mCredentials = AccountSecurity.logCredentials(mEmail, mPassword);

            //Ask the database if the information is correct
            String answer = NetworkAccess.performPostRequest("log_user.php",
                    "email=" + mCredentials[0] + "&pw=" + mCredentials[1]);

            return answer;
        }

        @Override
        protected void onPostExecute(final String status) {
            if(TextUtils.equals(status, "Unknown email")) {
                Log.d("STATUS", "Login failed");
                showProgress(false);
                EditText emailEntry = (EditText) findViewById(R.id.emailEntryLogin);
                emailEntry.setError("Email nicht registriert");
                emailEntry.requestFocus();
            }else if(TextUtils.equals(status, "Incorrect password")){
                Log.d("STATUS", "Login failed");
                showProgress(false);
                EditText passwordEntry = (EditText) findViewById(R.id.passwordEntryLogin);
                passwordEntry.setError("Inkorrekt");
                passwordEntry.requestFocus();
            }
            else if(status.startsWith("Success-")){
                Log.d("STATUS", "Logged in successfully");

                if(status.split("-").length != 2){
                    Log.d("STATUS", "Unexpected response format");
                    showProgress(false);
                    return;
                }
                String token = status.split("-")[1];
                Log.d("PREFS", "Received authentication token: " + token);

                //Set current user as local default user
                SharedPreferences localStatus = getSharedPreferences("local_instance", MODE_PRIVATE);
                SharedPreferences.Editor editor = localStatus.edit();
                editor.putBoolean(getString(R.string.local_toggle), true);
                editor.putString(getString(R.string.local_key), mCredentials[0]);
                editor.putString("password", mCredentials[1]);
                editor.putString("token", token);
                editor.commit();

                CurrentProfile.getInstance().setUserCredentials(mCredentials[0], mCredentials[1]);
                CurrentProfile.getInstance().setAuthToken(token);

                //Switch to the HomeActivity
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
            else{
                showProgress(false);
                Log.d("STATUS", "Unexpexted server response");
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class UserRegistrationTask extends AsyncTask<Void, Void, String>{
        private String mEmail;
        private String mPassword;
        private String mName;
        private String[] mCredentials;

        public UserRegistrationTask(String name, String email, String password){
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params){
            //
            mCredentials = AccountSecurity.logCredentials(mEmail, mPassword);

            // Post user data on server
            String answer = NetworkAccess.performPostRequest("registration.php",
                    "name=" + mName + "&email=" + mCredentials[0] + "&pw=" + mCredentials[1]);

            return answer;
        }

        @Override
        protected void onPostExecute(String status){
            if(TextUtils.equals(status, "Email already registered")){
                Log.d("STATUS", "Registration failed");
                EditText emailEntry = (EditText) findViewById(R.id.emailEntryRegister);
                emailEntry.setError("Email already in use");
                emailEntry.requestFocus();
            }
            else if(status.startsWith("Success")) {
                Log.d("STATUS", "Registered successfully");

                //Receive authentication token
                if(status.split("-").length != 2){
                    Log.d("ERROR", "Invalid success response in registration communication");
                    return;
                }

                String token = status.split("-")[1];

                //Set current user as local default user
                SharedPreferences localStatus = getSharedPreferences("local_instance", MODE_PRIVATE);
                SharedPreferences.Editor editor = localStatus.edit();
                editor.putBoolean(getString(R.string.local_toggle), true);
                editor.putString(getString(R.string.local_key), mCredentials[0]);
                editor.putString("password", mCredentials[1]);
                editor.putString("token", token);
                editor.commit();

                //Save user information in class
                CurrentProfile.getInstance().setUserCredentials(mName, mCredentials[0], mCredentials[1]);
                CurrentProfile.getInstance().setAuthToken(token);

                //Switch to the HomeActivity
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}

