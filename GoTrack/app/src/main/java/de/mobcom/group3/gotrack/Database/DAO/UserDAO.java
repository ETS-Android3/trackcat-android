package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.google.gson.Gson;

import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.Database.Models.User;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static de.mobcom.group3.gotrack.Database.DAO.DbContract.UserEntry.*;

// toDo: write javaDoc and comments

public class UserDAO {
    private final Context context;
    private Gson gson = new Gson();
    private Type imExportType = User.class;

    public UserDAO(Context context) {
        this.context = context;
    }

    public void create(User user) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            user.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null, valueGenerator(user)));
        } finally {
            dbHelper.close();
        }
    }

    public User read(int id) {
        DbHelper dbHelper = new DbHelper(context);
        User result = new User();
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            String[] projection = {
                    COL_ID,
                    COL_FIRSTNAME,
                    COL_LASTNAME,
                    COL_MAIL,
                    COL_ISACTIVE,
                    COL_THEME,
                    COL_HINT,
                    COL_IMAGE };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null )) {
                if (cursor.moveToFirst()) {
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    result.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)));
                    result.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)));
                    result.setActiveForDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISACTIVE)));
                    result.setHintsActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)));
                    result.setDarkThemeActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)));
                    result.setMail(cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)));
                    result.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)));
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    public List<User> readAll() {
        return this.readAll(new String[]{COL_ID, "DESC"});
    }

    /**
     * @param orderArgs String[] { column to sort, ASC / DESC } use COL_ID or COL_NAME as columns
     * @return List of all users in database
     */
    private List<User> readAll(String[] orderArgs) {
        DbHelper dbHelper = new DbHelper(context);
        ArrayList<User> result = new ArrayList<>();
        try {
            String[] projection = {
                    COL_ID,
                    COL_FIRSTNAME,
                    COL_LASTNAME,
                    COL_MAIL,
                    COL_ISACTIVE,
                    COL_IMAGE,
                    COL_HINT,
                    COL_THEME };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1] )) {
                if (cursor.moveToFirst())
                    do {
                        result.add(new User(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISACTIVE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)),
                                cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE))));
                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    public void update(int id, User user) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            user.setId(dbHelper.getWritableDatabase().update(TABLE_NAME, valueGenerator(user), selection, selectionArgs));
        } finally {
            dbHelper.close();
        }
    }

    private ContentValues valueGenerator(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, user.getFirstName());
        values.put(COL_LASTNAME, user.getLastName());
        values.put(COL_MAIL, user.getMail());
        values.put(COL_ISACTIVE, user.isActiveForDB());
        values.put(COL_HINT, user.isHintsActiveDB());
        values.put(COL_THEME, user.isDarkThemeActiveDB());
        values.put(COL_IMAGE, user.getImage());
        return values;
    }

    public void delete(User user) {
        DbHelper dbHelper = new DbHelper(context);
        RouteDAO routeDAO = new RouteDAO(context);
        try {
            for (Route route : routeDAO.readAll(user.getId())) {
                routeDAO.delete(route);
            }
            String selection = COL_ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(user.getId())};
            dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    public void importUserFromJson(String jsonString) {
        this.create(gson.fromJson(jsonString, imExportType));
    }

    public void importUsersFromJson(ArrayList<String> jsonStrings) {
        for (String jsonString : jsonStrings) {
            this.importUserFromJson(jsonString);
        }
    }

    public String exportUserToJson(int id) {
        return gson.toJson(this.read(id));
    }

    public ArrayList<String> exportUsersToJson() {
        ArrayList<String> result = new ArrayList<>();
        for (User user : this.readAll(new String[]{COL_ID, "DESC"})) {
            result.add(gson.toJson(user));
        }
        return result;
    }
}
