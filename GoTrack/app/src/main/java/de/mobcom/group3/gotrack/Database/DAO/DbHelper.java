package de.mobcom.group3.gotrack.Database.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import static de.mobcom.group3.gotrack.Database.DAO.DbContract.*;

class DbHelper extends SQLiteOpenHelper {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getWritableDatabase().setForeignKeyConstraintsEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    SQLiteDatabase getInstance(boolean writable) {
        if(writable)
            return this.getWritableDatabase();
        return this.getReadableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //toDo: implement onUpgrade()
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //toDo: implement onDowngrade()
    }
}
