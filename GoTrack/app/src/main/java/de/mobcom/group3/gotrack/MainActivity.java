package de.mobcom.group3.gotrack;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.mobcom.group3.gotrack.Dashboard.DashboardFragment;
import de.mobcom.group3.gotrack.Recording.RecordFragment;
import de.mobcom.group3.gotrack.Settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    final int NOTIFICATION_ID = 100;
    private DrawerLayout mDrawerLayout;
    private static MainActivity instance;
    private RecordFragment recordFragment;
    private NotificationManagerCompat notificationManager;

    private static final String PREF_DARK_THEME = "dark_theme";

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action != null && action.equalsIgnoreCase("RECORD")) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, "RECORD");
            fragTransaction.commit();
        } else if (action != null && action.equalsIgnoreCase("SETTINGS")) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), "SETTINGS");
            fragTransaction.commit();
        }
    }


    @Override
    protected void onDestroy() {
        /*
         * cancel Notification if App is closed
         * */
        notificationManager.cancel(getNOTIFICATION_ID());
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aktuelles Themes aus Einstellungen laden
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_DARK_THEME, false) ? R.style.AppTheme_Dark : R.style.AppTheme);

        // Startseite definieren
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Startseite festlegen - Erster Aufruf
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), "DASHBOARD");
        fragTransaction.commit();

        // Instanz für spätere Objekte speichern
        instance = this;
        recordFragment = new RecordFragment();
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Nav-Menu Listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView imgvw = hView.findViewById(R.id.nav_imgView);
        TextView tv = hView.findViewById(R.id.nav_txtView);
        imgvw.setImageResource(R.drawable.ic_launcher_background);
        tv.setText("Max Mustermann");

        // Listener Menü-Item
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Aktion je nach Auswahl des Items
                        switch (menuItem.getItemId()) {
                            case R.id.nav_record:
                                if (getSupportFragmentManager().findFragmentByTag("RECORD") == null) {
                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                                    fragTransaction.replace(R.id.mainFrame, recordFragment, "RECORD");
                                    fragTransaction.commit();

                                  /*  FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                                    //getFragmentManager().beginTransaction();

                                    fragTransaction.replace(R.id.mainFrame, new PageViewer(), "PageViewer");
                                    fragTransaction.commit();*/

                                    return true;
                                }
                                break;
                            case R.id.nav_settings:
                                if (getSupportFragmentManager().findFragmentByTag("SETTINGS") == null) {
                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                                    fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), "SETTINGS");
                                    fragTransaction.commit();
                                    return true;
                                }
                                break;
                            case R.id.nav_dashboard:
                                if (getSupportFragmentManager().findFragmentByTag("DASHBOARD") == null) {
                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                                    fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), "DASHBOARD");
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
        actionbar.setHomeAsUpIndicator(R.drawable.menu_burger);

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
        notificationManager = NotificationManagerCompat.from(this);

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

    /*
     * stops/pauses Tracking opens App and switch to RecordFragment
     * */
    public void stopTracking() {
        recordFragment.stopTracking();
        startActivity(getIntent());
        try {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, "RECORD");
            fragTransaction.commit();
        } catch (RuntimeException e) {

        }
    }

    public int getNOTIFICATION_ID() {
        return NOTIFICATION_ID;
    }

    public void startTracking() {
        recordFragment.startTracking();
        startActivity(getIntent());
        try {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, "RECORD");
            fragTransaction.commit();
        } catch (RuntimeException e) {

        }
    }

    /*
    * restart Record Fragment after Tracking is ended
    * */
    public void endTracking() {
        // TODO switch to Statisitcs page

        recordFragment = new RecordFragment();
    }
}