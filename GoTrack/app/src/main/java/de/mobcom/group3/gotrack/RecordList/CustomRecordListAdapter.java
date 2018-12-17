package de.mobcom.group3.gotrack.RecordList;

import android.app.Activity;
import android.content.Context;
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

import java.lang.reflect.Method;
import java.util.List;
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
        recordId.setText("" + (position+1));

        ImageView recordType = recordItem.findViewById(R.id.activity_type);
        recordType.setImageResource(R.drawable.activity_running_record_list);

        TextView recordName = recordItem.findViewById(R.id.record_name);
        recordName.setText(records.get(position).getName());

        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        recordDistance.setText(records.get(position).getDistance() + " km |");

        TextView recordTime = recordItem.findViewById(R.id.record_time);
        recordTime.setText(records.get(position).getTime() + " Minuten");

        /* Anzeigen des Profilerstellungssfragment */
        recordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", records.get(position).getId());
                RecordDetailsFragment recordDetailsFragment = new RecordDetailsFragment();
                recordDetailsFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, "Record DETAILS");
                fragTransaction.commit();
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme von ID: "+records.get(position).getId(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
