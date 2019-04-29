package de.trackcat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import de.trackcat.Dashboard.DashboardFragment;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendsFragment;
import de.trackcat.Profile.EditPasswordFragment;
import de.trackcat.Profile.ProfileFragment;
import de.trackcat.Profile.EditProfileFragment;
import de.trackcat.RecordList.RecordListFragment;
import de.trackcat.Recording.Locator;
import de.trackcat.Recording.RecordFragment;
import de.trackcat.Settings.SettingsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.Calendar;
import java.util.List;

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
    public Boolean firstRun = false;
    private static boolean hints;
    private static boolean darkTheme;
    private UserDAO userDAO;
    private static int activeUser;
    public static Boolean isActiv = false;
    private static boolean isRestart = false;
    private static Menu menuInstance;

    /* Zufälliger Integer-Wert für die Wahl des Header Bildes */
    public static int randomImg = (int) (Math.random() * ((13 - 0) + 1)) + 0;

    /* Restart activity for Theme Switching */
    public static void restart() {
        Bundle temp_bundle = new Bundle();
        getInstance().onSaveInstanceState(temp_bundle);
        Intent intent = new Intent(getInstance(), MainActivity.class);
        intent.putExtra("bundle", temp_bundle);

        isRestart = true;

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

    public static boolean getDarkTheme() {
        return darkTheme;
    }

    public static int getActiveUser() {
        return activeUser;
    }

    public static void setDarkTheme(boolean activeDarkTheme) {
        darkTheme = activeDarkTheme;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fRecord))) {
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
            recordFragment.stopTimer();
            recordFragment = null;
        } catch (NullPointerException e) {

        }
        MainActivity.getInstance().stopService(new Intent(MainActivity.getInstance(), Locator.class));

        isActiv = false;

        if (!isRestart) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        isRestart = false;

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
        if (userList.size() == 0) {
            /* Initiale Usererstellung */

            User initialUser = new User("Test", "Testi", "test.testi@mail.de",
                    null);
            initialUser.setActive(true);
            initialUser.setHintsActive(true);
            initialUser.setDarkThemeActive(false);
            userDAO.create(initialUser);
            activeUser = 1;
            hints = true;
            darkTheme = false;

        } else {

            hints = userList.get(0).isHintsActive();
            darkTheme = userList.get(0).isDarkThemeActive();
            activeUser = userList.get(0).getId();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (isActiv) {
            Toast.makeText(this, "Die App läuft bereits in einer anderen Instanz",
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            isActiv = true;
        }
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

        /* set profile information */
        User currentUser = userDAO.read(activeUser);
        profileName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
        profileEmail.setText(currentUser.getMail());

        /* Menu Toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawer.addDrawerListener(toggle);
        toggle.syncState();

        notificationManager = NotificationManagerCompat.from(this);
        firstRun = true;

        /* Startseite festlegen - Erster Aufruf */
        loadDashboard();
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
                }
                break;
            case R.id.nav_recordlist:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) == null) {
                    menuInstance.clear();
                    loadRecordList();
                }
                break;
            case R.id.nav_record:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {
                    menuInstance.clear();
                    loadRecord();
                }
                break;
            case R.id.nav_settings:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) == null) {
                    menuInstance.clear();
                    loadSettings();
                }
                break;
            case R.id.nav_friends:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) == null) {
                    menuInstance.clear();
                    loadFriendSystem();
                }
                break;
            case R.id.nav_logout:

                /* set wait field */
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Abmeldung...");
                progressDialog.show();
                /* set waiting handler */
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                //TODO Geschmeidiger Übergang
                                progressDialog.dismiss();

                                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(intent);
                                MainActivity.super.onDestroy();

                            }
                        }, 3000);

                break;
        }
        menuItem.setChecked(true);
        mainDrawer.closeDrawer(GravityCompat.START);
        return true;
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
        recordFragment.startTracking();
        startActivity(getIntent());
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
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditProfile)) != null || getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fEditPassword)) != null) {
            loadProfile(false);
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
    public void loadFriendSystem() {
        Log.i(getResources().getString(R.string.app_name) + "-Fragment", "Das Freunde-Fragment wird geladen.");
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new FriendsFragment(),
                getResources().getString(R.string.fFriendSystem));
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
                    }else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fFriendSystem)) != null) {
                        alert.setMessage(getResources().getString(R.string.help_friendSystem));
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
}
