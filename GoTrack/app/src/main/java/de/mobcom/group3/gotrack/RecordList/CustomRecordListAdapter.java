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
import de.mobcom.group3.gotrack.MainActivity;
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

        /* Variablen erstellen */
        View view = inflater.inflate(R.layout.summary_list_item, parent, false);
        LinearLayout recordItem = view.findViewById(R.id.record_one_item);
        TextView recordId = recordItem.findViewById(R.id.record_id);
        ImageView recordType = recordItem.findViewById(R.id.activity_type);
        ImageView importState = recordItem.findViewById(R.id.imported_state);
        TextView recordName = recordItem.findViewById(R.id.record_name);
        TextView recordDistance = recordItem.findViewById(R.id.record_distance);
        TextView recordTime = recordItem.findViewById(R.id.record_time);

        ShowRecord.show(records, position, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsDashbaord), recordId, recordType, importState, recordName, recordDistance, recordTime, recordItem);
        return view;
    }
}
