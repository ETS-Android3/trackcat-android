package de.trackcat.Dashboard;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.CustomRecordListAdapter;

public class SummaryListFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);
        LinearLayout showMore = view.findViewById(R.id.show_more_records);
        LinearLayout noEntries = view.findViewById(R.id.no_entries_alert);
        Button firstRecordBtn = view.findViewById(R.id.create_first_record);

        /* Get all records */
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readAll();

        RecordTempDAO tempDAO = new RecordTempDAO(MainActivity.getInstance());
        List<Route> tempRecords = tempDAO.readAll();

        for (Route route : tempRecords) {
            records.add(route);
        }

        List<Route> mList = new ArrayList<>();
        mList.clear();

        /* Add Elements to List */
        if (!records.isEmpty() && records.size() >= 2) {

            for (int i = 0; i < getResources().getInteger(R.integer.summaryRecordListAmount); i++) {
                mList.add(records.get(i));
            }
        } else if (!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                mList.add(records.get(i));
            }
        }

        /* Set list adapter */
        CustomRecordListAdapter adapter = new CustomRecordListAdapter(MainActivity.getInstance(), mList);
        ListView recordList = view.findViewById(R.id.record_list);
        recordList.setAdapter(adapter);

        if (mList.isEmpty()) {
            showMore.setVisibility(View.GONE);
            noEntries.setVisibility(View.VISIBLE);
        } else {
            showMore.setVisibility(View.VISIBLE);
            noEntries.setVisibility(View.GONE);
        }
        showMore.setOnClickListener(this);
        firstRecordBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        /* Instantiate menu */
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        /* ActionHandler */
        switch (v.getId()) {
            case R.id.show_more_records:
                MainActivity.getInstance().synchronizeRecords(true, false);

                /* Mark current menu point */
                menu.findItem(R.id.nav_recordlist).setChecked(true);
                break;
            case R.id.create_first_record:
                MainActivity.getInstance().loadRecord();

                /* Mark current menu point */
                menu.findItem(R.id.nav_record).setChecked(true);
                break;
        }
    }
}