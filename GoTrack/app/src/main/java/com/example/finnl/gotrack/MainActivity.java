package com.example.finnl.gotrack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finnl.gotrack.Recording.RecordFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;


    private static MainActivity instance;

    private RecordFragment recordFragment;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /*
        --------------------------------------------------------------------------------------------

        --------------------------------------------------------------------------------------layout
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* save Instance for further Objects*/
        instance = this;

        recordFragment = new RecordFragment();


        mDrawerLayout = findViewById(R.id.drawer_layout);

        /*
         * set Listener on all Items of the Menu via @id/
         * */
        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView imgvw = hView.findViewById(R.id.nav_imgView);
        TextView tv = hView.findViewById(R.id.nav_txtView);
        imgvw.setImageResource(R.drawable.ic_launcher_background);
        tv.setText("Max Mustermann");

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
                            /*
                             * Open Record Fragment and track the User
                             * */
                            case R.id.record:

                                if (getSupportFragmentManager().findFragmentByTag("RECORD") == null) {
                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                                    //getFragmentManager().beginTransaction();

                                    fragTransaction.replace(R.id.mainFrame, new RecordFragment(), "RECORD");
                                    fragTransaction.commit();
                                    return true;

                                }
                                break;


                        }

                        return true;
                    }
                });

        /*
         * Menu stuff
         * */

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
        /*
        ###########################################################################################
        */

        /*
         * Play Button
         * */

        final ImageView playPause = (ImageView) findViewById(R.id.play_imageView);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordFragment.isTracking()) {
                } else {
                    if (getSupportFragmentManager().findFragmentByTag("RECORD") == null) {
                        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                        //getFragmentManager().beginTransaction();

                        fragTransaction.replace(R.id.mainFrame, recordFragment, "RECORD");
                        fragTransaction.commit();
                    }
                    recordFragment.startTracking();
                    playPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
            }
        });

    }

    /*
        ############################################################################################

        ----------------------------------------------------------------------------------layoutMenu
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*############################################################################################*/


}