package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.mobcom.group3.gotrack.R;

public class RecordDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Auslesen der Daten aus dem Bundle */
        double[] altitudeArray = getArguments().getDoubleArray("altitudeArray");
        double[] speedArray = getArguments().getDoubleArray("speedArray");
        int id = getArguments().getInt("id");

        /* neues Fragment erstellen */
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("altitudeArray", altitudeArray);
        bundle.putDoubleArray("speedArray", speedArray);
        bundle.putInt("id", id);
        RecordListDetailsPageViewer recordListDetailsPageViewer = new RecordListDetailsPageViewer();
        recordListDetailsPageViewer.setArguments(bundle);
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.pageViewerContainer, recordListDetailsPageViewer, getResources().getString(R.string.fRecordDetailsList));
        fragTransaction.commit();

        View view = inflater.inflate(R.layout.fragment_record_details, container, false);
        return view;
    }
}
