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

public class Export {

    private static Export exportSingleton = null;

    private Export(){
    }

    public static Export getExport (){
        if(exportSingleton == null){
            Log.i("Export", "Es wurde eine Export-Instanz erstellt.");
            exportSingleton = new Export();
        }
        return exportSingleton;
    }

    // Export einer einzelnen Route
    public String exportRoute(Context context, int routeId){
        RouteDAO rDAO = new RouteDAO(context);
        String fileName = "/"+rDAO.read(routeId).getName();
        Log.i("Export", "Der Export der Route "+rDAO.read(routeId).getName()+
                " wurde gestartet.");
        return generateFile(context, fileName, rDAO.exportRouteToJson(routeId));
    }

    // Export eines Users mit seinen Einstellungen
    public String exportUserData(Context context, int userId){
        UserDAO uDAO = new UserDAO(context);
        String fileName = "/"+uDAO.read(userId).getFirstName()+uDAO.read(userId).getLastName();
        Log.i("Export", "Der Export des Users "+uDAO.read(userId).getFirstName()+" "+uDAO.
                read(userId).getLastName()+" wurde gestartet.");
        return generateFile(context, fileName, uDAO.exportUserToJson(userId));
    }

    // Export aller  Routen eines bestimmenten Users
    public String exportAllRoute(Context context, int userId){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        String fileName = "/allRouteFrom"+uDAO.read(userId).getFirstName()+uDAO.read(userId).
                getLastName();
        Log.i("Export", "Der Export aller Routen des Users "+uDAO.read(userId).
                getFirstName()+" "+uDAO.read(userId).getLastName()+" wurde gestartet.");
        return generateFile(context, fileName, arrayListToString(rDAO.exportRoutesToJson(userId)));
    }

    // Export aller User mit Ihren Einstellungen
    public String exportAllUser(Context context){
        RouteDAO rDAO = new RouteDAO(context);
        UserDAO uDAO = new UserDAO(context);
        String fileName = "/allUser";
        Log.i("Export", "Der Export aller User wurde gestartet.");
        return generateFile(context, fileName, arrayListToString(uDAO.exportUsersToJson()));
    }

    // Export eines Nutzers mit allen seinen Einstellungen und Routen
    public String exportAllUserData(Context context, int userId){
        UserDAO uDAO = new UserDAO(context);
        RouteDAO rDAO = new RouteDAO(context);
        String fileContent =uDAO.exportUserToJson(userId) +"<endUser>"+
                arrayListToString(rDAO.exportRoutesToJson(userId));
        String fileName = "/full"+uDAO.read(userId).getFirstName()+uDAO.read(userId).getLastName();
        Log.i("Export", "Der Export des Users "+uDAO.read(userId).getFirstName()+
                uDAO.read(userId).getLastName()+" mit allen Routen wurde gestartet.");
        return generateFile(context, fileName, fileContent);
    }

    private String generateFile(Context context, String fileNameNoEnd, String fileContent){
        String fileName = fileNameNoEnd+".txt";
        String mainFileDirectory =Environment.getExternalStoragePublicDirectory(Environment.
                DIRECTORY_DOWNLOADS).toString();
        File root = new File(mainFileDirectory, "GoTrack");
        if (!root.exists()) {
            Log.i("Export", "Der Ordner GoTrack wurde in Download erstellt.");
            root.mkdirs();
        }
        try{
            File file = new File(root, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(fileContent);
            writer.flush();
            writer.close();
            Log.i("Export", "Die Datei wurde erfolgreich erstellt.");
            Toast.makeText(context, "Speichern erfolgreich", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e("Export", "Die Datei konnte nicht erstellt werden.");
            Toast.makeText(context, "Fehler beim Speichern", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return mainFileDirectory+"/GoTrack/"+fileName;
    }

    public void send(Context context, String fileName)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String fullFileName =fileName;
        String PLACEHOLDER = fullFileName;
        File file = new File(PLACEHOLDER);
        Uri uri = Uri.fromFile(file);
        Log.i("Export", "Der Versand der Datei "+fileName+" wurde gestartet.");
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("text/plain");
        try{
            context.startActivity(Intent.createChooser(share, "Share via"));
            Log.i("Export", "Der Versand wurde beendet");
            Toast.makeText(context, "Die Datei wurde versendet", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("Export", "Der Versand der Datei "+fileName+" war nicht erfolgreich.");
            Toast.makeText(context, "Fehler beim Versenden", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    // Diese Funktion wandelt ArrayList in Strings um
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
