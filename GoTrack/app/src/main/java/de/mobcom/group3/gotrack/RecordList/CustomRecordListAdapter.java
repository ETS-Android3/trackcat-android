package de.mobcom.group3.gotrack.RecordList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class CustomRecordListAdapter extends ArrayAdapter<String> {

    private List<Route> records;
    LayoutInflater inflater;
    String TAG;

    public CustomRecordListAdapter(Activity context, List<Route> records, String TAG) {
        super(context, R.layout.fragment_record_list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.records = records;
        this.TAG = TAG;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.summary_list_item, parent, false);
        LinearLayout recordItem = view.findViewById(R.id.record_one_item);

        TextView recordId = recordItem.findViewById(R.id.record_id);
        recordId.setText("" + (position + 1));

        ImageView recordType = recordItem.findViewById(R.id.activity_type);
        // TODO: Dynamische Implementation des Typen anhand von Datenbankwerten...
        switch(records.get(position).getType()){
            case 0:
                recordType.setImageResource(R.drawable.activity_running_record_list);
                break;
            case 1:
                recordType.setImageResource(R.drawable.activity_biking_record_list);
                break;
            case 2:
                recordType.setImageResource(R.drawable.activity_caring_record_list);
                break;
        }

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
                int size;
                /*Überprüfung ob zu wenig Daten existieren*/
                boolean fillArguments=false;
                if (locations.size()>60) {
                    size = locations.size() / 10;
                }else{
                    size=5;
                    fillArguments=true;
                }
                int n = 0;
                double[] speedValues = new double[size+1];
                double[] altitudeValues = new double[size+1];
                for (int i = 0; i <  locations.size(); i += 10) {

                    Location location = locations.get(i);
                    speedValues[n] = location.getSpeed()*3.931;
                    altitudeValues[n] = location.getAltitude();

                    Log.d("Schleifenwerte", "Speed: " + speedValues[n]);
                    Log.d("Schleifenwerte", "SpeedOriginal: " + location.getSpeed());
                    Log.d("Schleifenwerte", "Altitude: " + location.getAltitude());
                    n++;

                }
                /*Array auffüllen, falls zu wenig Argumente existieren*/
                if (fillArguments){
                    for (int i=n;i<=size;i++){
                        speedValues[n] = 0.0;
                        altitudeValues[n] = 0.0;
                        Log.d("Schleifenwerte", "Zusatzspeed: " + speedValues[n]);
                        Log.d("Schleifenwerte", "Zusatzaltitude: " + altitudeValues[n]);
                    }
                }

                Log.d("Schleifenwerte", "Size: " + size);
                Log.d("Schleifenwerte", "Locationsize: " + locations.size());

                /*Neues Fragment erstellen*/
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
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme  \"" + records.get(position).getName()+"\"", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
