package de.trackcat.FriendsSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import de.trackcat.FriendsSystem.Tabs.FindFriendsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListAdapter extends ArrayAdapter<String> implements View.OnClickListener{

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

            /* add name and regist since */
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            email.setText(friends.get(position).getEmail());

            /* find level */
            double distance = Math.round(friends.get(position).getTotalDistance());
            double levelDistance;
            if (distance >= 1000) {
                levelDistance = distance / 1000L;
            } else {
                levelDistance = distance / 1000;
            }
            state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

            /* set profile image */
            byte[] imgRessource = friends.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
            if (imgRessource != null && imgRessource.length > 3) {
                bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
            }
            image.setImageBitmap(bitmap);

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
                                FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("friendId", friends.get(position).getId());
                                friendProfileFragment.setArguments(bundle);
                                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                fragTransaction.replace(R.id.mainFrame, friendProfileFragment,
                                        MainActivity.getInstance().getResources().getString(R.string.fFriendProfile));
                                fragTransaction.commit();
                            }
                            if (id == 1) {
                                FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                fragTransaction.replace(R.id.mainFrame, new FriendLiveFragment(),
                                        MainActivity.getInstance().getResources().getString(R.string.fFriendLiveView));
                                fragTransaction.commit();
                            }
                            /* delete friend */
                            if (id == 2) {
                                deleteFriend(friends.get(position).getId());
                            }
                        }
                    });

                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
        } else {
            /* find views */
            if(friends.size()% 10==0 && position==friends.size()-1){
                view = inflater.inflate(R.layout.new_friend_list_last_item, parent, false);
                Button loadMore= view.findViewById(R.id.loadMore);
                loadMore.setOnClickListener(this);
            }else{
                view = inflater.inflate(R.layout.new_friend_list_item, parent, false);
            }

            name = view.findViewById(R.id.friend_name);
            registSince = view.findViewById(R.id.friend_regist_since);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);

            /* add name and regist since */
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            registSince.setText(GlobalFunctions.getDateWithTimeFromSeconds(friends.get(position).getDateOfRegistration(), "dd.MM.yyyy"));

            /* find level */
            double distance = Math.round(friends.get(position).getTotalDistance());
            double levelDistance;
            if (distance >= 1000) {
                levelDistance = distance / 1000L;
            } else {
                levelDistance = distance / 1000;
            }
            state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

            /* set profile image */
            byte[] imgRessource = friends.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
            if (imgRessource != null && imgRessource.length > 3) {
                bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
            }
            image.setImageBitmap(bitmap);

            /* add on click listener */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                    alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.friendsOptionTitle));

                    /* friend list by no friend request */
                    if (!friendQuestion) {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.foreignOptions), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                /* show public profile */
                                if (id == 0) {
                                    PublicPersonProfileFragment publicPersonProfileFragment = new PublicPersonProfileFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("friendId", friends.get(position).getId());
                                    publicPersonProfileFragment.setArguments(bundle);
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.replace(R.id.mainFrame, publicPersonProfileFragment,
                                            MainActivity.getInstance().getResources().getString(R.string.fPublicPersonProfile));
                                    fragTransaction.commit();
                                }
                                /* addFriend */
                                if (id == 1) {
                                    addFriend(friends.get(position).getId());
                                }
                            }
                        });
                        /* friend list by friend request */
                    } else {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendQuestionOptions), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                /* addFriend */
                                if (id == 0) {
                                    addFriend(friends.get(position).getId());
                                }

                                /* delete friend */
                                if (id == 1) {
                                    deleteFriend(friends.get(position).getId());
                                }
                            }
                        });
                    }
                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
        }
        return view;
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    /* function to add a friend */
    private void addFriend(int friendId) {

        /* create hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put("friendId", "" + friendId);

        /* start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
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

                    /* friendship question okay */
                    if (mainObject.getString("success").equals("0")) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionOkay), Toast.LENGTH_SHORT).show();
                        /* friendship question error */
                    } else if (mainObject.getString("success").equals("1")) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionError), Toast.LENGTH_SHORT).show();
                        /* friendship question check */
                    } else if (mainObject.getString("success").equals("2")) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionCheck), Toast.LENGTH_SHORT).show();

                        /* load page new */
                        if(newFriend && friendQuestion){
                            FriendQuestionsFragment.loadPage();
                        }
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


    /* function to add a friend */
    private void deleteFriend(int friendId) {

        /* create AlertBox */
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        /* show friendQuestion delete text */
        if(newFriend) {
            alert.setTitle("Anfrage wirklich entfernen?");
            alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsQuestionDelete));

        }else{
            alert.setTitle("Freund wirklich entfernen?");
            alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsDelete));
        }

        alert.setPositiveButton("entfernen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /* create hashmap */
                HashMap<String, String> map = new HashMap<>();
                map.put("friendId", "" + friendId);

                /* start a call */
                Retrofit retrofit = APIConnector.getRetrofit();
                APIClient apiInterface = retrofit.create(APIClient.class);
                User currentUser = userDAO.read(MainActivity.getActiveUser());
                String base = currentUser.getMail() + ":" + currentUser.getPassword();
                String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

                Call<ResponseBody> call = apiInterface.deleteFriend(authString, map);
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            /* get jsonString from API */
                            String jsonString = response.body().string();

                            /* parse json */
                            JSONObject mainObject = new JSONObject(jsonString);

                            /* delete friend okay */
                            if (mainObject.getString("success").equals("0")) {
                                if(newFriend){
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteQuestionOkay), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteFriendOkay), Toast.LENGTH_SHORT).show();
                                }

                                /* delete friend error */
                            } else if (mainObject.getString("success").equals("1")) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteError), Toast.LENGTH_SHORT).show();
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
        });

        alert.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.loadMore:
            /*    page++;
                 search term
                search(searchTerm, page);*/
                FindFriendsFragment.search("ma",true, friends);
                clear();
                break;

        }
    }
}
