package de.mobcom.group3.gotrack;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.mobcom.group3.gotrack.InExport.Import;

public class ImportActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_record_details);
        Toast.makeText(this,"bis hierher-2", Toast.LENGTH_LONG).show();
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            Toast.makeText(this,"bis hierher-1", Toast.LENGTH_LONG).show();
            try {
                File f = handleSend(uri);
                FileInputStream is = new FileInputStream(f);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String fileText = new String(buffer);
                Toast.makeText(this,"bis hierher0", Toast.LENGTH_LONG).show();
                Import.getImport().incomingImport(this, fileText);
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
        Toast.makeText(this, "GoTrack Close", Toast.LENGTH_LONG).show();
        finish();
        if (MainActivity.isActiv) {
            finish();
        } else {
            Intent start = new Intent(this, MainActivity.class);
            startActivity(start);
        }
    }
    public File handleSend(Uri imageUri) throws IOException {
            File file = new File(getCacheDir(), "document");
            InputStream inputStream=getContentResolver().openInputStream(imageUri);
            try {
                OutputStream output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                } finally {
                    output.close();
                }
            } finally {
                inputStream.close();
            }
        return file;
    }
}
