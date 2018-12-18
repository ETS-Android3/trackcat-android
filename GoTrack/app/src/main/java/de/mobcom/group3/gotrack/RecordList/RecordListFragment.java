package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.List;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class RecordListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readAll(MainActivity.getActiveUser());

        CustomRecordListAdapter adapter = new CustomRecordListAdapter(MainActivity.getInstance(), records, getResources().getString(R.string.fRecordDetailsList));
        ListView recordList = (ListView) view.findViewById(R.id.record_list);
        recordList.setAdapter(adapter);

        return view;
    }
}
