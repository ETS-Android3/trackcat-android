package de.trackcat.RecordList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.trackcat.Database.DAO.LocationTempDAO;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Statistics.SpeedAverager;

public class ShowRecord {

    public static void show(List<Route> records, int position, String TAG, TextView recordId, ImageView recordType, ImageView importState, ImageView temp,TextView recordName, TextView recordDostance, TextView recordTime, View recordItem, TextView recordDate) {

        /* show ID */
        recordId.setText("" + (position + 1));

        /* symbolize type */
        recordType.setImageResource(SpeedAverager.getTypeIcon(records.get(position).getType(), true));

        /* import status */
        if (records.get(position).isTemp()) {
            temp.setVisibility(View.VISIBLE);
        } else {
            temp.setVisibility(View.INVISIBLE);
        }

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
                List<Location> locations = new ArrayList<>();
                if (records.get(position).isTemp()){
                    LocationTempDAO locationTempDAO = new LocationTempDAO(MainActivity.getInstance());
                    locations = locationTempDAO.readAll(records.get(position).getId());

                }else{
                    JSONArray locationArray = null;
                    try {
                        locationArray = new JSONArray(records.get(position).getLocations());

                        for ( int i=0;i< locationArray.length();i++) {
                            Location location= new Location();
                            location.setRecordId(((JSONObject) locationArray.get(i)).getInt("recordId"));
                            location.setLatitude(((JSONObject) locationArray.get(i)).getDouble("latitude"));
                            location.setLongitude(((JSONObject) locationArray.get(i)).getDouble("longitude"));
                            location.setAltitude(((JSONObject) locationArray.get(i)).getDouble("altitude"));
                            location.setTime(((JSONObject) locationArray.get(i)).getLong("time"));
                            location.setSpeed((float)((JSONObject) locationArray.get(i)).getDouble("speed"));
                            locations.add(location);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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

                ArrayList<Location> locationsArrayList = new ArrayList<>(locations.size());
                locationsArrayList.addAll(locations);

                String locationsAsString = new Gson().toJson(locations);

                /* Create new Fragment and put bundle */
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("altitudeArray", altitudeValues);
                bundle.putDoubleArray("speedArray", speedValues);
                bundle.putString("locations", locationsAsString);
                bundle.putInt("id", records.get(position).getId());
                bundle.putBoolean("temp", records.get(position).isTemp());

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
