package de.mobcom.group3.gotrack.RecordList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Settings.NewUserFragment;

public class CustomRecordListAdapter extends ArrayAdapter<String> {

    private List<Route> records;
    LayoutInflater inflater;

    public CustomRecordListAdapter(Activity context, List<Route> records) {
        super(context, R.layout.fragment_record_list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.records = records;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.record_list_one_item, parent, false);
        LinearLayout recordItem = view.findViewById(R.id.record_one_item);

        TextView recordId = recordItem.findViewById(R.id.record_id);
        recordId.setText("" + (position + 1));

        ImageView recordType = recordItem.findViewById(R.id.activity_type);
        recordType.setImageResource(R.drawable.activity_running_record_list);

        TextView recordName = recordItem.findViewById(R.id.record_name);
        recordName.setText(records.get(position).getName());

        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        double distance = Math.round(records.get(position).getDistance());
        if (distance >= 1000) {
            String d = "" + distance / 1000L;
            recordDistance.setText(d.replace('.', ',') + " km |");
        } else {
            recordDistance.setText((int) distance + " m |");
        }

        TextView recordTime = recordItem.findViewById(R.id.record_time);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        String time = df.format(new Date(records.get(position).getTime() * 1000));
        recordTime.setText(time);

        /* Anzeigen der Routendetaills */
        recordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Daten holen*/
                ArrayList<Location> locations = records.get(position).getLocations();
                double[] speedValues = new double[locations.size()];
                double[] altitudeValues = new double[locations.size()];
                for (int i = 0; i < locations.size(); i++) {

                        Location location = locations.get(i);
                        speedValues[i] = location.getSpeed();
                        altitudeValues[i] = location.getAltitude();

                }
 //Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzahl: "+altitudeValues.length, Toast.LENGTH_LONG).show();
                /*Neues Fragment erstellen*/
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("altitudeArray", altitudeValues);
                bundle.putDoubleArray("speedArray", speedValues);
                bundle.putInt("id", records.get(position).getId());
                RecordDetailsFragment recordDetailsFragment = new RecordDetailsFragment();
                recordDetailsFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, "Record DETAILS");
                fragTransaction.commit();
                //  Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme von ID: "+records.get(position).getId(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
