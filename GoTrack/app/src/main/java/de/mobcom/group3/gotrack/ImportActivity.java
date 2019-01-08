package de.mobcom.group3.gotrack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import de.mobcom.group3.gotrack.InExport.Import;

/* Diese Klasse wird ausgeführt wenn eine Datei aus einer anderen App importiert wird */
public class ImportActivity extends Activity {
    /* In der onCreate-Methode werden Datei,
     sofern sie existieren an die Import-Klasse weitergegeben */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        /* Import der Datei */
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            try {
                File file = new File(getCacheDir(), "document");
                InputStream inputStream=getContentResolver().openInputStream(uri);
                Import.getImport().handleSend(this, file, inputStream);
                Log.i("GoTrack-Import", "Import einer Datei gestartet.");
                Toast.makeText(this,"Import wird ausgeführt", Toast.LENGTH_LONG).show();
            }
            catch (Exception ex) {
                Toast.makeText(this,
                        "Die Datei konnte nicht importiert werden", Toast.LENGTH_LONG).show();
                Log.e("GoTrack-Import", "Die Datei konnte nicht importiert werden");
                Log.e("GoTrack-Import", ex.toString());
            }
        }
        else {
            Log.i("GoTrack-Import", "Der Intent war kein Import : " + action);
        }
        if (MainActivity.isActiv) {
            /* Diese App-Instanz wird beendet da die App bereits in einer anderen Instanz läuft */
            Toast.makeText(this,"GoTrack wird bereits in einer" +
                    " anderen Instanz ausgeführt", Toast.LENGTH_LONG).show();
            Log.i("GoTrack-Import", "GoTrack wird bereits in einer" +
                    " anderen Instanz ausgeführt");
            MainActivity.getInstance().addItemsToSpinner();
            finish();
        } else {
            /* Die App kann gestartet werden da sie derzeit nicht läuft */
            Log.i("GoTrack-Import",
                    "GoTrack wird nicht ausgeführt und wird daher gestartet");
            openApp(this, "de.mobcom.group3.gotrack");
            finish();
        }
    }

    /* Durch diese Methode wird die eingenständige Version der diese App gestartet werden */
    public void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            Log.i("GoTrack-Import",
                    "Das Laden der App war erfolgreich");
        } catch (ActivityNotFoundException ex) {
            Log.e("GoTrack-Import", ex.toString());
            Log.e("GoTrack-Import",
                    "Das Laden der App war nicht erfolgreich");
        }
    }
}
