package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import de.mobcom.group3.gotrack.Database.Models.User;

import static de.mobcom.group3.gotrack.Database.DAO.DbContract.*;
import static de.mobcom.group3.gotrack.Database.DAO.DbContract.UserEntry.*;

/**
 * Helper class to provide database operations
 */
class DbHelper extends SQLiteOpenHelper {

    /**
     * default constructor to init database
     * @param context of type context
     */
    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Getter for writable and readable access to the database
     * @param writable of type boolean ( true for writable, false for readable access)
     * @return readable or writable SQLite database
     */
    SQLiteDatabase getInstance(boolean writable) {
        if(writable)
            return this.getWritableDatabase();
        return this.getReadableDatabase();
    }

    /**
     * Creates tables to store user and route information in the database
     * @param db of type SQLiteDatabase
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    /**
     * Method to upgrade to a newer version of the database
     * @param db of type SQLiteDatabase
     * @param oldVersion of type integer
     * @param newVersion of type integer
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion +
                ". Old data will be destroyed");
        db.execSQL(SQL_DELETE_ROUTE_TABLE);
        db.execSQL(SQL_DELETE_USER_TABLE);
        onCreate(db);
    }

    // toDo: remove before contribution
    // creates initial user for testing purposes
    public void createInitialUser(){
        User initialUser = new User("Max", "Mustermann", "max.mustermann@mail.de",
                null);
        initialUser.setActive(1);
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, initialUser.getFirstName());
        values.put(COL_LASTNAME, initialUser.getLastName());
        values.put(COL_MAIL, initialUser.getMail());
        values.put(COL_ISACTIVE, initialUser.isActiveForDB());
        values.put(COL_IMAGE, initialUser.getImage());
        initialUser.setId((int)this.getWritableDatabase().insert(TABLE_NAME, null, values));
    }
}
