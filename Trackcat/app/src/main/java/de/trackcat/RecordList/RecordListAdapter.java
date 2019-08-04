package de.trackcat.RecordList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.trackcat.Database.Models.Route;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.MyViewHolder> {
    private Context context;
    private List<Route> records;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name, distance, time, date;
        public ImageView type, importedState, temp;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.record_id);
            name = view.findViewById(R.id.record_name);
            type = view.findViewById(R.id.activity_type);
            importedState = view.findViewById(R.id.imported_state);
            temp = view.findViewById(R.id.temp);
            distance = view.findViewById(R.id.record_distance);
            time = view.findViewById(R.id.record_time);
            date = view.findViewById(R.id.record_date);
        }
    }

    public RecordListAdapter(Context context, List<Route> cartList) {
        this.context = context;
        this.records = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ShowRecord.show(records, position, holder.id, holder.type, holder.importedState, holder.temp,holder.name, holder.distance, holder.time, holder.itemView, holder.date);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void removeItem(int position) {
        records.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Route item, int position) {
        records.add(position, item);
        notifyItemInserted(position);
    }

    /* SwipeRefresh - alle Einträge entfernen */
    public void clear() {
        records.clear();
        notifyDataSetChanged();
    }
}
