package de.mobcom.group3.gotrack.RecordList;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Statistics.SpeedAverager;

public class ShowRecord {

    public static void show(List<Route> records, int position, String TAG, TextView recordId, ImageView recordType, ImageView importState, TextView recordName, TextView recordDostance, TextView recordTime, View recordItem){

        /* ID anzeigen */
        recordId.setText("" + (position + 1));

        /* Typ symbolisieren */
        recordType.setImageResource(SpeedAverager.getTypeIcon(records.get(position).getType(), true));

        /* Importstatus */
        if (records.get(position).isImported()) {
            importState.setVisibility(View.VISIBLE);
        } else {
            importState.setVisibility(View.INVISIBLE);
        }

        /* Name anzeigen */
        recordName.setText(records.get(position).getName());

        /* Distanz anzeigen */
        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        double distance = Math.round(records.get(position).getDistance());
        if (distance >= 1000) {
            String d = "" + distance / 1000L;
            recordDistance.setText(d.replace('.', ',') + " km |");
        } else {
            recordDistance.setText((int) distance + " m |");
        }

        /* Zeit anzeigen */
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        String time = df.format(new Date(records.get(position).getTime() * 1000));
        recordTime.setText(time);

        /* Anzeigen der Routendetaills */
        recordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Daten holen */
                ArrayList<Location> locations = records.get(position).getLocations();
                int size;
                int run;
                /* Überprüfung ob zu wenig Daten existieren */
                boolean fillArguments = false;
                if (locations != null && locations.size() > 60) {
                    size = locations.size() / 10;
                    run = locations.size();
                } else {
                    size = 5;
                    run = 0;
                    fillArguments = true;
                }
                int n = 0;
                double[] speedValues = new double[size + 1];
                double[] altitudeValues = new double[size + 1];
                for (int i = 0; i < run; i += 10) {

                    Location location = locations.get(i);
                    speedValues[n] = location.getSpeed() * 3.931;
                    altitudeValues[n] = location.getAltitude();
                    n++;

                }
                /* Array auffüllen, falls zu wenig Argumente existieren */
                if (fillArguments) {
                    for (int i = n; i <= size; i++) {
                        speedValues[n] = 0.0;
                        altitudeValues[n] = 0.0;
                    }
                }

                /* Neues Fragment erstellen */
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("altitudeArray", altitudeValues);
                bundle.putDoubleArray("speedArray", speedValues);
                bundle.putInt("id", records.get(position).getId());

                RecordDetailsFragment recordDetailsFragment = new RecordDetailsFragment();
                recordDetailsFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, TAG);
                fragTransaction.commit();
                if (MainActivity.getHints()) {
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme  \"" + records.get(position).getName() + "\"", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
