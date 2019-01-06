package de.mobcom.group3.gotrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import de.mobcom.group3.gotrack.Dashboard.DashboardFragment;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.InExport.Import;
import de.mobcom.group3.gotrack.RecordList.RecordListFragment;
import de.mobcom.group3.gotrack.Recording.RecordFragment;
import de.mobcom.group3.gotrack.Settings.CustomSpinnerAdapter;
import de.mobcom.group3.gotrack.Settings.SettingsFragment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PermissionManager permissionManager = new PermissionManager() {};
    final int NOTIFICATION_ID = 100;
    private DrawerLayout mainDrawer;
    private NavigationView navigationView;
    private static MainActivity instance;
    private RecordFragment recordFragment;
    private NotificationManagerCompat notificationManager;
    private static Spinner spinner;
    private static int activeUser;
    private static boolean hints;
    private static boolean darkTheme;
    private static boolean createInitialUser =false;
    private UserDAO userDAO;
    public static Boolean isActiv=false;
    private static final String PREF_DARK_THEME = "dark_theme";

    // Restart activity for Theme Switching
    public static void restart(){
        Bundle temp_bundle = new Bundle();
        getInstance().onSaveInstanceState(temp_bundle);
        Intent intent = new Intent(getInstance(), MainActivity.class);
        intent.putExtra("bundle", temp_bundle);
        getInstance().startActivity(intent);
        getInstance().finish();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static Spinner getSpinner() {
        return spinner;
    }

    public static int getActiveUser() {
        return activeUser;
    }

    public static boolean getHints() {
        return hints;
    }

    public static void setHints(boolean activeHints) {
        hints=activeHints;
    }

    public static boolean getDarkTheme() {
        return darkTheme;
    }

    public static void setDarkTheme(boolean activeDarkTheme) {
        darkTheme=activeDarkTheme;
    }

    public static void setCreateUser(boolean createUser) {
        createInitialUser=createUser;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fRecord))) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
            fragTransaction.commit();
        } else if (action != null && action.equalsIgnoreCase(getResources().getString(R.string.fSettings))) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), getResources().getString(R.string.fSettings));
            fragTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        /* Entferne die Benachrichtigung, wenn App läuft */
        notificationManager.cancel(getNOTIFICATION_ID());
        isActiv=false;
        super.onDestroy();
    }

    @Override
    public void recreate(){
        if(Build.VERSION.SDK_INT >= 11){
            super.recreate();
        }else{
            startActivity(getIntent());
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(this);

        /* Aktuelles Themes aus Einstellungen laden */
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_DARK_THEME, false) ? R.style.AppTheme_Dark : R.style.AppTheme);

        if(getIntent().hasExtra("bundle") && savedInstanceState == null){
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        }

        /* Startseite definieren */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isActiv){
            Toast.makeText(this, "Die App läuft bereits in einer anderen Instanz",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            isActiv =true;
        }
        /* Instanz für spätere Objekte speichern */
        instance = this;
        recordFragment = new RecordFragment();
        mainDrawer = findViewById(R.id.drawer_layout);

        /* Actionbar definieren und MenuListener festlegen */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Menu Toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawer.addDrawerListener(toggle);
        toggle.syncState();

        notificationManager = NotificationManagerCompat.from(this);

        /* Initiale Usererstellung */
        userDAO = new UserDAO(this);
        List<User> userList = userDAO.readAll();
        if (userList.size() == 0) {
            User initialUser = new User("Max", "Mustermann", "max.mustermann@mail.de",
                    null);
            initialUser.setActive(true);
            initialUser.setHintsActive(true);
            userDAO.create(initialUser);
            createInitialUser=true;
        }

        spinner = navigationView.getHeaderView(0).findViewById(R.id.profile_spinner);
        addItemsToSpinner();

        /* Startseite festlegen - Erster Aufruf */
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
        fragTransaction.commit();
        Log.d("test123", "===========Problem=========");
        Log.d("test123", "Nach dem deaktivieren der Hints und dem Wechsel zu einem anderen Nutzer ist alles gut. Beim Wechsel zurück, zu dem eben erwähnten Nutzer, werden seine Hints wieder aktiviert");
    }

    /* Dynamisches Hinzufügen von Spinner-Items */
    public void addItemsToSpinner() {
        Log.d("test123", "===========in addItemToSpinner=========");
        /* Erstellen der Listen */
        final ArrayList<byte[]> spinnerAccountIcons = new ArrayList<byte[]>();
        ArrayList<String> spinnerAccountEmail = new ArrayList<String>();
        final ArrayList<String> spinnerAccountNames = new ArrayList<String>();
        List<User> users = userDAO.readAll();
        int selectedID = 0;
        boolean findActiveUser = false;
        for (int i = 0; i < users.size(); i++) {
            Log.d("test123", "for Schleife Items hinzufügen: "+users.get(i).getFirstName() + " " + users.get(i).getLastName());
            spinnerAccountEmail.add(users.get(i).getMail());
            spinnerAccountNames.add(users.get(i).getFirstName() + " " + users.get(i).getLastName());
            spinnerAccountIcons.add(users.get(i).getImage());
            if (users.get(i).isActive()) {
                Log.d("test123", "activer Nutzer gefunden!");
                activeUser = users.get(i).getId();
                hints = users.get(i).isHintsActive();
                Log.d("test123", "hints Variable: "+ hints);
                darkTheme = users.get(i).isDarkThemeActive();
                selectedID = i;
                findActiveUser = true;
            }
        }

        /*Wenn nach dem Löschen eines Users kein neuer aktiver Nutzer gefunden wurde*/
        if (!findActiveUser) {
            Log.d("test123", "keinen Activen Nutzer gefunden!");
            activeUser = users.get(selectedID).getId();
            User newActiveUser = userDAO.read(activeUser);
            newActiveUser.setActive(true);
            userDAO.update(activeUser, newActiveUser);
        }
        final boolean deactivateOldUser = findActiveUser;

        /* Erstellen des Custom Spinners */
        final CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), spinnerAccountIcons, spinnerAccountNames, spinnerAccountEmail);

        /* Setzen des Adapters */
        spinner.setAdapter(spinAdapter);
        spinner.setSelection(selectedID);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {

                /* Überprüfung, ob immoment ein Import aktiv ist */
                if(Import.getImport().getIsImportActiv()){
                    if (hints) {
                        Toast.makeText(getApplicationContext(), "Nutzerwechsel nicht möglich, da im Moment ein Import läuft.", Toast.LENGTH_LONG).show();
                    }
                }else {

                    /* Auslesen des angeklickten Items */
                    String item = adapter.getItemAtPosition(position).toString();
                    Log.d("test123", "===========in OneItemSelected=========");
                    /* Wechseln des Profilbildes */
                    byte[] imgRessource = spinnerAccountIcons.get(position);
                    de.hdodenhof.circleimageview.CircleImageView circleImageView = findViewById(R.id.profile_image);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.default_profile);
                    if (imgRessource != null && imgRessource.length > 0) {
                        bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
                    }
                    circleImageView.setImageBitmap(bitmap);

                    /* Überprüfung, ob Nutzerwechsel oder Nutzer bearbeiten */
                    for (int i = 0; i < users.size(); i++) {
                        if (adapter.getItemAtPosition(position).equals(users.get(i).getFirstName() + " " + users.get(i).getLastName())) {
                            /* Ausgewählten Nutzer als aktiven Nutzer setzen */
                            Log.d("test123", "===========Nutzerwechsel=========");
                            Log.d("test123", "User aus Liste: " + users.get(i).getFirstName() + " hints: " + users.get(i).isHintsActive());
                            User user = new User();
                            user.setFirstName(users.get(i).getFirstName());
                            user.setLastName(users.get(i).getLastName());
                            user.setMail(users.get(i).getMail());
                            user.setImage(users.get(i).getImage());
                            user.setHintsActive(users.get(i).isHintsActive());
                            user.setDarkThemeActive(users.get(i).isDarkThemeActive());
                            user.setActive(true);
                            userDAO.update(users.get(i).getId(), user);
                            Log.d("test123", "neuer User in DB: " + user.getFirstName() + " hints: " + user.isHintsActive());

                            /* Alten Nutzer deaktivieren */
                            if (deactivateOldUser && !createInitialUser) {
                                Log.d("test123", "===========alten Nutzer deaktivieren=========");
                                Log.d("test123", "alter Activer Nutzer: " + activeUser);
                                User oldUser = userDAO.read(activeUser);
                                oldUser.setActive(false);
                                userDAO.update(activeUser, oldUser);
                                Log.d("test123", "oldUser: " + oldUser.getFirstName() + " hints: " + oldUser.isHintsActive());
                            } else {
                                createInitialUser = false;
                            }

                            /* Nutzerwechsel in globaler Variable */
                            activeUser = users.get(i).getId();
                            hints = users.get(i).isHintsActive();
                            darkTheme = users.get(i).isDarkThemeActive();
                            Log.d("test123", "neuer Activer Nutzer: " + activeUser);
                            Log.d("test123", "Variable: " + hints);
                            if (hints) {
                                Toast.makeText(getApplicationContext(), "Ausgewähltes Profil: " + item, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) == null) {
                        /*Anzeigen des Dashboard nach Wechsel des Nutzers*/
                        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
                        fragTransaction.commit();
                        Menu menu = navigationView.getMenu();
                        menu.findItem(R.id.nav_dashboard).setChecked(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        /* Aktion je nach Auswahl des Items */
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fDashboard)) == null) {
                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                    fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
                    fragTransaction.commit();
                }
                break;
            case R.id.nav_recordlist:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordlist)) == null) {
                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                    fragTransaction.replace(R.id.mainFrame, new RecordListFragment(), getResources().getString(R.string.fRecordlist));
                    fragTransaction.commit();
                }
                break;
            case R.id.nav_record:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecord)) == null) {
                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                    fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
                    fragTransaction.commit();
                }
                break;
            case R.id.nav_import:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(
                            Intent.createChooser(intent, "Import"),0);
                break;
            case R.id.nav_settings:
                if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fSettings)) == null) {
                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

                    fragTransaction.replace(R.id.mainFrame, new SettingsFragment(), getResources().getString(R.string.fSettings));
                    fragTransaction.commit();
                }
                break;
        }
        menuItem.setChecked(true);
        mainDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        File file = new File(getCacheDir(), "document");
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Import.getImport().handleSend(this, file, inputStream);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /* Stops/pauses Tracking opens App and switch to RecordFragment */
    public void stopTracking() {
        startActivity(getIntent());
        try {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
            fragTransaction.commit();
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
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
            fragTransaction.commit();
        } catch (RuntimeException e) {

        }
    }

    /* Startet RecordFragment nach Ende der Aufzeichnung */
    public void endTracking() {
        recordFragment = new RecordFragment();
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, recordFragment, getResources().getString(R.string.fRecord));
        fragTransaction.commit();
    }

    public RecordFragment getRecordFragment() {
        return recordFragment;
    }

    /* BackPressed Listener */
    private boolean exitApp = false;
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsDashbaord)) != null) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
            fragTransaction.commit();
        } else if (getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.fRecordDetailsList)) != null) {
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new RecordListFragment(), getResources().getString(R.string.fRecordlist));
            fragTransaction.commit();
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

}