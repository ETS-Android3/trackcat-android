package de.trackcat.FriendsSystem.Tabs;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendListAdapter;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FindFriendsFragment extends Fragment implements View.OnKeyListener {

    EditText findFriend;
    private UserDAO userDAO;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends_find, container, false);

        userDAO = new UserDAO(MainActivity.getInstance());

        /* find view */
        findFriend = view.findViewById(R.id.findFriend);
        findFriend.setOnKeyListener(this);


        return view;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            String find = findFriend.getText().toString();
            Toast.makeText(getContext(), "Suche nach '" + find + "' gestartet.", Toast.LENGTH_SHORT).show();


            HashMap<String, String> map = new HashMap<>();
            map.put("search", "" + find);
            map.put("page", "1");


            Retrofit retrofit = APIConnector.getRetrofit();
            APIClient apiInterface = retrofit.create(APIClient.class);

            /* start a call */
            User currentUser = userDAO.read(MainActivity.getActiveUser());
            String base = currentUser.getMail() + ":" + currentUser.getPassword();
            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

            Call<ResponseBody> call = apiInterface.findFriend(authString, map);

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


            return true;
        }
        return false;
    }
}
