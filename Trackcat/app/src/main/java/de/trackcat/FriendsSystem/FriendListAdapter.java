package de.trackcat.FriendsSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendLiveFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendProfileFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.PublicPersonProfileFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListAdapter extends ArrayAdapter<String> {

    private List<CustomFriend> friends;
    public TextView name, email, registSince;
    LayoutInflater inflater;
    CircleImageView image, state;
    boolean newFriend;
    boolean friendQuestion;
    UserDAO userDAO;

    public FriendListAdapter(Activity context, List<CustomFriend> friends, boolean type, boolean friendQuestion) {
        super(context, R.layout.friend_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friends = friends;
        this.newFriend = type;
        this.friendQuestion = friendQuestion;

        /* create userDAOs */
        userDAO = new UserDAO(MainActivity.getInstance());
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (!newFriend) {

            /* Variablen erstellen */
            view = inflater.inflate(R.layout.friend_list_item, parent, false);
            name = view.findViewById(R.id.friend_name);
            email = view.findViewById(R.id.friend_email);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            // email.setText(friends.get(position).getEmail());

            /* Shows details of routes */
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                    alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.friendsOptionTitle));
                    alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendOptions), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            if (id == 0) {
                                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                fragTransaction.replace(R.id.mainFrame, new FriendProfileFragment(),
                                        MainActivity.getInstance().getResources().getString(R.string.fFriendProfile));
                                fragTransaction.commit();
                            }
                            if (id == 1) {
                                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                fragTransaction.replace(R.id.mainFrame, new FriendLiveFragment(),
                                        MainActivity.getInstance().getResources().getString(R.string.fFriendLiveView));
                                fragTransaction.commit();
                            }
                            if (id == 2) {

                            }
                        }
                    });

                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
        } else {
            /* find views */
            view = inflater.inflate(R.layout.new_friend_list_item, parent, false);
            name = view.findViewById(R.id.friend_name);
            registSince = view.findViewById(R.id.friend_regist_since);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);

            /* add values */
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            registSince.setText(GlobalFunctions.getDateWithTimeFromSeconds(friends.get(position).getDateOfRegistration(), "dd.MM.yyyy HH:MM"));
            state.setImageBitmap(GlobalFunctions.findLevel(friends.get(position).getTotalDistance()));
            /* set profile image */
            byte[] imgRessource = friends.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
            if (imgRessource != null && imgRessource.length > 3) {
                bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
            }
            image.setImageBitmap(bitmap);

            // ImageView addFriend = view.findViewById(R.id.add_friend);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                    alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.friendsOptionTitle));

                    if (!friendQuestion) {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.foreignOptions), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (id == 0) {
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.replace(R.id.mainFrame, new PublicPersonProfileFragment(),
                                            MainActivity.getInstance().getResources().getString(R.string.fPublicPersonProfile));
                                    fragTransaction.commit();
                                }
                                if (id == 1) {

                                    /* addFriend */
                                    addFriend(friends.get(position).getId());
                                }
                            }
                        });
                    } else {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendQuestionOptions), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (id == 0) {
                                    /* addFriend */
                                    addFriend(friends.get(position).getId());
                                }
                            }
                        });
                    }
                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
        }
        // ShowRecord.show(records, position, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsDashbaord), recordId, recordType, importState, recordName, recordDistance, recordTime, recordItem, recordDate);
        return view;
    }

    private void addFriend(int friendId) {
        /* create hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put("friendId", "" + friendId);

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.requestFriend(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    /* get jsonString from API */
                    String jsonString = response.body().string();

                    /* parse json */
                    JSONObject mainObject = new JSONObject(jsonString);

                    /* open activity if login success*/
                    if (mainObject.getString("success").equals("0")) {
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
            }
        });
    }
}
