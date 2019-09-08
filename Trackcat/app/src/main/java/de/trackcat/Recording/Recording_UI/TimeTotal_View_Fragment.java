package de.trackcat.Recording.Recording_UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.trackcat.R;

public class TimeTotal_View_Fragment extends Fragment {

    public TimeTotal_View_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_record_time_total_view, container, false);
    }
}
