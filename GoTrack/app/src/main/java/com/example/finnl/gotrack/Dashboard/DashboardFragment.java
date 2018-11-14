package com.example.finnl.gotrack.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.finnl.gotrack.Charts.LineChartFragment;
import com.example.finnl.gotrack.R;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();

        // LineChart dem Dashboar hinzufügen
        fragTransaction.replace(R.id.summaryContainer, new SummaryFragment(), "SUMMARY");

        // LineChart dem Dashboar hinzufügen
        fragTransaction.replace(R.id.chartContainer, new LineChartFragment(), "LINE_CHART");

        // Änderungen zusammenfassen + Dashboard aufbauen
        fragTransaction.commit();
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}
