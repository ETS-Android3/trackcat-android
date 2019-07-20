package de.trackcat.FriendsSystem.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.List;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendListAdapter;
import de.trackcat.FriendsSystem.FriendsViewerFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendSendQuestionsFragment extends Fragment {

    private UserDAO userDAO;
    private static FriendsViewerFragment parentFrag;
    private static View view;
    private static User currentUser;
    private static FriendListAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends_send_question, container, false);
        parentFrag = (FriendsViewerFragment) this.getParentFragment();


        /* create user DAO and get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Update page with SwipeRefresh */
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadPage();
                swipeContainer.setRefreshing(false);
            }
        });

        loadPage();

        return view;
    }

    public static void loadPage() {
        showSendFriendQuestions();
    }

    private static void showSendFriendQuestions() {
        /* check of friend questions */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */

        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.showMyFriendRequests(authString);

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

                    /* show friend questions if they exists */
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

                    /* update badget */
                    parentFrag.setBadgeText(3, "" + friends.length());

                    /* add entrys to view */
                    adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, true, true, true);
                    ListView friendListView = view.findViewById(R.id.friend_question_list);
                    friendListView.setAdapter(adapter);
                    friendListView.setSelection(MainActivity.getSendFriendQuestionIndex());

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
    }


}
