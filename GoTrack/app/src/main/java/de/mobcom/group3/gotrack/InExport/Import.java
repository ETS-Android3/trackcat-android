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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;

public class Import {

    private static Import importSingleton = null;

    private Import(){
    }

    public static Import getImport (){
        if(importSingleton == null){
            Log.i("Import", "Es wurde eine Import-Instanz erstellt.");
            importSingleton = new Import();
        }
        return importSingleton;
    }
    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    //  intent.setType("*/*");
    /*  intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(Intent.createChooser(intent,"Select file"), 1);
    setResult(Activity.RESULT_OK);
                    String fileName = "/time.ser";
                String fullFileName =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+fileName;
                FileInputStream fis = null;
                ObjectInputStream in = null;
                User2 p;
                try {
                    fis = new FileInputStream(fullFileName);
                    in = new ObjectInputStream(fis);
                    p = (User2) in.readObject();
                    in.close();
                    showText.setText(p.getUserName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
*/
    public void incomingImport (Context context, String incomingFile){
        Toast.makeText(context,"bis hierher1", Toast.LENGTH_LONG).show();
        Toast.makeText(context, incomingFile, Toast.LENGTH_LONG).show();
        String[] stringArr = incomingFile.split("<index>");

        String index =stringArr[0];
        Toast.makeText(context,index, Toast.LENGTH_LONG).show();
        String content =stringArr[1];
        switch(index){
            case("1"):
                Toast.makeText(context,"bis hierher2", Toast.LENGTH_LONG).show();
                importRoute(context, content);
                break;
            case("2"):
                importUserData(context, content);
                break;
            case("3"):
                importAllRoute(context, content);
                break;
            case("4"):
                importAllUser(context, content);
                break;
            case("5"):
                importAllUserData(context, content);
                break;
            default:
                Toast.makeText(context, "Die Import-Datei war fehlerhaft",
                        Toast.LENGTH_LONG).show();
        }
    }

    // Import einer einzelnen Route
    private void importRoute(Context context, String incomingFile){
        RouteDAO rDAO = new RouteDAO(context);
        Toast.makeText(context,"bis hierher3", Toast.LENGTH_LONG).show();
        rDAO.importRouteFromJSON(incomingFile);
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
        //UserDAO uDAO = new UserDAO(context);
        rDAO.importRoutesFromJson(stringToarrayList(incomingFile));
        Log.i("Import", "Der Import aller Routen eines Users wurde gestartet.");
    }

    // Import aller User mit Ihren Einstellungen
    public void importAllUser(Context context, String incomingFile){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        uDAO.importUsersFromJson( stringToarrayList(incomingFile));
        Log.i("Import", "Der Import aller User wurde gestartet.");
    }

    // Import eines Nutzers mit allen seinen Einstellungen und Routen
    private void importAllUserData(Context context, String incomingFile){
        UserDAO uDAO = new UserDAO(context);
        RouteDAO rDAO = new RouteDAO(context);
        String[] stringArr = incomingFile.split("<endUser>");
        String user = stringArr[0];
        String routes = stringArr[1];
        uDAO.importUserFromJson(user);
        rDAO.importRoutesFromJson(stringToarrayList(routes));
        Log.i("Import", "Der Import eines Users mit allen Routen wurde gestartet.");
    }

    private ArrayList<String> stringToarrayList(String listStr)
    {
        ArrayList<String> resultList = new ArrayList<String>();
        String[] stringArr = listStr.split("<goTrack>");
        for (String lineStr: stringArr)
        {
            resultList.add(lineStr);
        }
        return resultList;
    }
}
