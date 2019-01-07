package de.mobcom.group3.gotrack.InExport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;

public class Export {

    private static Export exportSingleton = null;

    /* Konstruktor */
    private Export(){
    }

    /* Mit dieser Methode wird eine Instanz der Klasse zurügegeben */
    public static Export getExport (){
        if(exportSingleton == null){
            Log.i("GoTrack-Export", "Es wurde eine Export-Instanz erstellt.");
            exportSingleton = new Export();
        }
        return exportSingleton;
    }

    /* Export einer einzelnen Route */
    public String exportRoute(Context context, int routeId, Boolean forSend){
        RouteDAO rDAO = new RouteDAO(context);
        String fileName = "/"+rDAO.read(routeId).getName();
        Log.i("GoTrack-Export", "Der Export der Route "+rDAO.read(routeId).getName()+
                " wurde gestartet.");
        return generateFile(context, fileName,"SingleRoute<index>"+
                        rDAO.exportRouteToJson(routeId), forSend);
    }

    /* Export eines Users mit seinen Einstellungen ohne Routen */
    public String exportUserData(Context context, int userId,Boolean forSend){
        UserDAO uDAO = new UserDAO(context);
        String fileName = "/"+uDAO.read(userId).getFirstName()+uDAO.read(userId).getLastName();
        Log.i("GoTrack-Export", "Der Export des Users "+uDAO.read(userId).
                getFirstName()+" "+uDAO.read(userId).getLastName()+" wurde gestartet.");
        return generateFile(context, fileName,"UserSettings<index>"+
                uDAO.exportUserToJson(userId), forSend);
    }

    /* Export aller Routen eines bestimmten Users */
    public String exportAllRoute(Context context, int userId, Boolean forSend){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        String fileName = "/allRouteFrom"+uDAO.read(userId).getFirstName()+uDAO.read(userId).
                getLastName();
        Log.i("GoTrack-Export", "Der Export aller Routen des Users "+uDAO.read(userId).
                getFirstName()+" "+uDAO.read(userId).getLastName()+" wurde gestartet.");
        return generateFile(context, fileName,"AllRoutes<index>"+
                arrayListToString(rDAO.exportRoutesToJson(userId)), forSend);
    }

    /* Export aller Routen und aller Users mit dem User */
    public String exportAllRouteUsers(Context context, Boolean forSend){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        List<User> users = uDAO.readAll();
        String fileName = "/fullApp";
        Log.i("GoTrack-Export",
                "Der Export aller Routen und aller Users wurde gestartet.");
        String fileContent ="AllUsersAllRoutes<index>";
        for(User u: users )
        {
            fileContent =fileContent+uDAO.exportUserToJson(u.getId())+"<route>"+
                    arrayListToString(rDAO.exportRoutesToJson(u.getId()))+"<nextUser>";
        }
        return generateFile(context, fileName,fileContent, forSend);
    }

    /* Export eines Nutzers mit allen seinen Einstellungen und Routen */
    public String exportAllUserData(Context context, int userId, Boolean forSend){
        UserDAO uDAO = new UserDAO(context);
        RouteDAO rDAO = new RouteDAO(context);
        String fileContent =uDAO.exportUserToJson(userId) +"<endUser>"+
                arrayListToString(rDAO.exportRoutesToJson(userId));
        String fileName = "/full"+uDAO.read(userId).getFirstName()+uDAO.read(userId).getLastName();
        Log.i("GoTrack-Export", "Der Export des Users "+uDAO.read(userId).getFirstName()+
                uDAO.read(userId).getLastName()+" mit allen Routen wurde gestartet.");
        return generateFile(context, fileName, "OneUserAllRoutes<index>"+fileContent,
                forSend);
    }

    /* Diese Methode generiert eine Datei aus einem String und speichert
     sie im Ordner GoTrack im Download-Ordner ab */
    private String generateFile(Context context, String fileNameNoEnd, String fileContent,
                                Boolean forSend){
        String fileName = fileNameNoEnd+".gotrack";
        String mainFileDirectory =Environment.getExternalStoragePublicDirectory(Environment.
                DIRECTORY_DOWNLOADS).toString();
        File root = new File(mainFileDirectory, "GoTrack");
        if (!root.exists()) {
            Log.i("GoTrack-Export", "Der Ordner GoTrack wurde in Download erstellt.");
            root.mkdirs();
        }
        try{
            File file = new File(root, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(fileContent);
            writer.flush();
            writer.close();
            Log.i("GoTrack-Export", "Die Datei wurde erfolgreich erstellt.");
            Toast.makeText(context, "Speichern erfolgreich", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e("GoTrack-Export", "Die Datei konnte nicht erstellt werden.");
            Toast.makeText(context, "Fehler beim Speichern", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        String pathFromFile =mainFileDirectory+"/GoTrack/"+fileName;
        if(forSend){
            send(context, pathFromFile);
        }
        return pathFromFile;
    }

    /* Diese Methode kann eine Datei über ein Medium versenden */
    private void send(Context context, String fileName)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String fullFileName =fileName;
        String PLACEHOLDER = fullFileName;
        File file = new File(PLACEHOLDER);
        Uri uri = Uri.fromFile(file);
        Log.i("GoTrack-Export", "Der Versand der Datei "+fileName+" wurde gestartet.");
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("application/gotrack");
        try{
            context.startActivity(Intent.createChooser(share, "Share via"));
            Log.i("Export", "Der Versand wurde beendet");
        } catch (Exception ex) {
            Log.e("Export", "Der Versand der Datei "+fileName+" war nicht erfolgreich.");
            Toast.makeText(context, "Fehler beim Versenden", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    /* Diese Funktion wandelt eine ArrayList in einen String um */
    private String arrayListToString(List<String> list)
    {
        String resultStr = "";
        for (String str: list)
        {
            resultStr += str;
            resultStr += "<goTrack>";
        }
        return resultStr;
    }
}
