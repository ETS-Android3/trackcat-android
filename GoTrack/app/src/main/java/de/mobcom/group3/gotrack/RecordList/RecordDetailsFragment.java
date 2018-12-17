package de.mobcom.group3.gotrack.RecordList;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class RecordDetailsFragment  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_details, container, false);
        int id = getArguments().getInt("id");
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        Route record = dao.read(id);
        double [] speedValues=new double[record.getLocations().size()];
        for (int i = 0; i < record.getLocations().size(); i++) {
            Location location = record.getLocations().get(i);
            if (i > 1) {
                speedValues[i] = location.getAltitude();
            }
        }

        Bundle bundle = new Bundle();
        bundle.putDoubleArray("altitudeArray", speedValues);
        bundle.putDoubleArray("speedArray", speedValues);
        RecordPageViewerCharts recordPageViewerCharts = new RecordPageViewerCharts();
        recordPageViewerCharts.setArguments(bundle);
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.chartContainer, recordPageViewerCharts, "RecordPageViewerCharts");
        fragTransaction.commit();

        return view;
    }
}
