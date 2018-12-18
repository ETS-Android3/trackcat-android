package de.mobcom.group3.gotrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import de.mobcom.group3.gotrack.RecordList.RecordDetailsInformationFragment;
import de.mobcom.group3.gotrack.RecordList.RecordListFragment;
import de.mobcom.group3.gotrack.Recording.RecordFragment;
import de.mobcom.group3.gotrack.Settings.CustomSpinnerAdapter;
import de.mobcom.group3.gotrack.Settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PermissionManager permissionManager = new PermissionManager() {
    };
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
    UserDAO userDAO;

    private static final String PREF_DARK_THEME = "dark_theme";

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

    public static boolean getDarkTheme() {
        return darkTheme;
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
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(this);

        /* Aktuelles Themes aus Einstellungen laden */
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_DARK_THEME, false) ? R.style.AppTheme_Dark : R.style.AppTheme);

        /* Startseite definieren */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            initialUser.setActive(1);
            userDAO.create(initialUser);
        }

        // TODO Profilwechsel
        spinner = navigationView.getHeaderView(0).findViewById(R.id.profile_spinner);
        addItemsToSpinner();

        /* Startseite festlegen - Erster Aufruf */
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new DashboardFragment(), getResources().getString(R.string.fDashboard));
        fragTransaction.commit();
    }

    /* Dynamisches Hinzufügen von Spinner-Items */
    public void addItemsToSpinner() {

        /* Erstellen der Listen */
        final ArrayList<byte[]> spinnerAccountIcons = new ArrayList<byte[]>();
        ArrayList<String> spinnerAccountEmail = new ArrayList<String>();
        final ArrayList<String> spinnerAccountNames = new ArrayList<String>();
        List<User> users = userDAO.readAll();
        int selectedID = 0;
        boolean findActiveUser = false;
        for (int i = 0; i < users.size(); i++) {
            spinnerAccountEmail.add(users.get(i).getMail());
            spinnerAccountNames.add(users.get(i).getFirstName() + " " + users.get(i).getLastName());
            spinnerAccountIcons.add(users.get(i).getImage());
            if (users.get(i).isActive()) {
                activeUser = users.get(i).getId();
                hints = true;
                darkTheme = true;
                selectedID = i;
                findActiveUser = true;
            }
        }

        /*Wenn nach dem Löschen eines Users kein neuer aktiver Nutzer gefunden wurde*/
        if (!findActiveUser) {
            activeUser = users.get(selectedID).getId();
            User newActiveUser = userDAO.read(activeUser);
            newActiveUser.setActive(1);
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
                /* Auslesen des angeklickten Items */
                String item = adapter.getItemAtPosition(position).toString();

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
                        User user = new User();
                        user.setFirstName(users.get(i).getFirstName());
                        user.setLastName(users.get(i).getLastName());
                        user.setMail(users.get(i).getMail());
                        user.setImage(users.get(i).getImage());
                        user.setActive(1);
                        userDAO.update(users.get(i).getId(), user);

                        /* Alten Nutzer deaktivieren */
                        if (deactivateOldUser) {
                            User oldUser = userDAO.read(activeUser);
                            oldUser.setActive(0);
                            userDAO.update(activeUser, oldUser);
                        }

                        /* Nutzerwechsel in globaler Variable */
                        activeUser = users.get(i).getId();
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

                    fragTransaction.replace(R.id.mainFrame, new RecordDetailsInformationFragment(), getResources().getString(R.string.fRecordlist));
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
        // TODO switch to Statisitcs page
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
            Toast.makeText(instance, "Noch einmal klicken, um App zu beenden!", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitApp = false;
                    Toast.makeText(instance, "Zu langsam. Versuche es erneut...", Toast.LENGTH_LONG).show();
                }
            }, 3000);
        }
    }

}