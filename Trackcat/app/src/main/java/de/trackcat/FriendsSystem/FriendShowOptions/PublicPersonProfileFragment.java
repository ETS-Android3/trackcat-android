package de.trackcat.FriendsSystem.FriendShowOptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PublicPersonProfileFragment extends Fragment {

    RelativeLayout loadProfile;
    TextView name, dayOfBirth, gender, dayOfRegistration;
    CircleImageView image, state;
    ImageView user_gender_image;
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_public_person_profile, container, false);

        /* Get profil fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        image = view.findViewById(R.id.profile_image);
        state = view.findViewById(R.id.profile_state);
        name = view.findViewById(R.id.user_name);
        dayOfBirth = view.findViewById(R.id.user_dayOfBirth);
        gender = view.findViewById(R.id.user_gender);
        user_gender_image = view.findViewById(R.id.user_gender_image);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);

        /* Create userDAO*/
        userDAO = new UserDAO(MainActivity.getInstance());

        /* Get friendId and authorizationType from bundle */
        int friendId = getArguments().getInt("friendId");
        int authorizationType = getArguments().getInt("authorizationType");

        /* Create hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put("strangerId", "" + friendId);

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.showStrangerProfile(authString, map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(authorizationType);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* Set values */
                        name.setText(mainObject.getString("firstName") + " " + mainObject.getString("lastName"));
                        dayOfBirth.setText(GlobalFunctions.getDateFromSeconds(mainObject.getLong("dateOfBirth"), "dd.MM"));
                        dayOfRegistration.setText(GlobalFunctions.getDateFromSeconds(mainObject.getLong("dateOfRegistration"), "dd.MM.yyyy"));

                        /* Set gender */
                        if (mainObject.getInt("gender") != 2) {
                            InputStream imageStream;
                            if (mainObject.getInt("gender") == 0) {
                                gender.setText("weiblich");
                                gender.setTextColor(getResources().getColor(R.color.colorFemale));
                                imageStream = getContext().getResources().openRawResource(R.raw.female);
                            } else {
                                gender.setText("männlich");
                                imageStream = getContext().getResources().openRawResource(R.raw.male);
                                gender.setTextColor(getResources().getColor(R.color.colorMale));
                            }

                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            user_gender_image.setImageBitmap(bitmap);
                            user_gender_image.setVisibility(View.VISIBLE);
                        } else {
                            GlobalFunctions.setNoInformationStyle(gender);
                            user_gender_image.setVisibility(View.GONE);
                        }

                        /* Set profile image */
                        byte[] imgRessource = GlobalFunctions.getBytesFromBase64(mainObject.getString("image"));
                        Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
                        if (imgRessource != null && imgRessource.length > 0) {
                            bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
                        }
                        image.setImageBitmap(bitmap);

                        /* Set level */
                        double distance = Math.round(mainObject.getInt("totalDistance"));
                        double levelDistance;
                        if (distance >= 1000) {
                            levelDistance = distance / 1000L;
                        } else {
                            levelDistance = distance / 1000;
                        }
                        state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

                        /* Remove loadscreen */
                        loadProfile.setVisibility(View.GONE);
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
        return view;
    }
}
