package org.kurthen.myband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Test;

import java.sql.Ref;
import java.util.concurrent.Executor;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    NewsFragment.OnNewsInteraction,
                    FinancesFragment.OnFinancesInteraction,
                    CalendarFragment.OnCalendarInteraction,
                    ContactsFragment.OnContactsInteraction{

    private TextView mUsernameTextView;
    private TextView mEmailTextView;

    private ImageView mProfilePicture;
    private Menu mBandsMenu;
    private MenuItem[] mBandsMenuItems;

    private NewsFragment mNewsFragment;
    private CalendarFragment mCalendarFragment;
    private FinancesFragment mFinancesFragment;
    private ContactsFragment mContactsFragment;

    private FragmentManager mFragManager;

    private int mCurrentFragment = 0;
    private boolean mAddItemView = false;

    private Handler mUiHandler;
    private Handler mSyncHandler;

    private boolean uiWidgetsBuilt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set home layout
        setContentView(R.layout.activity_home);

        // Set toolbar on the top
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup navigation drawer to the left
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Bind the bottom menu bar to its fragments
        ImageView newsLogo = (ImageView) findViewById(R.id.newsImageView);
        newsLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(0);
            }
        });
        ImageView calendarView = (ImageView) findViewById(R.id.calendarImageView);
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(1);
            }
        });
        ImageView financesView = (ImageView) findViewById(R.id.financesImageView);
        financesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(2);
            }
        });
        ImageView musicView = (ImageView) findViewById(R.id.musicImageView);
        musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(3);
            }
        });

        //Initialize the fragment manager and start with the newspage (Page 0)
        mFragManager = getSupportFragmentManager();

        mNewsFragment = NewsFragment.newInstance();
        mCalendarFragment = CalendarFragment.newInstance();
        mFinancesFragment = FinancesFragment.newInstance();
        mContactsFragment = ContactsFragment.newInstance();

        if(savedInstanceState != null){
            //savedInstanceState.getString("openedPage");
        }

        // Check authentication token (gets executed on every start)
        // If no connection is possible,
        AuthConfiguration tokenConfig = new AuthConfiguration(
                CurrentProfile.getInstance().getToken(),
                CurrentProfile.getInstance().getUser().getEmail(),
                CurrentProfile.getInstance().getUser().getPassword());
        tokenConfig.execute();

        mUiHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == R.integer.MSG_REFRESH){
                    this.post(mRefreshUiTask);
                }
                super.handleMessage(msg);
            }
        };

        //Fetch user data
        ImageManager.getInstance().setContext(getApplicationContext());
        DatabaseConnection.getInstance().setContext(getApplicationContext());
        mSyncHandler = DatabaseConnection.getInstance().startNetworking(mUiHandler);

        mBandsMenu = (Menu) navigationView.getMenu().findItem(R.id.bands_menu_drawer).getSubMenu();
    }

    @Override
    public void onBackPressed() {

        // Close the drawer on 'back' button if opened
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        // In the case that the user is currently viewing the "Add ... " Fragment, a return to
        // the original fragment has to be done
        else if(mAddItemView) {
            // Poop the latest transaction off the stack
            mFragManager.popBackStack();

            // Change this flag to false
            mAddItemView = false;

            // Make the ADD-Button visible again
            findViewById(R.id.home_add_button).setVisibility(View.VISIBLE);
        }

        // If the user is in the home screen, the app just pauses
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        mUsernameTextView = (TextView) findViewById(R.id.usernameTextViewHome);
        mEmailTextView = (TextView) findViewById(R.id.emailTextViewHome);

        mProfilePicture = (ImageView) findViewById(R.id.personalImageView);

        /* This initializing sequence has partly the same code as the mRefreshUiTask
         * So a call to >> mRefreshUiTask.run(); << would be sufficient to build up the drawer menu
         *
         * Only problem is, that the auto-selection of the first band has to be implemented in
         *
         */
        /*
        Band[] bands = CurrentProfile.getInstance().getBands();
        if(bands != null) {
            for (int i = 0; i < bands.length; i++) {
                MenuItem m = mBandsMenu.add(Menu.NONE, bands[i].getId(), Menu.NONE, bands[i].getName());

                // Select this band physically to enable highlighting for the auto-selected band
                if(i == 0)
                    onNavigationItemSelected(m);
            }
            if(bands.length > 0)
                CurrentProfile.getInstance().selectBand(0);
        }
        */
        mRefreshUiTask.run();

        // Start the NewsActivity at beginning
        fragmentSwitch(0);

        uiWidgetsBuilt = true;
        return true;
    }

    /* UI Refresh task
     * # Precondition: All new information is also saved in the CurrentProfile singleton
     * # Postcondition: All new information is now (partially) displayed in the respective fragments
     * A band-dependent fitering has to be done
     */
    public Runnable mRefreshUiTask = new Runnable() {

        @Override
        public void run(){

            Log.d("SYNCSTAT", "Refreshing UI...");

            CurrentProfile profile = CurrentProfile.getInstance();

            if(uiWidgetsBuilt) {
                mUsernameTextView.setText(profile.getUser().getFirstName());
                mEmailTextView.setText(profile.getUser().getEmail());

                mProfilePicture.setImageDrawable(profile.getUser().getPictureThumbnail());

                Band[] bands = profile.getBands();
                if(bands != null && bands.length > 0){
                    // Initialize bands menu items (the band labels)
                    if(mBandsMenuItems == null || mBandsMenuItems.length < bands.length)
                        mBandsMenuItems = new MenuItem[bands.length];

                    // Add the bands to the drawer menu
                    for (int i = 0; i < bands.length; i++) {
                        if(mBandsMenu.findItem(bands[i].getId()) == null){
                            mBandsMenuItems[i] = mBandsMenu.add(R.id.bands_menu_item_group,
                                                                bands[i].getId(),
                                                                Menu.NONE, bands[i].getName());
                            mBandsMenuItems[i].setCheckable(true);
                        }
                    }

                    //Make band items mutually checkable
                    mBandsMenu.setGroupCheckable(R.id.bands_menu_item_group, true, true);

                    // Auto-select the first band
                    if(profile.getSelectedBand() == null) {
                        onNavigationItemSelected(mBandsMenuItems[0]);
                    }
                }

                mNewsFragment.refreshList(profile.getUpdates());
                mCalendarFragment.refreshList(profile.getEvents());
                mFinancesFragment.refreshList(profile.getTransactions());
                mContactsFragment.refreshList(profile.getContacts());

                // Add bands that the user is not yet member of


            }
            //mSyncTaskHandler.postDelayed(this, 1000);
            Log.d("SYNCSTAT", "UI refreshed.");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_userprofile) {

        } else if (id == R.id.nav_add_band) {

        } else {
            Band[] bands = CurrentProfile.getInstance().getBands();
            for (int i = 0; i < bands.length; i++) {
                if (id == bands[i].getId()) {
                    Log.d("STATUS", "Band " + bands[i].getName() + " got selected!");
                    CurrentProfile.getInstance().selectBand(i);

                    mBandsMenuItems[i].setChecked(true);
                }
            }
        }

        mUiHandler.sendEmptyMessage(R.integer.MSG_REFRESH);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onAddUpdateClicked(View v){
        FragmentTransaction action = mFragManager.beginTransaction();

        switch(mCurrentFragment){
            case 0: //News Fragment
                action.detach(mNewsFragment);
                action.add(R.id.fragment_container_home, new AddUpdateFragment());
                break;
            case 1: //Calendar Fragment
                action.detach(mCalendarFragment);
                action.add(R.id.fragment_container_home, new AddEventFragment());
                break;
            case 2: //Finances Fragment
                action.detach(mFinancesFragment);
                action.add(R.id.fragment_container_home, new AddTransactionFragment());
                break;
            case 3: //Contacts Fragment
                action.detach(mContactsFragment);
                action.add(R.id.fragment_container_home, new AddContactFragment());
                break;
            default:
                return;
        }
        action.addToBackStack("addItem");
        action.commit();

        mAddItemView = true;

        ImageButton plusButton = (ImageButton) findViewById(R.id.home_add_button);
        plusButton.setVisibility(View.INVISIBLE);


    }

    public void logout(){
        SharedPreferences sp = getSharedPreferences("local_instance", MODE_PRIVATE);
        sp.edit().putBoolean(getString(R.string.local_toggle), false).commit();

        CurrentProfile.getInstance().resetUser();

        Intent authIntent = new Intent(this, AuthenticationActivity.class);
        startActivity(authIntent);
        finish();
    }

    public void fragmentSwitch(int fragNr){
        FragmentTransaction action = mFragManager.beginTransaction();
        Fragment selFragment;
        int titleId = 0;
        LinearLayout bottom_menu = (LinearLayout) findViewById(R.id.tab_menu_home);

        switch(fragNr){
            case 0: //News fragment
                selFragment = mNewsFragment;
                focusTab(0, bottom_menu);
                mCurrentFragment = 0;
                titleId = R.string.title_fragment_news;
                break;
            case 1:
                selFragment = mCalendarFragment;
                focusTab(1, bottom_menu);
                mCurrentFragment = 1;
                titleId = R.string.title_fragment_calendar;
                break;
            case 2:
                selFragment = mFinancesFragment;
                focusTab(2, bottom_menu);
                mCurrentFragment = 2;
                titleId = R.string.title_fragment_finances;
                break;
            case 3:
                selFragment = mContactsFragment;
                focusTab(3, bottom_menu);
                mCurrentFragment = 3;
                titleId = R.string.title_fragment_contacts;
                break;
            default:
                Log.d("ERROR", "Unknown fragment number");
                return;
        }

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(titleId);

        action.replace(R.id.fragment_container_home, selFragment);
        action.commit();

        findViewById(R.id.home_add_button).setVisibility(View.VISIBLE);
    }

    public void onFragmentInteraction(Uri uri){

    }

    /*
    Changes the respecting colors of the chosen tab in the bottom menu
     */
    public void focusTab(int index, LinearLayout tabMenu){
        if(tabMenu == null ||tabMenu.getChildCount() != 4 || index > 3)
            return;

        LinearLayout[] tabs = new LinearLayout[4];
        for(int i = 0; i < 4; i++)
            tabs[i] = (LinearLayout) tabMenu.getChildAt(i);

        int light_color = ResourcesCompat.getColor(getResources(), R.color.light, null);
        int dark_color = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);

        for(LinearLayout tab : tabs){
            ImageView iconView = (ImageView) tab.getChildAt(0);
            TextView titleView = (TextView) tab.getChildAt(1);

            tab.setBackgroundColor(light_color);
            iconView.setColorFilter(dark_color);
            titleView.setTextColor(dark_color);
        }

        tabs[index].setBackgroundColor(dark_color);
        ((ImageView)tabs[index].getChildAt(0)).setColorFilter(light_color);
        ((TextView)tabs[index].getChildAt(1)).setTextColor(light_color);
    }

    @Override
    public void onDestroy(){
        DatabaseConnection.getInstance().stopNetworking();
        super.onDestroy();
    }

    /*
    This asynchronously executed task configures the start-up token configuration of the user.
    By default, the following steps are done:
        -   Check validity of existing token (which can be null)
        -   If the token is invalid: request a new token (doesn't have to be checked again)
        -   If the token is valid, nothing has to be done in extra

     */
    public class AuthConfiguration extends AsyncTask<Void, Void, Boolean>{
        String mToken;
        String mEmail;
        String mPw;

        public AuthConfiguration(String token, String email, String pw){
            mToken = token;
            mEmail = email;
            mPw = pw;
        }

        public Boolean doInBackground(Void... params){
            Log.d("ASYNC", "Starting config");

            String response = NetworkAccess.performPostRequest("log_token.php", "t=" + mToken);

            // Check if token is marked as invalid
            if(TextUtils.equals(response, "Invalid token")){

                // Renew the authentication token by providing the email and password of the user
                response = NetworkAccess.performPostRequest("log_user.php",
                        "email=" + mEmail + "&pw=" + mPw);


                if(response.startsWith("Success-")){
                    // The new token must be saved locally now
                    response = response.replace("Success-", "");
                    SharedPreferences sp = getSharedPreferences("local_instance", MODE_PRIVATE);
                    sp.edit().putString("token", response).commit();
                    CurrentProfile.getInstance().setAuthToken(response);

                    Log.d("STATUS", "New token received: " + response);
                    return true;
                }
                else if(TextUtils.equals(response, "Unknown email") ||
                        TextUtils.equals(response, "Incorrect password")){
                    // In this case, the login credentials were obviously wrong
                    // and the user has to login again
                    Log.d("STATUS", "Login credentials wrong! Restart authentication activity...");

                    // Start the authentication activity
                    Intent authIntent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                    startActivity(authIntent);

                    // The home activity stops right here
                    finish();
                    return false;
                }
                else{
                    // In any other cases, a server error has occured.
                    return false;
                }
            }
            else if(TextUtils.equals(response, "Valid token")){
                // Now it's a green light for any synchronization methods
                // - including the notification service
                return true;
            }

            // Something went wrong (Server script error or connection error)
            return false;
        }


        public void onPostExecute(boolean configured){
            if(configured){
                Log.d("STATUS", "Successfully configured");
            }
            else{
                Log.d("STATUS", "Sunsuccesful configuration");
            }
        }
    }


}
