package de.mobcom.group3.gotrack.RecordList;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.mobcom.group3.gotrack.CustomLocation;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Statistics.SpeedAverager;

public class ShowRecord {

    public static void show(List<Route> records, int position, String TAG, TextView recordId, ImageView recordType, ImageView importState, TextView recordName, TextView recordDostance, TextView recordTime, View recordItem){

        /* show ID */
        recordId.setText("" + (position + 1));

        /* symbolize type */
        recordType.setImageResource(SpeedAverager.getTypeIcon(records.get(position).getType(), true));

        /* import status */
        if (records.get(position).isImported()) {
            importState.setVisibility(View.VISIBLE);
        } else {
            importState.setVisibility(View.INVISIBLE);
        }

        /* Show name */
        recordName.setText(records.get(position).getName());

        /* Show Distance */
        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        double distance = Math.round(records.get(position).getDistance());
        if (distance >= 1000) {
            String d = "" + distance / 1000L;
            recordDistance.setText(d.replace('.', ',') + " km |");
        } else {
            recordDistance.setText((int) distance + " m |");
        }

        /* Show Time */
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        String time = df.format(new Date(records.get(position).getTime() * 1000));
        recordTime.setText(time);

        /* Shows details of routes */
        recordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* get Location Data */
                ArrayList<CustomLocation> locations = records.get(position).getLocations();
                int size;
                int run;
                int step;

                /* defines Steps and amount of values depending on available locations */
                if (locations != null && locations.size() > 6000) {
                    size = (locations.size() / 1000);
                    run = locations.size();
                    step = 1000;
                }else if (locations != null && locations.size() > 600) {
                    size = (locations.size() / 100);
                    run = locations.size();
                    step = 100;
                }else if (locations != null && locations.size() > 60) {
                    size = (locations.size() / 10);
                    run = locations.size();
                    step = 10;
                } else if(locations != null && locations.size() > 30){
                    size = (locations.size() / 5);
                    run = locations.size();
                    step = 5;
                }else if ( locations != null && locations.size() > 10){
                    size = (locations.size() / 2);
                    run = locations.size();
                    step = 2;
                }else{
                    size = locations.size() - 1;
                    run = locations.size();
                    step = 1;
                }

                int n = 0;
                double[] speedValues = new double[size + 1];
                double[] altitudeValues = new double[size + 1];
                for (int i = 0; i < run; i += step) {
                    CustomLocation location = locations.get(i);
                    speedValues[n] = location.getSpeed() * 3.931;
                    altitudeValues[n] = location.getAltitude();
                    n++;
                }

                /* Create new Fragment and put bundle */
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("altitudeArray", altitudeValues);
                bundle.putDoubleArray("speedArray", speedValues);
                bundle.putInt("id", records.get(position).getId());

                RecordDetailsFragment recordDetailsFragment = new RecordDetailsFragment();
                recordDetailsFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsDashbaord));
                fragTransaction.commit();
                if (MainActivity.getHints()) {
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme  \"" + records.get(position).getName() + "\"", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
