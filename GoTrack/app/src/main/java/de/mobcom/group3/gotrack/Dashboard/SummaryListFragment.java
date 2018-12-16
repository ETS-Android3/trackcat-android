package de.mobcom.group3.gotrack.Dashboard;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.RecordList.CustomRecordListAdapter;
import de.mobcom.group3.gotrack.RecordList.RecordListFragment;

public class SummaryListFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readAll(MainActivity.getActiveUser());

        List<Route> mList = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.summaryRecordListAmount) && i < records.size(); i++) {
            mList.add(records.get(i));
        }

        CustomRecordListAdapter adapter = new CustomRecordListAdapter(MainActivity.getInstance(), mList);
        ListView recordList = view.findViewById(R.id.record_list);
        recordList.setAdapter(adapter);

        LinearLayout showMore = view.findViewById(R.id.show_more_records);
        showMore.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_more_records:
                FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new RecordListFragment(), getResources().getString(R.string.fRecordlist));
                fragTransaction.commit();

                /* Menüvermerk festlegen */
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.nav_recordlist).setChecked(true);
                break;
        }
    }
}