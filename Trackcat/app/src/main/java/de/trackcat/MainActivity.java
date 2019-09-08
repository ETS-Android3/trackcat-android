package de.trackcat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.trackcat.CustomElements.RecordModelForServer;
import de.trackcat.Dashboard.DashboardFragment;
import de.trackcat.Database.DAO.LocationTempDAO;
import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendLiveFragment;
import de.trackcat.FriendsSystem.FriendsViewerFragment;
import de.trackcat.Profile.DeleteAccountFragment;
import de.trackcat.Profile.EditPasswordFragment;
import de.trackcat.Profile.ProfileFragment;
import de.trackcat.Profile.EditProfileFragment;
import de.trackcat.RecordList.RecordListFragment;
import de.trackcat.Recording.Locator;
import de.trackcat.Recording.RecordFragment;
import de.trackcat.Settings.SettingsFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private PermissionManager permissionManager = new PermissionManager() {
    };
    final int NOTIFICATION_ID = 100;
    private DrawerLayout mainDrawer;
    private ImageView showHelp, profileImage;
    private TextView profileName, profileEmail;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private static MainActivity instance;
    private RecordFragment recordFragment;
    private NotificationManagerCompat notificationManager;
    private static boolean hints;
    private static boolean darkTheme;
    private static String searchFriendTerm;
    private static int searchFriendPage;
    private static int searchFriendPageIndex;
    private static int friendQuestionIndex;
    private static int sendFriendQuestionIndex;
    private static String searchForeignTerm;
    private static int searchForeignPage;
    private static int searchForeignPageIndex;
    private UserDAO userDAO;
    private static int activeUser;
    public static Boolean isActiv = false;
    private static Menu menuInstance;
    private ProgressDialog progressDialog;
    private static boolean connected, restarted;

    /* Random value for header image */
    public static int randomImg = (int) (Math.random() * ((13 - 0) + 1)) + 0;

    /* Restart activity for Theme Switching */
    public static void restart() {
        Intent intent = getInstance().getIntent();
        getInstance().finish();
        getInstance().startActivity(intent);
        restarted = true;
    }

    /* Getter and Setter */
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static Menu getMenuInstance() {
        return menuInstance;
    }

    public static boolean getHints() {
        return hints;
    }

    public static void setHints(boolean activeHints) {
        hints = activeHints;
    }

    public static void setSendFriendQuestionIndex(int index) {
        sendFriendQuestionIndex = index;
    }

    public static int getSendFriendQuestionIndex() {
        return sendFriendQuestionIndex;
    }

    public static void setFriendQuestionIndex(int index) {
        friendQuestionIndex = index;
    }

    public static int getFriendQuestionIndex() {
        return friendQuestionIndex;
    }

    public static void setSearchForeignTerm(String term) {
        searchForeignTerm = term;
    }

    public static String getSearchForeignTerm() {
        return searchForeignTerm;
    }

    public static void setSearchForeignPage(int page) {
        searchForeignPage = page;
    }

    public static int getSearchForeignPage() {
        return searchForeignPage;
    }

    public static void setSearchForeignPageIndex(int index) {
        searchForeignPageIndex = index;
    }

    public static int getSearchForeignPageIndex() {
        return searchForeignPageIndex;
    }

    public static void setSearchFriendTerm(String term) {
        searchFriendTerm = term;
    }

    public static String getSearchFriendTerm() {
        return searchFriendTerm;
    }

    public static void setSearchFriendPage(int page) {
        searchFriendPage = page;
    }

    public static int getSearchFriendPage() {
        return searchFriendPage;
    }

    public static void setSearchFriendPageIndex(int index) {
        searchFriendPageIndex = index;
    }

    public static int getSearchFriendPageIndex() {
        return searchFriendPageIndex;
    }

    public static boolean getDarkTheme() {
        return darkTheme;
    }

    public static int getActiveUser() {
        return activeUser;
    }

    public static void setDarkTheme(boolean activeDarkTheme) {
        darkTheme = activeDarkTheme;
    }

    public static boolean getConnection() {
        return connected;
    }


    public static void setConnection(boolean connection) {
        connected = connection;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fRecord)) && getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {
            loadRecord();
        } else if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fSettings))) {
            loadSettings();
        }
    }

    @Override
    protected void onDestroy() {
        /* Remove notification, if app destroy */
        notificationManager.cancel(getNOTIFICATION_ID());
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        try {
            recordFragment.stopTimer();
            recordFragment = null;

        } catch (NullPointerException e) {
        }
        this.stopService(new Intent(this, Locator.class));

        isActiv = false;

        try {
            stopTracking();
        } catch (NullPointerException e) {
        }
        super.onDestroy();
    }

    @Override
    public void recreate() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    /* Function to get active user information */
    public void getCurrentUserInformation() {
        userDAO = new UserDAO(this);
        User currentUser = userDAO.readCurrentUser();
        hints = currentUser.isHintsActive();
        darkTheme = currentUser.isDarkThemeActive();
        activeUser = currentUser.getId();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Register ConnectivityChecker ---> alert on connectivity change */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityChecker cc = new ConnectivityChecker();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(cc, filter);
        }

        /* Turn off power saving and battery optimization */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
        }

        startService(new Intent(getBaseContext(), ClosingService.class));
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        /* Check Permissions --> if not okay logout user */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            logout();
        }

        /* Get current user information */
        getCurrentUserInformation();

        /* Load theme */
        setTheme(getDarkTheme() ? R.style.AppTheme_Dark : R.style.AppTheme);

        if (getIntent().hasExtra("bundle") && savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }

        /* Define startpage */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Save instances for later */
        instance = this;
        recordFragment = new RecordFragment();
        mainDrawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        showHelp = findViewById(R.id.showHelp);

        /* OnClick Listener */
        showHelp.setOnClickListener(this);

        /* Define actionbar and MenuListener */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        /* Show menu, if you click on profile */
        profileEmail = headerView.findViewById(R.id.profile_email);
        profileName = headerView.findViewById(R.id.profile_name);
        profileImage = headerView.findViewById(R.id.profile_image);
        profileEmail.setOnClickListener(this);
        profileName.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        /* Choose header */
        LinearLayout header_img = headerView.findViewById(R.id.header_img);
        int month = Calendar.getInstance().getTime().getMonth() + 1;
        switch (randomImg) {
            case 1:
                header_img.setBackgroundResource(R.raw.bg_january);
                break;
            case 2:
                header_img.setBackgroundResource(R.raw.bg_february);
                break;
            case 3:
                header_img.setBackgroundResource(R.raw.bg_march);
                break;
            case 4:
                header_img.setBackgroundResource(R.raw.bg_april);
                break;
            case 5:
                header_img.setBackgroundResource(R.raw.bg_may);
                break;
            case 6:
                header_img.setBackgroundResource(R.raw.bg_june);
                break;
            case 7:
                header_img.setBackgroundResource(R.raw.bg_july);
                break;
            case 8:
                header_img.setBackgroundResource(R.raw.bg_august);
                break;
            case 9:
                header_img.setBackgroundResource(R.raw.bg_september);
                break;
            case 10:
                header_img.setBackgroundResource(R.raw.bg_october);
                break;
            case 11:
                header_img.setBackgroundResource(R.raw.bg_november);
                break;
            case 12:
                header_img.setBackgroundResource(R.raw.bg_december);
                break;
        }
        getProfileColor();

        /* Set drawer information */
        User currentUser = userDAO.read(activeUser);
        setDrawerInfromation(currentUser.getImage(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getMail());
        synchronizeUser(currentUser);

        /* Menu Toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawer.addDrawerListener(toggle);
        toggle.syncState();

        notificationManager = NotificationManagerCompat.from(this);

        /* Check start connectivity */
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            Log.v(getResources().getString(R.string.app_name) + "-ConnectedListener", String.valueOf(connected));
        } else {
            connected = false;
            Log.v(getResources().getString(R.string.app_name) + "-ConnectedListener", String.valueOf(connected));
        }
        setConnection(connected);

        /* Load settings if them changed */
        if (restarted) {
            loadSettings();
        } else {
            synchronizeRecords(false, false);
        }
    }

    /* Set profile information */
    public void setDrawerInfromation(byte[] imgRessource, String first_name, String last_name, String email) {

        profileName.setText(first_name + " " + last_name);
        profileEmail.setText(email);

        /* Set image */
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.default_profile);
        if (imgRessource != null && imgRessource.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
        }
        profileImage.setImageBitmap(bitmap);
    }

    /* Backgroundcolor for header image */
    public void getProfileColor() {
        int month = Calendar.getInstance().getTime().getMonth() + 1;
        int defaultColor = Color.WHITE;
        switch (MainActivity.randomImg) {
            case 8:
            case 9:
            case 10:
            case 12:
                defaultColor = Color.BLACK;
                break;
        }
        profileEmail.setTextColor(defaultColor);
        profileName.setTextColor(defaultColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInstance = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* OptionItems Listener */
        switch (item.getItemId()) {

            case R.id.nav_editProfile:
                loadEditProfile();
                return true;
            case R.id.nav_editPassword:
                loadEditPassword(true);
                return true;
            case R.id.nav_deleteAccount:
                loadDeleteAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        /* Menu options */
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:

                Fragment fragmentDashboard = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard));
                if (fragmentDashboard == null || (fragmentDashboard != null && !fragmentDashboard.isVisible())) {
                    toolbar.getMenu().clear();
                    synchronizeRecords(false, false);
                    clearValuesAfterChangeMenu();
                }

                break;
            case R.id.nav_recordlist:

                Fragment fragmentRecordList = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist));
                if (fragmentRecordList == null || (fragmentRecordList != null && !fragmentRecordList.isVisible())) {
                    menuInstance.clear();
                    synchronizeRecords(true, false);
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_record:
                Fragment fragmentRecord = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord));
                if (fragmentRecord == null || (fragmentRecord != null && !fragmentRecord.isVisible())) {
                    menuInstance.clear();
                    loadRecord();
                    clearValuesAfterChangeMenu();

                }
                break;
            case R.id.nav_settings:

                Fragment fragmentSettings = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings));
                if (fragmentSettings == null || (fragmentSettings != null && !fragmentSettings.isVisible())) {
                    menuInstance.clear();
                    loadSettings();
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_friends:
                Fragment fragmentFriends = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem));
                if (fragmentFriends == null || (fragmentFriends != null && !fragmentFriends.isVisible())) {
                    menuInstance.clear();
                    loadFriendSystem(1);
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_logout:

                logout();
                break;
        }
        menuItem.setChecked(true);
        mainDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Function to clear values */
    public void clearValuesAfterChangeMenu() {
        setSearchForeignPage(0);
        setSearchForeignTerm(null);
        setSearchForeignPageIndex(0);
        setSearchFriendPage(0);
        setSearchFriendTerm("");
        setSearchFriendPageIndex(0);
        setFriendQuestionIndex(0);
        setSendFriendQuestionIndex(0);
        try {
            FriendLiveFragment.resetHandler();
        } catch (Exception e) {
        }
    }

    /* Function to logout */
    public void logout() {
        /* Set wait field */
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Abmeldung...");
        progressDialog.show();

        /* Set waiting handler */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        finish();

                        /* Remove user from local db */
                        List<User> deletedUsers = userDAO.readAll();
                        for (User user : deletedUsers) {
                            userDAO.delete(user);
                        }

                        /* Open login fragment */
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        intent.putExtra("isLogout", true);

                        startActivity(intent);
                    }
                }, 3000);
    }

    /* Stops/pauses Tracking opens App and switch to RecordFragment */
    public void stopTracking() {
        try {
            loadRecord();
        } catch (RuntimeException e) {
            Log.v("Fehler beim Stoppen: ", e.toString());
        }
        recordFragment.stopTracking();
    }

    public int getNOTIFICATION_ID() {
        return NOTIFICATION_ID;
    }

    public void startTracking() {
        recordFragment.startTracking();
        try {
            loadRecord();
        } catch (RuntimeException e) {
        }
    }

    /* Started RecordFragment after finish tracking */
    public void endTracking() {
        recordFragment = new RecordFragment();
        loadRecord();
    }

    public RecordFragment getRecordFragment() {
        return recordFragment;
    }

    /* BackPressed Listener */
    private boolean exitApp = false;

    @Override
    public void onBackPressed() {

        /* Go page back */
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 1) {
            int index = this.getSupportFragmentManager().getBackStackEntryCount() - 2;
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            this.getSupportFragmentManager().popBackStack();

            if (tag == getResources().getString(R.string.fProfile)) {
                this.getSupportFragmentManager().popBackStack();
                loadProfile(false);
            } else {
                fragTransaction.replace(R.id.mainFrame, fragment, tag);
                fragTransaction.commit();
            }

        } else {

            /* Close app with click double back */
            if (exitApp) {

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    RecordTempDAO recordTempDAO = new RecordTempDAO(MainActivity.getInstance());
                    recordTempDAO.deleteAllNotFinished();
                    finish();
                }
            }
            exitApp = true;
            if (hints) {
                Toast.makeText(instance, "Noch einmal klicken, um App zu beenden!", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                    if (hints) {
                        Toast.makeText(instance, "Zu langsam. Versuche es erneut...", Toast.LENGTH_LONG).show();
                    }
                }
            }, 3000);
        }
    }

    /* Load Dashboard-Fragments */
    public void loadDashboard(boolean addToStack) {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Dashboard-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(),
                getResources().getString(R.string.fDashboard));
        fragTransaction.commit();

        /* Add to Stack */
        if (addToStack) {
            fragTransaction.addToBackStack(getResources().getString(R.string.fDashboard));
        }
    }

    /* Load Record-Fragments */
    public void loadRecord() {

        /* Check permission - additionally security */
        permissionManager.checkAndRequestPermissions(MainActivity.getInstance());

        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getInstance().checkCallingOrSelfPermission(permission);

        String permissionGPS = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int resGPS = getInstance().checkCallingOrSelfPermission(permissionGPS);

        if (res == PackageManager.PERMISSION_GRANTED && resGPS == PackageManager.PERMISSION_GRANTED) {

            Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Aufnahme-Fragment wird geladen.");
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment,
                    getResources().getString(R.string.fRecord));
            fragTransaction.commit();

            /* Add to Stack */
            fragTransaction.addToBackStack(getResources().getString(R.string.fRecord));
        }
    }

    /* Load list-fragments */
    public void loadRecordList(boolean addToStack) {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Listen-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new RecordListFragment(),
                getResources().getString(R.string.fRecordlist));
        fragTransaction.commit();

        /* Add to Stack */
        if (addToStack) {
            fragTransaction.addToBackStack(getResources().getString(R.string.fRecordlist));
        }
    }

    /* Load profile-fragments */
    public void loadProfile(boolean loadMenu) {

        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Profil-Fragment wird geladen.");

        /* Set Bundle */
        Bundle bundleSpeed = new Bundle();
        bundleSpeed.putBoolean("loadMenu", loadMenu);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundleSpeed);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, profileFragment,
                getResources().getString(R.string.fProfile));
        fragTransaction.commit();

        /* Set menu checked on false */
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_dashboard).setChecked(false);
        menu.findItem(R.id.nav_record).setChecked(false);
        menu.findItem(R.id.nav_recordlist).setChecked(false);
        menu.findItem(R.id.nav_settings).setChecked(false);
        menu.findItem(R.id.nav_friends).setChecked(false);

        /* Add to Stack */
        fragTransaction.addToBackStack(getResources().getString(R.string.fProfile));
    }

    /* Load setting-fragments */
    public void loadSettings() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Einstellung-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new SettingsFragment(),
                getResources().getString(R.string.fSettings));
        fragTransaction.commit();

        /* Add to Stack */
        fragTransaction.addToBackStack(getResources().getString(R.string.fSettings));
    }

    /* Load editProfile-fragment */
    public void loadEditProfile() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Profil-Bearbeiten-Fragment wird geladen.");

        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new EditProfileFragment(),
                getResources().getString(R.string.fEditProfile));
        fragTransaction.commit();

        /* Add to Stack */
        fragTransaction.addToBackStack(getResources().getString(R.string.fEditProfile));
    }

    /* Load editPassword-fragment */
    public void loadEditPassword(boolean addToStack) {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Passwort-Ändern-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new EditPasswordFragment(),
                getResources().getString(R.string.fEditPassword));
        fragTransaction.commit();

        /* Add to Stack */
        if (addToStack) {
            fragTransaction.addToBackStack(getResources().getString(R.string.fEditPassword));
        }
    }

    /* Load friends-fragments */
    public void loadFriendSystem(int activeSite) {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Freunde-Fragment wird geladen.");
        Bundle bundle = new Bundle();
        bundle.putInt("activeSite", activeSite);

        FriendsViewerFragment firendsFragment = new FriendsViewerFragment();
        firendsFragment.setArguments(bundle);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, firendsFragment,
                getResources().getString(R.string.fFriendSystem));
        fragTransaction.commit();

        /* Add to Stack */
        fragTransaction.addToBackStack(getResources().getString(R.string.fFriendSystem));
    }

    /* Load deleteFriend-fragments */
    public void loadDeleteAccount() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Account-Löschen-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DeleteAccountFragment(),
                getResources().getString(R.string.fDeleteAccount));
        fragTransaction.commit();

        /* Add to Stack */
        fragTransaction.addToBackStack(getResources().getString(R.string.fDeleteAccount));
    }

    /* Set the RecordFragment wich is in use */
    public void setRecordFragment(RecordFragment recordFragment) {
        this.recordFragment = recordFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showHelp:

                /* Show help */
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.getInstance());
                alert.setTitle("Hilfe");

                Fragment fragment;
                if (MainActivity.getHints()) {
                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_dashboard));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_record));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_record_list));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_settings));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetails)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetails));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_record_details));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_profile));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_editProfile));
                        }
                    }
                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_editPassword));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_friendSystem));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDeleteAccount)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDeleteAccount));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_deleteAccount));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendProfile)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendProfile));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_friends_profile));
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendLiveView)) != null) {
                        fragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendLiveView));
                        if (fragment.isVisible()) {
                            alert.setMessage(getResources().getString(R.string.help_friends_live_view));
                        }
                    }

                    alert.setNegativeButton("Schließen", null);
                    alert.show();
                } else {
                    Toast.makeText(instance, "Diese Funktion muss zunächst in den Einstellungen aktiviert werden!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.profile_email:
            case R.id.profile_image:
            case R.id.profile_name:
                Fragment fragmentProfile = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile));
                if (fragmentProfile == null || (fragmentProfile != null && !fragmentProfile.isVisible())) {
                    menuInstance.clear();
                    loadProfile(true);
                }
                mainDrawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

    /* Function check if device have network connection */
    public void networkChange(boolean connected) {
        Log.v(getResources().getString(R.string.app_name) + "-ConnectedListener", String.valueOf(connected));
        setConnection(connected);

        /* Device have connection */
        if (connected) {

            User currentUser = userDAO.read(activeUser);
            synchronizeUser(currentUser);
            synchronizeOfflineRoutes(currentUser);

            /* Synchronize records */
            Fragment fragmentDashboard = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard));
            Fragment fragmentRecordList = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist));

            if (fragmentDashboard.isVisible()) {
                synchronizeRecords(false, false);
            }
            if (fragmentRecordList.isVisible()) {
                synchronizeRecords(true, false);
            }
            if (!fragmentRecordList.isVisible() && !fragmentDashboard.isVisible()) {
                synchronizeRecords(false, true);
            }
        }
    }

    /* Function to show not autorized modal */
    int showAutorizeCounter = 0;

    public void showNotAuthorizedModal(int type) {
        if (type == 5 | type == 6 | type == 7 | type == 8 | type == 9) {
            showAutorizeCounter++;
        } else {
            showAutorizeCounter = 1;
        }

        if (showAutorizeCounter == 1) {

            /* Create AlertBox */
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Achtung");
            LayoutInflater layoutInflater = (LayoutInflater) Objects.requireNonNull(MainActivity.this).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alertView = layoutInflater != null ? layoutInflater.inflate(R.layout.fragment_notauthorized, null, true) : null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alert.setView(alertView);
            }

            alert.setPositiveButton("Autorisieren", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    User user = userDAO.read(getActiveUser());
                    TextView password = alertView.findViewById(R.id.input_password);

                    /* If password validate send call */
                    if (GlobalFunctions.validatePassword(password, MainActivity.this)) {

                        /* Start a call */
                        String passwordText = GlobalFunctions.hashPassword("" + password.getText());
                        Retrofit retrofit = APIConnector.getRetrofit();
                        APIClient apiInterface = retrofit.create(APIClient.class);
                        String base = user.getMail() + ":" + passwordText;
                        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                        Call<ResponseBody> call = apiInterface.getUser(authString);
                        call.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {

                                    if (response.code() == 401) {
                                        showNotAuthorizedModal(type);
                                        Toast.makeText(instance, "Ihre Eingabe war nicht korrekt!", Toast.LENGTH_LONG).show();
                                    } else {

                                        /* Get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* Parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);

                                        /* Open activity if login success*/
                                        if (mainObject.getString("success").equals("0")) {

                                            Toast.makeText(instance, "Erfolgreich autorisiert!", Toast.LENGTH_LONG).show();

                                            /* Get userObject from Json */
                                            JSONObject userObject = mainObject.getJSONObject("userData");
                                            userObject.put("password", passwordText);

                                            /* Save logged user in db */
                                            userDAO.update(getActiveUser(), GlobalFunctions.createUser(userObject, true, true));

                                            /* Restart ProfileFragment */
                                            if (type == 0) {
                                                loadProfile(false);

                                                /* Restart EditProfileFragment */
                                            } else if (type == 1) {
                                                loadEditProfile();

                                                /* restart Fragment after synchronize Data failed */
                                            } else if (type == 2) {

                                                /* Get last element */
                                                int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
                                                FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
                                                String tag = backEntry.getName();

                                                if (tag == getResources().getString(R.string.fRecord)) {
                                                    loadRecord();
                                                } else if (tag == getResources().getString(R.string.fSettings)) {
                                                    loadSettings();
                                                } else if (tag == getResources().getString(R.string.fRecordDetails)) {
                                                    //TODO
                                                } else if (tag == getResources().getString(R.string.fProfile)) {
                                                    loadProfile(false);
                                                } else if (tag == getResources().getString(R.string.fEditProfile)) {
                                                    loadEditProfile();
                                                } else if (tag == getResources().getString(R.string.fEditPassword)) {
                                                    loadEditPassword(false);
                                                } else if (tag == getResources().getString(R.string.fFriendSystem)) {
                                                    loadFriendSystem(1);
                                                } else if (tag == getResources().getString(R.string.fDeleteAccount)) {
                                                    loadDeleteAccount();
                                                }
                                            } else if (type == 14) {
                                                loadRecord();
                                            } else if (type == 13) {
                                                loadDeleteAccount();
                                            } else if (type == 12) {
                                                loadRecordList(false);
                                            } else if (type == 11) {
                                                loadDashboard(false);
                                            } else if (type == 10) {
                                                loadEditPassword(false);
                                            } else if (type == 3) {
                                                loadDeleteAccount();
                                            } else if (type == 4) {
                                                loadRecordList(false);
                                                /* FriendPage */
                                            } else if (type == 5) {
                                                loadFriendSystem(1);
                                                showAutorizeCounter = 0;
                                                /* FriendQuestionPage */
                                            } else if (type == 6) {
                                                loadFriendSystem(3);
                                                showAutorizeCounter = 0;
                                                /* Send friend question page */
                                            } else if (type == 7) {
                                                loadFriendSystem(4);
                                                showAutorizeCounter = 0;
                                                /* Search stranger */
                                            } else if (type == 8) {
                                                loadFriendSystem(0);
                                                showAutorizeCounter = 0;
                                            } else if (type == 9) {
                                                loadFriendSystem(2);
                                                showAutorizeCounter = 0;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                                Toast.makeText(instance, "Bitte überprüfen Sie Ihre Internetverbindung.", Toast.LENGTH_LONG).show();
                                showNotAuthorizedModal(type);
                            }
                        });
                    } else {
                        showNotAuthorizedModal(type);
                    }
                }
            });

            alert.setNegativeButton("Abmelden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logout();
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    /* Function to synchronize user */
    private void synchronizeUser(User currentUser) {

        /* send user timestamp to bb */
        HashMap<String, String> map = new HashMap<>();
        map.put("timeStamp", "" + currentUser.getTimeStamp());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.synchronizeData(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(2);
                    } else {
                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* User on server is new */
                        if (mainObject.getString("state").equals("0")) {

                            /* Get userObject from Json */
                            JSONObject userObject = mainObject.getJSONObject("user");
                            userObject.put("password", currentUser.getPassword());

                            /* Save user in db */
                            userDAO.update(currentUser.getId(), GlobalFunctions.createUser(userObject, true, true));

                            /* Set drawe profile information */
                            setDrawerInfromation(currentUser.getImage(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getMail());

                            /* User on device is new */
                        } else if (mainObject.getString("state").equals("1")) {

                            /* Change values in global DB*/
                            HashMap<String, String> map = new HashMap<>();
                            map.put("image", GlobalFunctions.getBase64FromBytes(currentUser.getImage()));
                            map.put("email", currentUser.getMail());
                            map.put("firstName", currentUser.getFirstName());
                            map.put("lastName", currentUser.getLastName());
                            map.put("size", "" + currentUser.getWeight());
                            map.put("weight", "" + currentUser.getWeight());
                            map.put("gender", "" + currentUser.getGender());
                            map.put("dateOfBirth", "" + currentUser.getDateOfBirth());
                            map.put("timeStamp", "" + currentUser.getTimeStamp());

                            Retrofit retrofit = APIConnector.getRetrofit();
                            APIClient apiInterface = retrofit.create(APIClient.class);

                            /* Start a call */
                            String base = currentUser.getMail() + ":" + currentUser.getPassword();
                            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                            Call<ResponseBody> call2 = apiInterface.updateUser(authString, map);
                            call2.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    try {
                                        /* Get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* Parse json */
                                        JSONObject successJSON = new JSONObject(jsonString);

                                        if (successJSON.getString("success").equals("0")) {

                                            /* Save is Synchronized value as true */
                                            currentUser.isSynchronised(true);
                                            userDAO.update(currentUser.getId(), currentUser);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    call2.cancel();
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    Log.d(getResources().getString(R.string.app_name) + "-SynchronizeData", "Server Error: " + response.raw().message());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });
    }

    /* Function to synchronize offline records */
    private void synchronizeOfflineRoutes(User currentUser) {
        /* send user timestamp to bb */
        HashMap<String, String> map = new HashMap<>();
        map.put("email", currentUser.getMail());
        map.put("timeStamp", "" + currentUser.getTimeStamp());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* Start a call */
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        /* Get all temp routes */
        RecordTempDAO recordTempDAO = new RecordTempDAO(this);
        List<Route> recordList = recordTempDAO.readAll();
        LocationTempDAO locationTempDAO = new LocationTempDAO((this));
        RouteDAO recordDAO = new RouteDAO(this);

        for (int i = 0; i < recordList.size(); i++) {
            RecordModelForServer m = new RecordModelForServer();
            m.setId(recordList.get(i).getId());
            m.setUserID(MainActivity.getActiveUser());
            m.setName(recordList.get(i).getName());
            m.setType(recordList.get(i).getType());
            m.setTime(recordList.get(i).getTime());
            m.setDate(recordList.get(i).getDate());
            m.setRideTime(recordList.get(i).getRideTime());
            m.setDistance(recordList.get(i).getDistance());
            m.setTimeStamp(recordList.get(i).getTimeStamp());
            m.setLocations(locationTempDAO.readAll(recordList.get(i).getId()));

            Call<ResponseBody> call = apiInterface.uploadFullTrack(authString, m);
            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    /* Get jsonString from API */
                    String jsonString = null;

                    try {
                        jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        if (mainObject.getString("success").equals("0")) {

                            /* Save in DB*/
                            if (mainObject.getJSONObject("record") != null) {
                                JSONObject recordJSON = mainObject.getJSONObject("record");

                                Route record = new Route();
                                record.setId(recordJSON.getInt("id"));
                                record.setName(recordJSON.getString("name"));
                                record.setTime(recordJSON.getLong("time"));
                                record.setDate(recordJSON.getLong("date"));
                                record.setType(recordJSON.getInt("type"));
                                record.setRideTime(recordJSON.getInt("ridetime"));
                                record.setDistance(recordJSON.getDouble("distance"));
                                record.setTimeStamp(recordJSON.getLong("timestamp"));
                                record.setTemp(false);
                                record.setLocations(recordJSON.getString("locations"));
                                recordDAO.create(record);

                                /* Delete old record */
                                int tempRecordId = mainObject.getInt("oldId");
                                record.setId(tempRecordId);

                                /* Remove from temp*/
                                recordTempDAO.delete(record);

                                /* Refresh page */
                                Fragment fragmentDashboard = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard));
                                Fragment fragmentRecordList = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist));

                                if (fragmentDashboard != null && fragmentDashboard.isVisible()) {
                                    loadDashboard(false);

                                } else if (fragmentRecordList != null && fragmentRecordList.isVisible()) {
                                    loadRecordList(false);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }
    }

    /* Function to synchronize all records */
    public void synchronizeRecords(boolean recordList, boolean loadNothing) {

        /* Load view */
        if (!loadNothing) {
            if (recordList) {
                loadRecordList(true);
            } else {
                loadDashboard(true);
            }
        }

        /* Get all records routes */
        RouteDAO recordDAO = new RouteDAO(this);
        List<Route> records = recordDAO.readAll();

        /* Add maps to result */
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {

            HashMap<String, String> map = new HashMap<>();
            map.put("id", "" + records.get(i).getId());
            map.put("timeStamp", "" + records.get(i).getTimeStamp());
            map.put("name", "" + records.get(i).getName());
            result.add(map);
        }

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* Start a call */
        User currentUser = userDAO.read(activeUser);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.synchronizeRecords(authString, result);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                /* Get jsonString from API */
                String jsonString = null;

                try {
                    if (response.code() == 401) {
                        if (recordList) {
                            MainActivity.getInstance().showNotAuthorizedModal(12);
                        } else {
                            MainActivity.getInstance().showNotAuthorizedModal(11);
                        }

                    } else {
                        jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* Update records in local db */
                        if (mainObject.getJSONArray("newerOnServer") != null && mainObject.getJSONArray("newerOnServer").length() > 0) {

                            for (int i = 0; i < mainObject.getJSONArray("newerOnServer").length(); i++) {
                                JSONArray newRecords = mainObject.getJSONArray("newerOnServer");
                                int recordId = ((JSONObject) newRecords.get(i)).getInt("id");
                                String name = ((JSONObject) newRecords.get(i)).getString("name");

                                Route newRecord = recordDAO.read(recordId);
                                newRecord.setName(name);
                                newRecord.setTimeStamp(((JSONObject) newRecords.get(i)).getLong("timeStamp"));
                                recordDAO.update(recordId, newRecord);
                            }
                        }
                        /* Save records from server ind db */
                        if (mainObject.getJSONArray("onServer") != null && mainObject.getJSONArray("onServer").length() > 0) {

                            JSONArray recordsArray = mainObject.getJSONArray("onServer");
                            for (int i = 0; i < recordsArray.length(); i++) {
                                Route record = new Route();
                                record.setId(((JSONObject) recordsArray.get(i)).getInt("id"));
                                record.setName(((JSONObject) recordsArray.get(i)).getString("name"));
                                record.setTime(((JSONObject) recordsArray.get(i)).getLong("time"));
                                record.setDate(((JSONObject) recordsArray.get(i)).getLong("date"));
                                record.setType(((JSONObject) recordsArray.get(i)).getInt("type"));
                                record.setRideTime(((JSONObject) recordsArray.get(i)).getInt("ridetime"));
                                record.setDistance(((JSONObject) recordsArray.get(i)).getDouble("distance"));
                                record.setTimeStamp(((JSONObject) recordsArray.get(i)).getLong("timestamp"));
                                record.setTemp(false);
                                record.setLocations(((JSONObject) recordsArray.get(i)).getString("locations"));
                                recordDAO.create(record);
                            }
                        }

                        /* Delete records, that was deleted on server */
                        if (mainObject.getJSONArray("deletedOnServer") != null && mainObject.getJSONArray("deletedOnServer").length() > 0) {
                            JSONArray deletedIdArray = mainObject.getJSONArray("deletedOnServer");
                            for (int i = 0; i < deletedIdArray.length(); i++) {
                                Route deletedRecord = recordDAO.read(((JSONObject) deletedIdArray.get(i)).getInt("id"));
                                recordDAO.delete(deletedRecord);
                            }
                        }

                        /* Load view */
                        if (!loadNothing) {
                            if (recordList) {
                                loadRecordList(false);
                            } else {
                                loadDashboard(false);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    /* Load view */
                    if (!loadNothing) {
                        if (recordList) {
                            loadRecordList(false);
                        } else {
                            loadDashboard(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    /* Load view */
                    if (!loadNothing) {
                        if (recordList) {
                            loadRecordList(false);
                        } else {
                            loadDashboard(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();

                /* Load view */
                if (!loadNothing) {
                    if (recordList) {
                        loadRecordList(false);
                    } else {
                        loadDashboard(false);
                    }
                }
            }
        });
    }
}
