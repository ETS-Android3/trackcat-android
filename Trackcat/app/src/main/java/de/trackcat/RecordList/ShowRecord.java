package de.trackcat.RecordList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.Database.DAO.LocationDAO;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Statistics.SpeedAverager;

public class ShowRecord {

    public static void show(List<Route> records, int position, String TAG, TextView recordId, ImageView recordType, ImageView importState, TextView recordName, TextView recordDostance, TextView recordTime, View recordItem, TextView recordDate) {

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

        /* Show Date */
        long curDate = records.get(position).getDate();
        String curDateString = getDate(curDate, "dd.MM.yyyy");
        recordDate.setText(curDateString);

        /* Shows details of routes */
        recordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* get Location Data */
                LocationDAO locationDao = new LocationDAO(MainActivity.getInstance());
                List<Location> locations = locationDao.readAll(records.get(position).getId());
                int size;
                int run;
                int step;

                /* defines Steps and amount of values depending on available locations */

                if (locations != null && locations.size() > 100) {
                    size = (locations.size() / 10);
                    run = locations.size();
                    step = 10;
                } else {
                    size = locations.size();
                    run = locations.size();
                    step = 1;
                }

                int n = 0;
                double[] speedValues = new double[size + 1];
                double[] altitudeValues = new double[size + 1];
                for (int i = 0; i < run; i += step) {
                    Location location = locations.get(i);
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
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, TAG);
                fragTransaction.commit();
                if (MainActivity.getHints()) {
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme  \"" + records.get(position).getName() + "\"", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* Das Datum wird von Millisekunden als Formatiertes Datum zurückgegeben */
    private static String getDate(long millis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }
}
