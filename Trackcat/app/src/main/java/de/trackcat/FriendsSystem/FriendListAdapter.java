package de.trackcat.FriendsSystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {
    private Context context;
    private List<CustomFriend> friends;
    public TextView name, email;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.friend_name);
            email = view.findViewById(R.id.friend_email);
        }
    }

    public FriendListAdapter(Context context, List<CustomFriend> friendsList) {
        this.context = context;
        this.friends = friendsList;
    }

    @Override
    public FriendListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item, parent, false);
        return new FriendListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendListAdapter.MyViewHolder holder, final int position) {

        name.setText(friends.get(position).getFirstName()+" "+friends.get(position).getLastName());
        email.setText(friends.get(position).getEmail());

       // ShowRecord.show(records, position, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsList), holder.id, holder.type, holder. importedState, holder.name, holder.distance, holder.time, holder.itemView, holder.date);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void removeItem(int position) {
        friends.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(CustomFriend item, int position) {
        friends.add(position, item);
        notifyItemInserted(position);
    }

    /* SwipeRefresh - alle Einträge entfernen */
    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }
}
