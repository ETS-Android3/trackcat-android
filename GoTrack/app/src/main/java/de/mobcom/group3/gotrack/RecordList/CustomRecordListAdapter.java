package de.mobcom.group3.gotrack.RecordList;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.R;

public class CustomRecordListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<Integer> listIds = new ArrayList<>();
    private ArrayList<String> listNames = new ArrayList<>();
    private ArrayList<Double> listDistances = new ArrayList<>();
    private ArrayList<Double> listTimes = new ArrayList<>();
    LayoutInflater inflater;

    public CustomRecordListAdapter(Activity context, List<Route> records) {
        super(context, R.layout.fragment_record_list);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Umwandlung in einzelne Listen
        for (int i = 0; i < records.size(); i++) {
            this.listIds.add(i+1);
            this.listNames.add(records.get(i).getName());
            this.listDistances.add(records.get(i).getDistance());
            this.listTimes.add(records.get(i).getTime());
        }
    }

    @Override
    public int getCount() {
        return listIds.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.record_list_one_item, parent, false);
        LinearLayout recordItem = view.findViewById(R.id.record_one_item);

        TextView recordId = recordItem.findViewById(R.id.record_id);
        recordId.setText("" + listIds.get(position));

        MapView map = recordItem.findViewById(R.id.record_map);
        map.setBuiltInZoomControls(false);

        TextView recordName = recordItem.findViewById(R.id.record_name);
        recordName.setText(listNames.get(position));

        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        recordDistance.setText(listDistances.get(position) + " km |");

        TextView recordTime = recordItem.findViewById(R.id.record_time);
        recordTime.setText(listTimes.get(position) + " Minuten");

        return view;
    }
}
