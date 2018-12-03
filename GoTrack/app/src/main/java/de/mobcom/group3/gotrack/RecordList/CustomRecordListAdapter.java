package de.mobcom.group3.gotrack.RecordList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.R;

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

        return view;
    }
}
