package de.trackcat.FriendsSystem.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendListAdapter;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.SwipeControll.RecordListAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendsFragment extends Fragment {

    private UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        /* create user DAO */
        userDAO= new UserDAO(MainActivity.getInstance());

        /* check of friend questions */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.showFriendRequest(authString);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                /* get jsonString from API */
                String jsonString = null;

                try {
                    jsonString = response.body().string();

                    /* parse json */
                    JSONArray friends = new JSONArray(jsonString);

                    List<CustomFriend> friendList = new ArrayList<>();

                    for (int i = 0; i < friends.length(); i++) {
                        CustomFriend friend = new CustomFriend();
                        friend.setFirstName(((JSONObject) friends.get(i)).getString("firstName"));
                        friend.setLastName(((JSONObject) friends.get(i)).getString("lastName"));
                        friend.setDateOfRegistration(((JSONObject) friends.get(i)).getLong("dateOfRegistration"));
                        friend.setImage(GlobalFunctions.getBytesFromBase64(((JSONObject) friends.get(i)).getString("image")));
                        friend.setTotalDistance(((JSONObject) friends.get(i)).getLong("totalDistance"));
                        friend.setId(((JSONObject) friends.get(i)).getInt("id"));
                        friendList.add(friend);
                    }
                    FriendListAdapter adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, true);
                    ListView friendListView = view.findViewById(R.id.friend_list);
                    friendListView.setAdapter(adapter);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });

        List<CustomFriend> friendList= new ArrayList<>();

        CustomFriend friend1= new CustomFriend();
        friend1.setFirstName("Max");
        friend1.setLastName("Mustermann");
        //friend1.setEmail("max@mustermann.de");
        friendList.add(friend1);

        CustomFriend friend2= new CustomFriend();
        friend2.setFirstName("Mimi");
        friend2.setLastName("Mensch");
        //friend2.setEmail("mimi@mensch.de");
        friendList.add(friend2);

        CustomFriend friend3= new CustomFriend();
        friend3.setFirstName("Manfred");
        friend3.setLastName("Walter");
      //  friend3.setEmail("manfred@walter.de");
        friendList.add(friend3);

        FriendListAdapter adapter = new FriendListAdapter(MainActivity.getInstance(),friendList, false);
        ListView friendListView = view.findViewById(R.id.friend_list);
        friendListView.setAdapter(adapter);

        return view;
    }


}
