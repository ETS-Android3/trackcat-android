package de.mobcom.group3.gotrack;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import de.mobcom.group3.gotrack.LogIn.LoadScreenFragment;
import de.mobcom.group3.gotrack.LogIn.LogInFragment;

public class StartActivity extends AppCompatActivity {

    private FragmentTransaction fragTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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

