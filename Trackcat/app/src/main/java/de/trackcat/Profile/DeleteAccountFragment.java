package de.trackcat.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

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

public class DeleteAccountFragment extends Fragment implements View.OnClickListener {

    Button btnSave;
    CheckBox accept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        /* get fields */
        btnSave = view.findViewById(R.id.btn_delete_account);
        accept = view.findViewById(R.id.checkBoxAccept);

        btnSave.setOnClickListener(this);
        accept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBoxAccept:

                /* change button */
                if (btnSave.isEnabled()) {

                    btnSave.setEnabled(false);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                }

                break;
            case R.id.btn_delete_account:

                /* create AlertBox */
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Account löschen?");
                alert.setMessage("Sind Sie sich sicher, dass Sie Ihren Account löschen wollen?");


                alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* get current user */
                        UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                        User currentUser = userDAO.read(MainActivity.getActiveUser());

                        /* read profile values from global db */
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", "" + currentUser.getIdUsers());

                        Retrofit retrofit = APIConnector.getRetrofit();
                        APIClient apiInterface = retrofit.create(APIClient.class);

                        /* start a call */
                        String base = currentUser.getMail() + ":" + currentUser.getPassword();
                        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                        Call<ResponseBody> call = apiInterface.deleteUser(authString, map);

                        call.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {

                                    if (response.code() == 401) {
                                        MainActivity.getInstance().showNotAuthorizedModal(3);
                                    } else {
                                        /* get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        if (mainObject.getString("success").equals("0")) {
                                            MainActivity.getInstance().logout();
                                            Toast.makeText(getContext(), "Account wird gelöscht.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                                Toast.makeText(getContext(), "Account kann nicht gelöscht werden. Bitte überprüfen Sie Ihre Internetverbindung.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

                alert.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();


                break;
        }
    }
}
