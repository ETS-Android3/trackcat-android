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

public class ImportActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_activity);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            try {
                File file = new File(getCacheDir(), "document");
                InputStream inputStream=getContentResolver().openInputStream(uri);
                Import.getImport().handleSend(this, file, inputStream);
                //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
            catch (Exception ex) {
                Toast.makeText(this,
                        "Die Datei konnte nicht importiert werden", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
        else {
            Log.i("Import", "Der Intent war kein Import : " + action);
        }
        if (MainActivity.isActiv) {
            Toast.makeText(this,"GoTrack läuft bereits", Toast.LENGTH_LONG).show();
            finish();
        } else {
            openApp(this, "de.mobcom.group3.gotrack");
            finish();
        }
    }
    public void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
