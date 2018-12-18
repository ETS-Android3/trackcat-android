package de.mobcom.group3.gotrack.RecordList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class RecordDetailsInformationFragment extends Fragment {
    public RecordDetailsInformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int id = getArguments().getInt("id");
        View view = inflater.inflate(R.layout.fragment_record_details_information, container, false);
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        Route record = dao.read(id);

        Toast.makeText(getContext(), "Hallo ID: " + id, Toast.LENGTH_LONG).show();

        /*DateFormat setzen*/
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);

        /*Name setzen*/
        TextView name_TextView = view.findViewById(R.id.name_TextView);
        String toSet = record.getName();
        name_TextView.setText(toSet);

        /*Average Speed setzen*/
        TextView average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
        toSet = Math.round(((record.getDistance() / record.getTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        average_speed_TextView.setText(toSet);

        /*Real Average Speed setzen*/
        TextView real_average_speed_TextView = view.findViewById(R.id.real_average_speed_TextView);
        toSet = Math.round(((record.getDistance() / record.getRideTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        real_average_speed_TextView.setText(toSet);


        /*Distance setzen*/
        TextView distance_TextView = view.findViewById(R.id.distance_TextView);
        toSet = Math.round(record.getDistance()) / 1000.0 + " km";
        distance_TextView.setText(toSet);

        /*
         * Set altimeter

        TextView altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
        toSet = "± " + Math.round(height) + " m";
        altimeter_TextView.setText(toSet);
* */
        /*TotalTime setzen*/
        TextView total_time_TextView = view.findViewById(R.id.total_time_TextView);
        String time = df.format(new Date(record.getTime() * 1000));
        total_time_TextView.setText(time);


        /*RideTime setzen*/
        TextView real_time_TextView = view.findViewById(R.id.real_time_TextView);
        time = df.format(new Date(record.getRideTime() * 1000));
        real_time_TextView.setText(time);


        /*
         * Set Max Speed

        TextView max_speed_TextView = view.findViewById(R.id.max_speed_TextView);
        toSet = (Math.round(maxSpeed * 60 * 60) / 100) / 10.0 + " km/h";
        max_speed_TextView.setText(toSet);
 * */

        return view;
    }
}
