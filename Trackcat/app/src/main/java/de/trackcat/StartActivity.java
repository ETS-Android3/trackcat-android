package de.trackcat;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.karan.churi.PermissionManager.PermissionManager;

import de.trackcat.LogIn.LoadScreenFragment;
import de.trackcat.LogIn.LogInFragment;

public class StartActivity extends AppCompatActivity {

    private FragmentTransaction fragTransaction;
    private static StartActivity instance;
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
        instance=this;

        /* Load Screen */
        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, new LoadScreenFragment(),
                getResources().getString(R.string.fLoadScreen));
        fragTransaction.commit();

        /* LogIn Screen */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        fragTransaction = getSupportFragmentManager().beginTransaction();
                        fragTransaction.replace(R.id.mainFrame, new LogInFragment(),
                                getResources().getString(R.string.fLogIn));
                        fragTransaction.commit();
                    }
                }, 3000);
    }
}

