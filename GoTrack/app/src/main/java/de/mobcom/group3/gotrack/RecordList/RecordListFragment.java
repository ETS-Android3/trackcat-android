package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
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
        //TODO DYnamische Umsetzung anhand der UserID
        List<Route> records = dao.readAll(1);

        for (Route record:records) {
            TextView recordName = view.findViewById(R.id.recordName);
            recordName.setText(record.getName());

            TextView recordDistance = view.findViewById(R.id.recordDistance);
            recordDistance.setText(("" + record.getDistance()));

            TextView recordTime = view.findViewById(R.id.recordTime);
            recordTime.setText("" + record.getTime());
        }
        return view;
    }
}
