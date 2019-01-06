package de.mobcom.group3.gotrack.InExport;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;

public class Import {

    private static Import importSingleton = null;

    private static Boolean isImportActiv = false;

    private Gson gson = new Gson();

    private Type imExportType = User.class;

    public Boolean getIsImportActiv () {return isImportActiv;}

    private Import(){
    }

    public static Import getImport (){
        if(importSingleton == null){
            Log.i("Import", "Es wurde eine Import-Instanz erstellt.");
            importSingleton = new Import();
        }
        return importSingleton;
    }

    public void incomingImport (Context context, String incomingFile){
        try {
            isImportActiv =true;
            String[] stringArr = incomingFile.split("<index>");
            String index = stringArr[0];
            String content = stringArr[1];
            RouteDAO rDAO = new RouteDAO(context);
            UserDAO uDAO = new UserDAO(context);
            switch (index) {
                case ("SingleRoute"):
                    // Import einer einzelnen Route
                    rDAO.importRouteFromJson(content, MainActivity.getActiveUser(), true);
                    Log.i("Import", "Der Import einer Route wurde gestartet.");
                    break;
                case ("UserSettings"):
                    // Import eines Users mit seinen Einstellungen
                    createUser(context, content, uDAO);
                    Log.i("Import", "Der Import eines Users wurde gestartet.");
                    break;
                case ("AllRoutes"):
                    // Import aller Routen eines Users
                    rDAO.importRoutesFromJson(stringToarrayList(content), MainActivity.getActiveUser(), true);
                    Log.i("Import", "Der Import aller Routen eines Users wurde gestartet.");
                    break;
                case ("AllUsersAllRoutes"):
                    // Import aller  Routen und aller User eines bestimmenten Users
                    String[] stringUsersRoutesArr = content.split("<nextUser>");
                    for(String userRoutes: stringUsersRoutesArr  )
                    {
                        String[] userRoutesArr= userRoutes.split("<route>");
                        String user = userRoutesArr[0];
                        String routes = userRoutesArr[1];
                        int userID = createUser(context, user, uDAO);
                        rDAO.importRoutesFromJson(stringToarrayList(routes), userID, false);

                    }
                    Log.i("Export", "Der Import aller Routen und aller Users wurde gestartet.");
                    break;
                case ("OneUserAllRoutes"):
                    // Import eines Nutzers mit allen seinen Einstellungen und Routen
                    String[] stringUserRoutesArr = content.split("<endUser>");
                    String user = stringUserRoutesArr[0];
                    String routes = stringUserRoutesArr[1];
                    int userID = createUser(context, user, uDAO);
                    rDAO.importRoutesFromJson(stringToarrayList(routes), userID, false);
                    Log.i("Import", "Der Import eines Users mit allen Routen wurde gestartet.");
                    break;
                default:
                    Toast.makeText(context, "Die Import-Datei war fehlerhaft",
                            Toast.LENGTH_LONG).show();
            }
            isImportActiv =false;
        }
        catch (Exception ex){
            Toast.makeText(context, "Die Import-Datei war fehlerhaft",
                    Toast.LENGTH_LONG).show();
            isImportActiv =false;
            ex.printStackTrace();
        }
    }

    public void handleSend(Context context, File file, InputStream inputStream) throws IOException {
        try {
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            } finally {
                output.close();
            }
        } finally {
            inputStream.close();
        }
        convertFile(context, file);
    }

    public void convertFile (Context context, File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String fileText = new String(buffer);
        Import.getImport().incomingImport(context, fileText);
    }

    private ArrayList<String> stringToarrayList(String listStr) {
        ArrayList<String> resultList = new ArrayList<String>();
        String[] stringArr = listStr.split("<goTrack>");
        for (String lineStr: stringArr)
        {
            resultList.add(lineStr);
        }
        return resultList;
    }

    private int getNewestUserID(Context context){
        UserDAO uDAO = new UserDAO(context);
        List<User> users = uDAO.readAll();
        int result=1;
        for(User u: users )
        {
            result=u.getId();
        }
        return result;
    }
    private int createUser (Context context,
                                       String user, UserDAO uDAO){
        User newUser = gson.fromJson(user, imExportType);
        List<User> users = uDAO.readAll();
        Boolean exist = false;
        int userID=0;
        for(User u: users )
        {
            if(newUser.getMail().equals(u.getMail())&&newUser.getFirstName().
                    equals(u.getFirstName())&&newUser.getLastName().
                    equals(u.getLastName())){
                exist= true;
                userID =u.getId();
                //Toast.makeText(context, "Nutzer existiert bereits", Toast.LENGTH_SHORT).show();
                break;
            }
            //Toast.makeText(context, "Nutzer wird angelgt", Toast.LENGTH_SHORT).show();
        }
        if(!exist) {
            uDAO.importUserFromJson(user);
            userID = getNewestUserID(context);
        }
        return userID;
    }
}
