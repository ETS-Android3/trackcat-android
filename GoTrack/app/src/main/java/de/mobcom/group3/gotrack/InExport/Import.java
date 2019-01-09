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

/* Mit dieser Klasse können exportierte Nutzer und Routen, aus einer Datei, eingelesen werden. */
public class Import {

    private static Import importSingleton = null;

    private static Boolean isImportActiv = false;

    private Gson gson = new Gson();

    private Type imExportType = User.class;

    public Boolean getIsImportActiv () {return isImportActiv;}

    /* Konstruktor */
    private Import(){
    }

    /* Mit dieser Methode wird eine Instanz der Klasse zurügegeben */
    public static Import getImport (){
        if(importSingleton == null){
            Log.i("GoTrack-Import", "Es wurde eine Import-Instanz erstellt.");
            importSingleton = new Import();
        }
        return importSingleton;
    }

    /* Diese Methode verwertet eingekommende Import-Dateien */
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
                    /* Import einer einzelnen Route */
                    rDAO.importRouteFromJson(content, MainActivity.getActiveUser(), true);
                    Log.i("GoTrack-Import", "Der Import einer Route wurde gestartet.");
                    Toast.makeText(context, "Die Datei wird importiert",
                            Toast.LENGTH_LONG).show();
                    break;
                case ("UserSettings"):
                    /* Import eines Users mit seinen Einstellungen */
                    createUser(context, content, uDAO);
                    Log.i("GoTrack-Import", "Der Import eines Users wurde gestartet.");
                    Toast.makeText(context, "Die Datei wird importiert",
                            Toast.LENGTH_LONG).show();
                    break;
                case ("AllRoutes"):
                    /* Import aller Routen eines Users, ohne den User */
                    rDAO.importRoutesFromJson(stringToarrayList(content),
                            MainActivity.getActiveUser(), true);
                    Log.i("GoTrack-Import",
                            "Der Import aller Routen eines Users wurde gestartet.");
                    Toast.makeText(context, "Die Datei wird importiert",
                            Toast.LENGTH_LONG).show();
                    break;
                case ("AllUsersAllRoutes"):
                    /* Import aller  Routen und aller User */
                    String[] stringUsersRoutesArr = content.split("<nextUser>");
                    for(String userRoutes: stringUsersRoutesArr  )
                    {
                        String[] userRoutesArr= userRoutes.split("<route>");
                        String user = userRoutesArr[0];
                        int userID = createUser(context, user, uDAO);
                        try {
                            String routes = userRoutesArr[1];
                            rDAO.importRoutesFromJson(stringToarrayList(routes),
                                    userID, false);
                        }catch (ArrayIndexOutOfBoundsException ex){
                            Log.e("GoTrack-Import", ex.toString());
                        }
                    }
                    Log.i("GoTrack-Import",
                            "Der Import aller Routen und aller Users wurde gestartet.");
                    Toast.makeText(context, "Die Datei wird importiert",
                            Toast.LENGTH_LONG).show();
                    break;
                case ("OneUserAllRoutes"):
                    /* Import eines Nutzers mit allen seinen Einstellungen und Routen */
                    String[] stringUserRoutesArr = content.split("<endUser>");
                    String user = stringUserRoutesArr[0];
                    int userID = createUser(context, user, uDAO);
                    try {
                        String routes = stringUserRoutesArr[1];
                        rDAO.importRoutesFromJson(stringToarrayList(routes),
                                userID, false);
                    }catch (ArrayIndexOutOfBoundsException ex){
                        Log.e("GoTrack-Import", ex.toString());
                    }
                    Log.i("GoTrack-Import",
                            "Der Import eines Users mit allen Routen wurde gestartet.");
                    Toast.makeText(context, "Die Datei wird importiert",
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    Log.e("GoTrack-Import",
                            "Die Import-Datei war fehlerhaft.");
                    Toast.makeText(context, "Die Import-Datei war fehlerhaft",
                            Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(context, "Die Import-Datei war fehlerhaft",
                    Toast.LENGTH_LONG).show();
            Log.e("GoTrack-Import", ex.toString());
        }
        finally {
            isImportActiv =false;
        }
    }

    /* Diese Methode holt aus einer Datei den Inhalt als String */
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
        FileInputStream is = new FileInputStream(file);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String fileText = new String(buffer);
        Import.getImport().incomingImport(context, fileText);
    }

    /* Diese Methode konvertiert einen String mit Elementen in ein Liste damit
    diese in die Datenbank geschrieben werden kann*/
    private ArrayList<String> stringToarrayList(String listStr) {
        ArrayList<String> resultList = new ArrayList<String>();
        String[] stringArr = listStr.split("<goTrack>");
        for (String lineStr: stringArr)
        {
            resultList.add(lineStr);
        }
        return resultList;
    }

    /* Diese Methode gibt zuletzt erstellten User zurück damit Ihm seine Routen zugewiesen
     werden können */
    private int getNewestUserID(Context context){
        UserDAO uDAO = new UserDAO(context);
        List<User> users = uDAO.readAll();
        int result=-1;
        for(User u: users )
        {
            if(u.getId()>result){
                result=u.getId();
            }
        }
        return result;
    }

    /* Diese Methode legt einen importierten User in der Datenbank, sofern er nicht existiert */
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
                Log.i("GoTrack-Import",
                        "Der User "+ u.getMail()+" existiert bereits.");
                break;
            }

        }
        if(!exist) {
            Log.i("GoTrack-Import",
                    "Der User "+ newUser.getMail()+" wurde angelegt.");
            uDAO.importUserFromJson(user);
            userID = getNewestUserID(context);
        }
        return userID;
    }
}
