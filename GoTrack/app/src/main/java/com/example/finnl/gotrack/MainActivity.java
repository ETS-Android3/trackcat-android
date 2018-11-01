package com.example.finnl.gotrack;

import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.finnl.gotrack.Recording.RecordFragment;
import com.example.finnl.gotrack.Recording.Timer;
import com.example.finnl.gotrack.Statistics.KmCounter;
import com.example.finnl.gotrack.Statistics.KmhAverager;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private KmCounter kmCounter;

    private Timer timer;
    private Timer rideTimer;
    private KmhAverager kmhAverager;

    private static MainActivity instance;
    public static Handler handler;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //------------------------------------------------------------------------------------------
        //-----------------------------------------------------------------------------------handler
        // recieves messages from another thread

        // handler recieves data from Timer Thread
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    //setTime((String) msg.obj);
                } else if (msg.what == 1) {
                    //setRideTime((String) msg.obj);
                }
            }
        };
        //##########################################################################################


        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()) {
                            case R.id.record:

                                if (getFragmentManager().findFragmentByTag("RECORD") == null) {
                                    FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();


                                    fragTransaction.replace(R.id.mainFrame, new RecordFragment(), "RECORD");
                                    fragTransaction.commit();
                                    return true;

                                }
                                break;


                        }

                        return true;
                    }
                });
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        //##########################################################################################
        //------------------------------------------------------------------------------------------




    }

    //##############################################################################################
    //----------------------------------------------------------------------------------------layout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    //##############################################################################################


    // get Location Update in this class
    //----------------------------------------------------------------------------------------------

    public void updateLocation(Location location) {
        // test View


        // add Distance
        kmCounter.addKm(location);

        // count ridetime
        if (!rideTimer.getActive() && location.getSpeed() > 0) {
            rideTimer.startTimer();
        } else if (rideTimer.getActive() && location.getSpeed() == 0) {
            rideTimer.killTimer();
        }

        kmhAverager.calcAvgSpeed();


    }
    //##############################################################################################


}