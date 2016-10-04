package org.kurthen.myband;

import android.net.Uri;
import android.os.Bundle;
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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    NewsFragment.OnNewsInteraction,
                    FinancesFragment.OnFinancesInteraction,
                    CalendarFragment.OnCalendarInteraction,
                    MusicFragment.OnMusicInteraction{

    private TextView mUsernameTextView;
    private TextView mEmailTextView;

    private Fragment mNewsFragment;
    private Fragment mCalendarFragment;
    private Fragment mFinancesFragment;
    private Fragment mMusicFragment;

    private FragmentManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Setup toolbar on the top
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup the action triggered by the floating 'plus' button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.plusButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        fragmentSwitch(0);

        if(savedInstanceState != null){
            //savedInstanceState.getString("openedPage");
        }

        DatabaseConnection.getInstance().receiveCurrentUserInformation();

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

        //Set current user in the navigation bar
        mUsernameTextView = (TextView) findViewById(R.id.usernameTextViewHome);
        mEmailTextView = (TextView) findViewById(R.id.emailTextViewHome);
        mUsernameTextView.setText(CurrentProfile.getInstance().getUser().getFirstName());
        mEmailTextView.setText(CurrentProfile.getInstance().getUser().getEmail());

        return true;
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
        } else if (id == R.id.nav_view) {

        } else if (id == R.id.nav_add_band){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fragmentSwitch(int fragNr){
        FragmentTransaction action = mFragManager.beginTransaction();
        Fragment newFragment;
        int titleId = 0;
        switch(fragNr){
            case 0: //News fragment
                newFragment = NewsFragment.newInstance();
                titleId = R.string.title_fragment_news;
                break;
            case 1:
                newFragment = CalendarFragment.newInstance();
                titleId = R.string.title_fragment_calendar;
                break;
            case 2:
                newFragment = FinancesFragment.newInstance();
                titleId = R.string.title_fragment_finances;
                break;
            case 3:
                newFragment = MusicFragment.newInstance();
                titleId = R.string.title_fragment_music;
                break;
            default:
                Log.d("ERROR", "Unknown fragment number");
                return;
        }

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(titleId);

        action.replace(R.id.contentView, newFragment);
        action.commit();
    }

    public void onFragmentInteraction(Uri uri){

    }
}
