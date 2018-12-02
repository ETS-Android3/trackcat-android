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

public class UserDAO implements IDAO<User> {
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

    @Override
    public void create(User user) {
        user.setId((int) writableDb.insert(TABLE_NAME, null, valueGenerator(user)));
    }

    @Override
    public User read(int id) {
        User result = new User();
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] projection = {COL_ID, COL_NAME, COL_MAIL, COL_THEME, COL_IMAGE};
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
            result.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            result.setMail(cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)));
            result.setTheme(cursor.getString(cursor.getColumnIndexOrThrow(COL_THEME)));
            result.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)));
        }
        cursor.close();

        return result;
    }

    @Override
    public List<User> readAll(int id) {
        return this.readAll();
    }

    public List<User> readAll() {
        ArrayList<User> result = new ArrayList<>();
        String selection = " * ";
        String[] projection = {COL_ID, COL_NAME, COL_MAIL, COL_THEME, COL_IMAGE};

        Cursor cursor = readableDb.query(
                TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst())
            do {
                result.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_THEME)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE))));
            } while (cursor.moveToNext());
        cursor.close();

        return result;
    }

    @Override
    public void update(int id, User user) {
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        valueGenerator(user);
        user.setId(writableDb.update(TABLE_NAME, valueGenerator(user), selection, selectionArgs));
    }

    private ContentValues valueGenerator(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_MAIL, user.getMail());
        values.put(COL_THEME, user.getTheme());
        values.put(COL_IMAGE, user.getImage());
        return values;
    }

    @Override
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
        for (User user : this.readAll()) {
            result.add(gson.toJson(user));
        }
        return result;
    }
}
