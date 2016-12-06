package org.kurthen.myband;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Ref;

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

    private NewsFragment mNewsFragment;
    private CalendarFragment mCalendarFragment;
    private FinancesFragment mFinancesFragment;
    private ContactsFragment mContactsFragment;

    private FragmentManager mFragManager;

    private boolean uiWidgetsBuilt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set home layout
        setContentView(R.layout.activity_home);

        // Set toolbar on the top
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the action triggered by the floating 'plus' button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.plusButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar sb = Snackbar.make(view, "Neues Item", Snackbar.LENGTH_LONG);
                sb.setAction("Action", null);
                sb.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);

                    }
                });
            }
        });

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
        mMusicFragment = ContactFragment.newInstance();

        if(savedInstanceState != null){
            //savedInstanceState.getString("openedPage");
        }

        //Fetch user data
        ImageManager.getInstance().setContext(getApplicationContext());
        DatabaseConnection.getInstance().setContext(getApplicationContext());

        final Handler refreshCycle = new Handler();
        refreshCycle.post(new RefreshUIData());

        mBandsMenu = navigationView.getMenu().findItem(R.id.bands_menu_drawer).getSubMenu();
    }

    //Close the drawer on 'back' button
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        Band[] bands = CurrentProfile.getInstance().getBands();
        if(bands != null) {
            for (Band band : bands) {
                mBandsMenu.add(Menu.NONE, band.getId(), Menu.NONE, band.getName());
            }
            CurrentProfile.getInstance().selectBand(0);
        }

        // Start the NewsActivity at beginning
        fragmentSwitch(0);

        uiWidgetsBuilt = true;
        return true;
    }

    public class RefreshUIData implements Runnable{
        Handler refreshCycler;

        public RefreshUIData(){
            refreshCycler = new Handler();
        }

        @Override
        public void run(){
            CurrentProfile profile = CurrentProfile.getInstance();

            if(uiWidgetsBuilt) {
                mUsernameTextView.setText(profile.getUser().getFirstName());
                mEmailTextView.setText(profile.getUser().getEmail());

                mProfilePicture.setImageDrawable(profile.getUser().getPictureThumbnail());

                mNewsFragment.refreshList(profile.getUpdates());
                mCalendarFragment.refreshList(profile.getEvents());
                mFinancesFragment.refreshList(profile.getTransactions());
                mMusicFragment.refreshList(profile.getSongs());

                // Add bands that the user is not yet member of
                Band[] bands = profile.getBands();
                if(bands != null){
                    for (Band band : bands) {
                        if(mBandsMenu.findItem(band.getId()) == null){
                            mBandsMenu.add(Menu.NONE, band.getId(), Menu.NONE, band.getName());
                        }
                    }
                }
            }
            refreshCycler.postDelayed(this, 1000);
        }
    }

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_userprofile) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_add_band){

        } else{
            Band[] bands = CurrentProfile.getInstance().getBands();
            for(int i = 0; i < bands.length; i++){
                if(id == bands[i].getId()){
                    Log.d("STATUS", "Band " + bands[i].getName() + " got selected!");
                    CurrentProfile.getInstance().selectBand(i);
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        switch(fragNr){
            case 0: //News fragment
                selFragment = mNewsFragment;
                titleId = R.string.title_fragment_news;
                break;
            case 1:
                selFragment = mCalendarFragment;
                titleId = R.string.title_fragment_calendar;
                break;
            case 2:
                selFragment = mFinancesFragment;
                titleId = R.string.title_fragment_finances;
                break;
            case 3:
                selFragment = mMusicFragment;
                titleId = R.string.title_fragment_music;
                break;
            default:
                Log.d("ERROR", "Unknown fragment number");
                return;
        }

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(titleId);

        action.replace(R.id.fragment_container_home, selFragment);
        action.commit();
    }

    public void onFragmentInteraction(Uri uri){

    }


}
