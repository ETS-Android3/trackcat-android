package de.trackcat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.karan.churi.PermissionManager.PermissionManager;

import java.util.List;

import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.LogIn.LoadScreenFragment;
import de.trackcat.LogIn.LogInFragment;

public class StartActivity extends AppCompatActivity {

    private FragmentTransaction fragTransaction;
    private UserDAO userDAO;
    private static StartActivity instance;
    private int delay = 3000;

    public ProgressDialog progressDialog;

    private PermissionManager permissionManager = new PermissionManager() {
    };

    public static StartActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(this);

        setContentView(R.layout.activity_start);

        /* set instance */
        instance = this;

        /* set dao and check if user in db */
        userDAO = new UserDAO(this);
        int userCount = userDAO.userInDB();

        try {

            if (getIntent().getExtras().getBoolean("isLogout")) {
                delay = 0;
            }
        } catch (Exception e) {

            /* Load Screen */
            fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new LoadScreenFragment(),
                    getResources().getString(R.string.fLoadScreen));
            fragTransaction.commit();
        }
        /* LogIn Screen */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        /* show login page if no user is logged in */
                        if (userCount == 0) {
                            fragTransaction = getSupportFragmentManager().beginTransaction();
                            fragTransaction.replace(R.id.mainFrame, new LogInFragment(),
                                    getResources().getString(R.string.fLogIn));
                            fragTransaction.commitAllowingStateLoss();
                            /* loged user in, if one entry in table */
                        } else {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, delay);
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}

