package de.mobcom.group3.gotrack.Database.DAO;

import android.provider.BaseColumns;

final class DbContract {
    private DbContract() {}

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "GoTrack.db";

    static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RouteEntry.TABLE_NAME + " ( " +
                    RouteEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RouteEntry.COL_USER + " INTEGER, " +
                    RouteEntry.COL_NAME + " TEXT, " +
                    RouteEntry.COL_TIME + " LONG, " +
                    RouteEntry.COL_DATE + " LONG, " +
                    RouteEntry.COL_TYPE + " INTEGER, " +
                    RouteEntry.COL_RIDETIME + " LONG, " +
                    RouteEntry.COL_DISTANCE + " DOUBLE, " +
                    RouteEntry.COL_LOCATIONS + " BLOB) ";

    static final String SQL_DELETE_ROUTE_TABLE =
            "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " ( " +
                    UserEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UserEntry.COL_FIRSTNAME + " TEXT, " +
                    UserEntry.COL_LASTNAME + " TEXT, " +
                    UserEntry.COL_ISACTIVE + " BOOLEAN, " +
                    UserEntry.COL_THEME + " BOOLEAN, " +
                    UserEntry.COL_HINT + " BOOLEAN, " +
                    UserEntry.COL_MAIL + " TEXT, " +
                    UserEntry.COL_IMAGE + " BLOB) ";

    static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    static final class RouteEntry implements BaseColumns {
        static final String TABLE_NAME = "route_table";
        static final String COL_ID = "id";
        static final String COL_USER = "fk_user_id";
        static final String COL_NAME = "name";
        static final String COL_TIME = "time";
        static final String COL_DATE = "date";
        static final String COL_TYPE = "type";
        static final String COL_RIDETIME = "rideTime";
        static final String COL_DISTANCE = "distance";
        static final String COL_LOCATIONS = "locations";
    }

    static final class UserEntry implements BaseColumns {
        static final String TABLE_NAME = "user_table";
        static final String COL_ID = "id";
        static final String COL_LASTNAME = "lastname";
        static final String COL_FIRSTNAME = "firstname";
        static final String COL_ISACTIVE = "active";
        static final String COL_HINT = "hint";
        static final String COL_THEME = "theme";
        static final String COL_MAIL = "mail";
        static final String COL_IMAGE = "image";
    }

}