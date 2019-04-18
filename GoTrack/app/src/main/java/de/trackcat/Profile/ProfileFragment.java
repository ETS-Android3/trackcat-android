package de.trackcat.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        boolean loadMenu = getArguments().getBoolean("loadMenu");

        if(loadMenu){
            /* Inlate Menu */
            MenuInflater menuInflater = MainActivity.getInstance().getMenuInflater();
            menuInflater.inflate(R.menu.profile_settings, MainActivity.getMenuInstance());
        }

        return view;
    }
}
