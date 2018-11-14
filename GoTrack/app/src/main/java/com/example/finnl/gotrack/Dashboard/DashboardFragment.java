package com.example.finnl.gotrack.Dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finnl.gotrack.Charts.LineChartFragment;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Recording.Recording_UI.PageViewer;

public class DashboardFragment extends Fragment {


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.chartContainer, new LineChartFragment(), "LineChart");
        fragTransaction.commit();

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}
