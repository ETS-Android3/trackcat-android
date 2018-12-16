package de.mobcom.group3.gotrack.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.mobcom.group3.gotrack.R;

public class DashboardFragment extends Fragment {
    private FragmentTransaction fragTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.summaryContainer, new SummaryListFragment(), "Summary");
        fragTransaction.replace(R.id.chartContainer, new PageViewerCharts(), "PageViewer");
/*
        // Summary dem Dashboard hinzufügen
        fragTransaction.replace(R.id.summaryContainer, new BarChartFragment(), "BAR_CHART");

        // LineChart dem Dashboard hinzufügen
        fragTransaction.replace(R.id.chartContainer, new LineChartFragment(), "LINE_CHART");
 */
        // Änderungen zusammenfassen + Dashboard aufbauen
        fragTransaction.commit();

        /*Floating ActionButton
        View view;
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FrameLayout item = view.findViewById(R.id.chartContainer);
        FloatingActionButton btn = item.findViewById(R.id.fabButton);
        btn.setOnClickListener(view1 -> {
            FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.mainFrame, new RecordFragment(), "RECORD");
            fragTransaction.commit();
        });*/

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}
