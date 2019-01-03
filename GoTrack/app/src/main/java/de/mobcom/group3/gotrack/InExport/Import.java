package de.mobcom.group3.gotrack.InExport;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Import {
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
    private List<String> arrayListToString(String listStr)
    {
        List<String> resultList = new ArrayList<String>();
        String[] stringArr = listStr.split("<goTrack>");
        for (String lineStr: stringArr)
        {
            resultList.add(lineStr);
        }
        return resultList;
    }

}
