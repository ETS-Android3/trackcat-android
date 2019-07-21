package de.trackcat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
    // public Boolean firstRun = false;
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
    //  private static boolean isRestart = false;
    private static Menu menuInstance;
    private ProgressDialog progressDialog;
    private static boolean connected;

    /* Zufälliger Integer-Wert für die Wahl des Header Bildes */
    public static int randomImg = (int) (Math.random() * ((13 - 0) + 1)) + 0;

    /* Restart activity for Theme Switching */
    public static void restart() {
        Bundle temp_bundle = new Bundle();
        getInstance().onSaveInstanceState(temp_bundle);
        Intent intent = new Intent(getInstance(), MainActivity.class);
        intent.putExtra("bundle", temp_bundle);

        //    isRestart = true;

        getInstance().startActivity(intent);
        getInstance().finish();
    }

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
        /* Entferne die Benachrichtigung, wenn App läuft */
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

        //   if (!isRestart) {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // }
        // isRestart = false;

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

    /* function to get active user information */
    public void getCurrentUserInformation() {
        userDAO = new UserDAO(this);

        List<User> userList = userDAO.readAll();
        hints = userList.get(0).isHintsActive();
        darkTheme = userList.get(0).isDarkThemeActive();
        activeUser = userList.get(0).getId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Turn off power saving and battery optimization */
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

        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(this);

        getCurrentUserInformation();
        /* Aktuelles Themes aus Einstellungen laden */
        setTheme(getDarkTheme() ? R.style.AppTheme_Dark : R.style.AppTheme);

        if (getIntent().hasExtra("bundle") && savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }

        /* Startseite definieren */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* if (isActiv) {
            Toast.makeText(this, "Die App läuft bereits in einer anderen Instanz",
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            isActiv = true;
        }*/
        /* Instanz für spätere Objekte speichern */
        instance = this;
        recordFragment = new RecordFragment();
        mainDrawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        showHelp = findViewById(R.id.showHelp);

        /*On Click Listener definieren*/
        showHelp.setOnClickListener(this);

        /* Actionbar definieren und MenuListener festlegen */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        /* show menu, if you click on profile */
        profileEmail = headerView.findViewById(R.id.profile_email);
        profileName = headerView.findViewById(R.id.profile_name);
        profileImage = headerView.findViewById(R.id.profile_image);
        profileEmail.setOnClickListener(this);
        profileName.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        /* Header anhand des aktuellen Monats wählen*/
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
        User currentUser = userDAO.read(activeUser);
        setDrawerInfromation(currentUser.getImage(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getMail());
        synchronizeUser(currentUser);

        /* Menu Toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawer.addDrawerListener(toggle);
        toggle.syncState();

        notificationManager = NotificationManagerCompat.from(this);
        //  firstRun = true;

        /* check start connectivity */
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

        /* Startseite festlegen - Erster Aufruf */
        loadDashboard();
    }

    /* set profile information */
    public void setDrawerInfromation(byte[] imgRessource, String first_name, String last_name, String email) {

        profileName.setText(first_name + " " + last_name);
        profileEmail.setText(email);

        /* set image */
        Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.default_profile);
        if (imgRessource != null && imgRessource.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
        }
        profileImage.setImageBitmap(bitmap);

    }

    /* Anpassen der TextFarbe zum Hintergrundbild */
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
                loadEditPassword();
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

        /* Aktion je nach Auswahl des Items */
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) == null) {
                    toolbar.getMenu().clear();
                    loadDashboard();
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_recordlist:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) == null) {
                    menuInstance.clear();
                    synchronizeRecords();
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_record:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {
                    menuInstance.clear();
                    loadRecord();
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_settings:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) == null) {
                    menuInstance.clear();
                    loadSettings();
                    clearValuesAfterChangeMenu();
                }
                break;
            case R.id.nav_friends:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) == null) {
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

    public void clearValuesAfterChangeMenu() {
        setSearchForeignPage(0);
        setSearchForeignTerm(null);
        setSearchForeignPageIndex(0);
        setSearchFriendPage(0);
        setSearchFriendTerm("");
        setSearchFriendPageIndex(0);
        setFriendQuestionIndex(0);
        setSendFriendQuestionIndex(0);
    }

    public void logout() {
        /* set wait field */
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Abmeldung...");
        progressDialog.show();
        /* set waiting handler */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        finish();

                        /* remove user from local db */
                        List<User> deletedUsers = userDAO.readAll();
                        for (User user : deletedUsers) {
                            userDAO.delete(user);
                        }

                        /* open login fragment */
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        intent.putExtra("isLogout", true);

                        startActivity(intent);
                    }
                }, 3000);
    }

    /* Stops/pauses Tracking opens App and switch to RecordFragment */
    public void stopTracking() {
        startActivity(getIntent());
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
        startActivity(getIntent());
        recordFragment.startTracking();
        try {
            loadRecord();
        } catch (RuntimeException e) {

        }
    }

    /* Startet RecordFragment nach Ende der Aufzeichnung */
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
        if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsDashbaord)) != null) {
            loadDashboard();
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsList)) != null) {
            loadRecordList();
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDeleteAccount)) != null) {
            loadProfile(false);
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendProfile)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendLiveView)) != null) {
            loadFriendSystem(1);
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fPublicPersonProfile)) != null) {
            loadFriendSystem(0);
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fPublicPersonProfileQuestion)) != null) {
            loadFriendSystem(3);
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fPublicPersonProfileSendQuestion)) != null) {
            loadFriendSystem(4);
        } else if (mainDrawer.isDrawerOpen(GravityCompat.START)) {
            mainDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (exitApp) {
                finish();
                System.exit(0);
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

    /* Laden des Dashboard-Fragments */
    public void loadDashboard() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Dashboard-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(),
                getResources().getString(R.string.fDashboard));
        fragTransaction.commit();
    }

    /* Laden des Aufnahme-Fragments */
    public void loadRecord() {
        /* Fragt nach noch nicht erteilten Permissions */

        Log.v("loadRecord", "loading Record");

        permissionManager.checkAndRequestPermissions(MainActivity.getInstance());

        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getInstance().checkCallingOrSelfPermission(permission);

        String permissionGPS = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int resGPS = getInstance().checkCallingOrSelfPermission(permissionGPS);

        if (res == PackageManager.PERMISSION_GRANTED && resGPS == PackageManager.PERMISSION_GRANTED) {//&& getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {

            Log.v("loadRecord", "placing Record");


            Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Aufnahme-Fragment wird geladen.");
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment,
                    getResources().getString(R.string.fRecord));
            fragTransaction.commit();
        }
    }

    /* Laden des Listen-Fragments */
    public void loadRecordList() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Listen-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new RecordListFragment(),
                getResources().getString(R.string.fRecordlist));
        fragTransaction.commit();
    }

    /* Laden des Profil-Fragments */
    public void loadProfile(boolean loadMenu) {

        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Profil-Fragment wird geladen.");

        /* set Bundle */
        Bundle bundleSpeed = new Bundle();
        bundleSpeed.putBoolean("loadMenu", loadMenu);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundleSpeed);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, profileFragment,
                getResources().getString(R.string.fProfile));
        fragTransaction.commit();

        /* set menu checked on false */
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_dashboard).setChecked(false);
        menu.findItem(R.id.nav_record).setChecked(false);
        menu.findItem(R.id.nav_recordlist).setChecked(false);
        menu.findItem(R.id.nav_settings).setChecked(false);
        menu.findItem(R.id.nav_friends).setChecked(false);
    }

    /* Laden des Einstellung-Fragments */
    public void loadSettings() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Einstellung-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new SettingsFragment(),
                getResources().getString(R.string.fSettings));
        fragTransaction.commit();
    }

    /* Laden des Einstellung-Fragments */
    public void loadEditProfile() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Profil-Bearbeiten-Fragment wird geladen.");

        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new EditProfileFragment(),
                getResources().getString(R.string.fEditProfile));
        fragTransaction.commit();
    }

    /* Laden des Einstellung-Fragments */
    public void loadEditPassword() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Passwort-Ändern-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new EditPasswordFragment(),
                getResources().getString(R.string.fEditPassword));
        fragTransaction.commit();
    }

    /* Laden des Friends-Fragments */
    public void loadFriendSystem(int activeSite) {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Freunde-Fragment wird geladen.");
        Bundle bundle = new Bundle();
        bundle.putInt("activeSite", activeSite);
        /* add searchTerm to bundle if its loaded site */
        if (activeSite == 0) {
            //   bundle.putString("searchTerm", searchTerm);
        }
        FriendsViewerFragment firendsFragment = new FriendsViewerFragment();
        firendsFragment.setArguments(bundle);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, firendsFragment,
                getResources().getString(R.string.fFriendSystem));
        fragTransaction.commit();
    }

    /* Laden des Friends-Fragments */
    public void loadDeleteAccount() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Account-Löschen-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DeleteAccountFragment(),
                getResources().getString(R.string.fDeleteAccount));
        fragTransaction.commit();
    }

    // set the RecordFragment wich is in use
    public void setRecordFragment(RecordFragment recordFragment) {
        this.recordFragment = recordFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showHelp:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.getInstance());
                alert.setTitle("Hilfe");

                if (MainActivity.getHints()) {
                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_dashboard));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_record));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_record_list));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_settings));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsDashbaord)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsList)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_record_details));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_profile));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_editProfile));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_editPassword));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_friendSystem));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDeleteAccount)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_deleteAccount));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendProfile)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_friends_profile));
                    } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendLiveView)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_friends_live_view));
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
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile)) == null) {
                    menuInstance.clear();
                    loadProfile(true);
                }
                mainDrawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

    /* function checkt if device have network connection */
    public void networkChange(boolean connected) {
        Log.v(getResources().getString(R.string.app_name) + "-ConnectedListener", String.valueOf(connected));
        setConnection(connected);

        /* device have connection */
        if (connected) {

            User currentUser = userDAO.read(activeUser);
            synchronizeUser(currentUser);
            synchronizeOfflineRoutes(currentUser);
        }
    }

    int showAutorizeCounter = 0;

    /* Function to show not autorized modal */
    public void showNotAuthorizedModal(int type) {
        if (type == 5 | type == 6 | type == 7 | type == 8) {
            showAutorizeCounter++;
        }

        if((type == 5 | type == 6 | type == 7 | type == 8)&&showAutorizeCounter==1) {
            /* create AlertBox */
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
                        Retrofit retrofit = APIConnector.getRetrofit();
                        APIClient apiInterface = retrofit.create(APIClient.class);
                        String base = user.getMail() + ":" + password.getText();
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

                                        /* get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        /* open activity if login success*/
                                        if (mainObject.getString("success").equals("0")) {

                                            /* get userObject from Json */
                                            JSONObject userObject = mainObject.getJSONObject("userData");

                                            /* save logged user in db */
                                            userDAO.update(getActiveUser(), GlobalFunctions.createUser(userObject));

                                            /* restart ProfileFragment */
                                            if (type == 0) {
                                                loadProfile(false);
                                                /* restart EditProfileFragment */
                                            } else if (type == 1) {
                                                loadEditProfile();
                                                /* restart Fragement after synchronize Data failed */
                                            } else if (type == 2) {

                                                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) != null) {
                                                    loadDashboard();
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) != null) {
                                                    loadRecord();
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) != null) {
                                                    loadRecordList();
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) != null) {
                                                    //TODO
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsDashbaord)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsList)) != null) {
                                                    //TODO
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fProfile)) != null) {
                                                    loadProfile(false);
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile)) != null) {
                                                    loadEditProfile();
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword)) != null) {
                                                    //TODO
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) != null) {
                                                    loadFriendSystem(1);
                                                } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDeleteAccount)) != null) {
                                                    loadDeleteAccount();
                                                }
                                            } else if (type == 3) {
                                                loadDeleteAccount();
                                            } else if (type == 4) {
                                                loadRecordList();
                                                /* friendPage */
                                            } else if (type == 5) {
                                                loadFriendSystem(1);
                                                showAutorizeCounter = 0;
                                                /* friendQuestionPage */
                                            } else if (type == 6) {
                                                loadFriendSystem(3);
                                                showAutorizeCounter = 0;
                                                /* Send friend question page */
                                            } else if (type == 7) {
                                                loadFriendSystem(4);
                                                showAutorizeCounter = 0;
                                            }else if (type == 8) {
                                                loadFriendSystem(0);
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

    private void synchronizeUser(User currentUser) {
        /* send user timestamp to bb */
        HashMap<String, String> map = new HashMap<>();
        map.put("email", currentUser.getMail());
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
                        /* get jsonString from API */
                        String jsonString = response.body().string();

                        /* parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* user on server is new */
                        if (mainObject.getString("state").equals("0")) {

                            /* get userObject from Json */
                            JSONObject userObject = mainObject.getJSONObject("user");

                            /* save user in db */
                            currentUser.setId(userObject.getInt("id"));
                            currentUser.setMail(userObject.getString("email"));
                            currentUser.setFirstName(userObject.getString("firstName"));
                            currentUser.setLastName(userObject.getString("lastName"));
                            if (userObject.getString("image") != "null") {
                                currentUser.setImage(GlobalFunctions.getBytesFromBase64(userObject.getString("image")));
                            }
                            currentUser.setGender(userObject.getInt("gender"));
                            if (userObject.getInt("darkTheme") == 0) {
                                currentUser.setDarkThemeActive(false);
                            } else {
                                currentUser.setDarkThemeActive(true);
                            }

                            if (userObject.getInt("hints") == 0) {
                                currentUser.setHintsActive(false);
                            } else {
                                currentUser.setHintsActive(true);
                            }

                            try {
                                currentUser.setDateOfRegistration(userObject.getLong("dateOfRegistration"));
                            } catch (Exception e) {
                            }

                            try {
                                currentUser.setLastLogin(userObject.getLong("lastLogin"));
                            } catch (Exception e) {
                            }

                            try {
                                currentUser.setWeight((float) userObject.getDouble("weight"));
                            } catch (Exception e) {
                            }

                            try {
                                currentUser.setSize((float) userObject.getDouble("size"));
                            } catch (Exception e) {
                            }
                            try {
                                currentUser.setDateOfBirth(userObject.getLong("dateOfBirth"));
                            } catch (Exception e) {
                            }

                            currentUser.setPassword(userObject.getString("password"));
                            currentUser.setTimeStamp(userObject.getLong("timeStamp"));
                            currentUser.isSynchronised(true);
                            userDAO.update(currentUser.getId(), currentUser);

                            /* set drawe profile information */
                            setDrawerInfromation(currentUser.getImage(), currentUser.getFirstName(), currentUser.getLastName(), currentUser.getMail());


                            /* user on device is new */
                        } else if (mainObject.getString("state").equals("1")) {

                            /* change values in global DB*/
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

                            /* start a call */
                            String base = currentUser.getMail() + ":" + currentUser.getPassword();
                            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);


                            Call<ResponseBody> call2 = apiInterface.updateUser(authString, map);

                            call2.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    try {
                                        /* get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject successJSON = new JSONObject(jsonString);

                                        if (successJSON.getString("success").equals("0")) {

                                            /* save is Synchronized value as true */
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
                    Log.d(getResources().getString(R.string.app_name) + "-SynchroniseData", "Server Error: " + response.raw().message());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void synchronizeOfflineRoutes(User currentUser) {
        /* send user timestamp to bb */
        HashMap<String, String> map = new HashMap<>();
        map.put("email", currentUser.getMail());
        map.put("timeStamp", "" + currentUser.getTimeStamp());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        /* get all temp routes */
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

                    /* get jsonString from API */
                    String jsonString = null;

                    try {
                        jsonString = response.body().string();

                        /* parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        if (mainObject.getString("success").equals("0")) {

                            /* save in DB*/
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

                                /*remove from temp*/
                                recordTempDAO.delete(record);
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

    /* function to synchronize all records */
    public void synchronizeRecords() {

        /* get all records routes */
        RouteDAO recordDAO = new RouteDAO(this);
        List<Route> records = recordDAO.readAll();

        /* add maps to result */
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

        /* start a call */
        User currentUser = userDAO.read(activeUser);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.synchronizeRecords(authString, result);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                /* get jsonString from API */
                String jsonString = null;

                try {
                    jsonString = response.body().string();

                    /* parse json */
                    JSONObject mainObject = new JSONObject(jsonString);

                    /* update records in local db */
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
                    /* save records from server ind db */
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
                    /* send records to server */
                    if (mainObject.getJSONArray("missingId") != null && mainObject.getJSONArray("missingId").length() > 0) {
                        Toast.makeText(MainActivity.getInstance(), "!!!!!!ES KOMMT VOR!!!!",
                                Toast.LENGTH_LONG).show();
                    }

                    /*delete records, that was deleted on server */
                    if (mainObject.getJSONArray("deletedOnServer") != null && mainObject.getJSONArray("deletedOnServer").length() > 0) {
                        JSONArray deletedIdArray = mainObject.getJSONArray("deletedOnServer");
                        for (int i = 0; i < deletedIdArray.length(); i++) {
                            Route deletedRecord = recordDAO.read(((JSONObject) deletedIdArray.get(i)).getInt("id"));
                            recordDAO.delete(deletedRecord);
                        }
                    }

                    /*load view*/
                    loadRecordList();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();

                /*load view*/
                loadRecordList();
            }
        });
    }
}
