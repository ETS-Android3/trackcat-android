package com.example.finnl.gotrack.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finnl.gotrack.Charts.BarChartFragment;
import com.example.finnl.gotrack.Charts.LineChartFragment;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Recording.Recording_UI.PageViewer;

public class DashboardFragment extends Fragment {
    private FragmentTransaction fragTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragTransaction = getChildFragmentManager().beginTransaction();

        fragTransaction.replace(R.id.chartContainer, new PageViewerCharts(), "PageViewer");
/*
        // Summary dem Dashboard hinzufügen
        fragTransaction.replace(R.id.summaryContainer, new BarChartFragment(), "BAR_CHART");

        // LineChart dem Dashboard hinzufügen
        fragTransaction.replace(R.id.chartContainer, new LineChartFragment(), "LINE_CHART");
 */
        // Änderungen zusammenfassen + Dashboard aufbauen
        fragTransaction.commit();

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}
