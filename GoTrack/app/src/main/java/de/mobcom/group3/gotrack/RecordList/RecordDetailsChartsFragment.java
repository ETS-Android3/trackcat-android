package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.mobcom.group3.gotrack.Charts.LineChartFragment;
import de.mobcom.group3.gotrack.R;

public class RecordDetailsChartsFragment extends Fragment {

    private FragmentTransaction fragTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Auslesen der Daten aus dem Bundle */
        double[] speedValues = getArguments().getDoubleArray("speedArray");
        double[] altitudeValues = getArguments().getDoubleArray("altitudeArray");

        /* Höhenmeterchart */
        Bundle bundleAltitide = new Bundle();
        bundleAltitide.putDoubleArray("array", altitudeValues);
        bundleAltitide.putString("title", "Höhenmeter");
        bundleAltitide.putString("rangeTitle", "m");

        LineChartFragment lineFragAltitude = new LineChartFragment();
        lineFragAltitude.setArguments(bundleAltitide);

        /* Speedchart */
        Bundle bundleSpeed = new Bundle();
        bundleSpeed.putDoubleArray("array", speedValues);
        bundleSpeed.putString("title", "Geschwindigkeit");
        bundleSpeed.putString("rangeTitle", "km/h");

        LineChartFragment lineFragSpeed = new LineChartFragment();
        lineFragSpeed.setArguments(bundleSpeed);

        /* Charts in Container einfügen */
        fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.speedContainer,lineFragSpeed , getResources().getString(R.string.fRecordDetailsChartSpeed));
        fragTransaction.replace(R.id.altitudeContainer, lineFragAltitude, getResources().getString(R.string.fRecordDetailsChartAltitude));
        fragTransaction.commit();

        View view = inflater.inflate(R.layout.fragment_record_details_charts, container, false);
        return view;
    }
}
