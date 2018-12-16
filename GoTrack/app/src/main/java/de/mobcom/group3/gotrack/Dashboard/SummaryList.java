package de.mobcom.group3.gotrack.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.RecordList.CustomRecordListAdapter;

public class SummaryList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readAll(MainActivity.getActiveUser());

        List<Route> mList = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.summaryRecordListAmount) && i < records.size(); i++){
            mList.add(records.get(i));
        }

        CustomRecordListAdapter adapter = new CustomRecordListAdapter(MainActivity.getInstance(), mList);
        ListView recordList = view.findViewById(R.id.record_list);
        recordList.setAdapter(adapter);
        return view;
    }
}