package de.mobcom.group3.gotrack.Dashboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.RecordFragment;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private FragmentTransaction fragTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.summaryContainer, new SummaryListFragment(), "Summary");
        fragTransaction.replace(R.id.chartContainer, new PageViewerCharts(), "PageViewer");
        fragTransaction.commit();

        /*Funktionen hinter dem Schnellwechsel-Button*/
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FloatingActionButton fabButton = view.findViewById(R.id.fabButton);
        fabButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                Toast.makeText(getContext(), "Wechseln auf Aufnahme", Toast.LENGTH_LONG).show();
                RecordFragment recordFragment = MainActivity.getInstance().getRecordFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainFrame, recordFragment);
                fragmentTransaction.commit();
                break;
        }
    }
}
