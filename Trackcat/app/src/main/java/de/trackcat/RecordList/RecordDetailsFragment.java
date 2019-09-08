package de.trackcat.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.trackcat.R;

public class RecordDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Read data from bundle */
        double[] altitudeArray = getArguments().getDoubleArray("altitudeArray");
        double[] speedArray = getArguments().getDoubleArray("speedArray");
        String locationsAsString = getArguments().getString("locations");
        int id = getArguments().getInt("id");
        boolean temp = getArguments().getBoolean("temp");

        /* Create new fragment */
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("altitudeArray", altitudeArray);
        bundle.putDoubleArray("speedArray", speedArray);
        bundle.putString("locations", locationsAsString);
        bundle.putBoolean("temp", temp);
        bundle.putInt("id", id);
        RecordListDetailsPageViewer recordListDetailsPageViewer = new RecordListDetailsPageViewer();
        recordListDetailsPageViewer.setArguments(bundle);
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.pageViewerContainer, recordListDetailsPageViewer, getResources().getString(R.string.fRecordDetails));
        fragTransaction.commit();

        View view = inflater.inflate(R.layout.fragment_record_details, container, false);
        return view;
    }
}
