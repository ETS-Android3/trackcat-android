package de.mobcom.group3.gotrack.InExport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;

public class Import {

    private static Import importSingleton = null;

    private static Boolean isImportActiv = false;

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
            switch (index) {
                case ("1"):
                    importRoute(context, content);
                    break;
                case ("2"):
                    importUserData(context, content);
                    break;
                case ("3"):
                    importAllRoute(context, content);
                    break;
                case ("4"):
                    importAllRouteUsers(context, content);
                    break;
                case ("5"):
                    importAllUserData(context, content);
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

    // Import einer einzelnen Route
    private void importRoute(Context context, String incomingFile){
        RouteDAO rDAO = new RouteDAO(context);
        rDAO.importRouteFromJson(incomingFile, MainActivity.getActiveUser(), true);
        Log.i("Import", "Der Import einer Route wurde gestartet.");
    }

    // Import eines Users mit seinen Einstellungen
    private void importUserData(Context context, String incomingFile){
        UserDAO uDAO = new UserDAO(context);
        uDAO.importUserFromJson(incomingFile);
        Log.i("Import", "Der Import eines Users wurde gestartet.");
    }

    // Import aller Routen eines bestimmenten Users
    private void importAllRoute(Context context, String incomingFile){
        RouteDAO rDAO = new RouteDAO(context);
        rDAO.importRoutesFromJson(stringToarrayList(incomingFile), MainActivity.getActiveUser(), true);
        Log.i("Import", "Der Import aller Routen eines Users wurde gestartet.");
    }

    // Import aller  Routen eines bestimmenten Users
    public void importAllRouteUsers(Context context, String incomingFile){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        String[] stringArr = incomingFile.split("<nextUser>");
        Boolean isFistLoop= true;
        int importUser = 1;
        int activUser= MainActivity.getActiveUser();
        for(String u: stringArr )
        {
            String[] userRoutes= u.split("<route>");
            String user = userRoutes[0];
            String routes = userRoutes[1];
            uDAO.importUserFromJson(user);
            if (isFistLoop) {
                isFistLoop = false;
                importUser = getNewestUserID(context);
            }
            int tempActivUser = deleteDuplexUser(context, importUser);
            MainActivity.setActiveUser(tempActivUser );
            try {
                rDAO.importRoutesFromJson(stringToarrayList(routes), tempActivUser, false);
            }finally {
                MainActivity.setActiveUser(activUser);
            }
            importUser +=1;
        }
        MainActivity.setActiveUser(activUser);
        Log.i("Export", "Der Import aller Routen und aller Users wurde gestartet.");
    }

    // Import eines Nutzers mit allen seinen Einstellungen und Routen
    private void importAllUserData(Context context, String incomingFile){
        UserDAO uDAO = new UserDAO(context);
        RouteDAO rDAO = new RouteDAO(context);
        String[] stringArr = incomingFile.split("<endUser>");
        String user = stringArr[0];
        String routes = stringArr[1];
        uDAO.importUserFromJson(user);
        int activUser= MainActivity.getActiveUser();
        int importUser = getNewestUserID(context);
        int tempActivUser = deleteDuplexUser(context, importUser);
        MainActivity.setActiveUser(tempActivUser);
        try {
            rDAO.importRoutesFromJson(stringToarrayList(routes), tempActivUser, false);
        }finally {
            MainActivity.setActiveUser(activUser);
        }
        Log.i("Import", "Der Import eines Users mit allen Routen wurde gestartet.");
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

    private int deleteDuplexUser(Context context, int newUserID){
        UserDAO uDAO = new UserDAO(context);
        List<User> users = uDAO.readAll();
        User newUser = uDAO.read(newUserID);
        try {
            Toast.makeText(context, newUser.getId(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        Boolean notFound = true;
        Boolean shouldDelete = false;
        int result = newUserID;
        for(User u: users )
        {
            if(newUser.getMail().equals(u.getMail()) && notFound){
                Toast.makeText(context, newUser.getMail()+u.getMail(), Toast.LENGTH_SHORT).show();
                notFound = false;
            }
            else{
                Toast.makeText(context, "was here", Toast.LENGTH_SHORT).show();

                shouldDelete = true;
                result = u.getId();
                break;
            }
        }
        if(shouldDelete){

            uDAO.delete(newUser);
        }
        return result;
    }
}
