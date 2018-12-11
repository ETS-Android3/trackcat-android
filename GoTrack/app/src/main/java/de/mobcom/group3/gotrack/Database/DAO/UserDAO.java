package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.google.gson.Gson;
import de.mobcom.group3.gotrack.Database.Models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.mobcom.group3.gotrack.Database.DAO.DbContract.UserEntry.*;

// toDo: write javaDoc and comments

public class UserDAO {
    private SQLiteDatabase writableDb;
    private SQLiteDatabase readableDb;
    private Gson gson = new Gson();
    private Type imExportType = User.class;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public UserDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        writableDb = dbHelper.getInstance(true);
        readableDb = dbHelper.getInstance(false);
    }

    public void create(User user) {
        user.setId((int) writableDb.insert(TABLE_NAME, null, valueGenerator(user)));
    }

    public User read(int id) {
        User result = new User();
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] projection = {COL_ID, COL_FORENAME, COL_LASTNAME, COL_MAIL, COL_ISACTIVE, COL_IMAGE};
        Cursor cursor = readableDb.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            result.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FORENAME)));
            result.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)));
            result.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISACTIVE)));
            result.setMail(cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)));
            result.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)));
        }
        cursor.close();

        return result;
    }

    public List<User> readAll() {
        return this.readAll(new String[]{COL_ID, "DESC"});
    }

    /**
     * @param orderArgs String[] { column to sort, ASC / DESC } use COL_ID or COL_NAME as columns
     * @return List of all users in database
     */
    public List<User> readAll(String[] orderArgs) {
        ArrayList<User> result = new ArrayList<>();
        String[] projection = {COL_ID, COL_FORENAME, COL_LASTNAME, COL_MAIL, COL_ISACTIVE, COL_IMAGE};

        Cursor cursor = readableDb.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                orderArgs[0] + " " + orderArgs[1]
        );
        if (cursor.moveToFirst())
            do {
                result.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FORENAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE))));
            } while (cursor.moveToNext());
        cursor.close();

        return result;
    }

    public void update(int id, User user) {
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        valueGenerator(user);
        user.setId(writableDb.update(TABLE_NAME, valueGenerator(user), selection, selectionArgs));
    }

    private ContentValues valueGenerator(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_FORENAME, user.getFirstName());
        values.put(COL_LASTNAME, user.getLastName());
        values.put(COL_MAIL, user.getMail());
        values.put(COL_ISACTIVE, user.isActive());
        values.put(COL_IMAGE, user.getImage());
        return values;
    }

    public void delete(User user) {
        String selection = COL_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(user.getId())};
        writableDb.delete(TABLE_NAME, selection, selectionArgs);
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
