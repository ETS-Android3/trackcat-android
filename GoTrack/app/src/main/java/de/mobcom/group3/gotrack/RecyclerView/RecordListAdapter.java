package de.mobcom.group3.gotrack.RecyclerView;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.RecordList.RecordDetailsFragment;

import static android.support.constraint.Constraints.TAG;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.MyViewHolder> {
    private Context context;
    private List<Route> records;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, distance, time;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            distance = view.findViewById(R.id.description);
            time = view.findViewById(R.id.price);

            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public RecordListAdapter(Context context, List<Route> cartList) {
        this.context = context;
        this.records = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Route item = records.get(position);
        holder.name.setText(item.getName());
        holder.distance.setText("" + item.getDistance());
        holder.time.setText("" + item.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Daten holen*/
                ArrayList<Location> locations = records.get(position).getLocations();
                int size;
                /*Überprüfung ob zu wenig Daten existieren*/
                boolean fillArguments=false;
                if (locations.size()>60) {
                    size = locations.size() / 10;
                }else{
                    size=5;
                    fillArguments=true;
                }
                int n = 0;
                double[] speedValues = new double[size+1];
                double[] altitudeValues = new double[size+1];
                for (int i = 0; i <  locations.size(); i += 10) {

                    Location location = locations.get(i);
                    speedValues[n] = location.getSpeed()*3.931;
                    altitudeValues[n] = location.getAltitude();

                    Log.d("Schleifenwerte", "Speed: " + speedValues[n]);
                    Log.d("Schleifenwerte", "SpeedOriginal: " + location.getSpeed());
                    Log.d("Schleifenwerte", "Altitude: " + location.getAltitude());
                    n++;

                }
                /*Array auffüllen, falls zu wenig Argumente existieren*/
                if (fillArguments){
                    for (int i=n;i<=size;i++){
                        speedValues[n] = 0.0;
                        altitudeValues[n] = 0.0;
                        Log.d("Schleifenwerte", "Zusatzspeed: " + speedValues[n]);
                        Log.d("Schleifenwerte", "Zusatzaltitude: " + altitudeValues[n]);
                    }
                }

                Log.d("Schleifenwerte", "Size: " + size);
                Log.d("Schleifenwerte", "Locationsize: " + locations.size());

                /*Neues Fragment erstellen*/
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("altitudeArray", altitudeValues);
                bundle.putDoubleArray("speedArray", speedValues);
                bundle.putInt("id", records.get(position).getId());
                RecordDetailsFragment recordDetailsFragment = new RecordDetailsFragment();
                recordDetailsFragment.setArguments(bundle);
                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, recordDetailsFragment, TAG);
                fragTransaction.commit();
                if (MainActivity.getHints()) {
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Anzeigen der Aufnahme  \"" + records.get(position).getName()+"\"", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void removeItem(int position) {
        records.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Route item, int position) {
        records.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
