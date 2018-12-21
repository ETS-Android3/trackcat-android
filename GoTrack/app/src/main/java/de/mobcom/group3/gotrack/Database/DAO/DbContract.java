package de.mobcom.group3.gotrack.Database.DAO;

import android.provider.BaseColumns;

final class DbContract {
    private DbContract() {}

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "GoTrack.db";

    static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RouteEntry.TABLE_NAME + " ( " +
                    RouteEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RouteEntry.COL_USER + " INTEGER, " + //toDo: implement foreign key constraint for user.id
                    RouteEntry.COL_NAME + " TEXT, " +
                    RouteEntry.COL_TIME + " LONG, " +
                    RouteEntry.COL_DATE + " LONG, " +
                    RouteEntry.COL_RIDETIME + " LONG, " +
                    RouteEntry.COL_DISTANCE + " DOUBLE, " +
                    RouteEntry.COL_LOCATIONS + " TEXT) ";

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
        public static final String COL_ID = "id";
        public static final String COL_USER = "fk_user_id";
        public static final String COL_NAME = "name";
        public static final String COL_TIME = "time";
        public static final String COL_DATE = "date";
        public static final String COL_RIDETIME = "rideTime";
        public static final String COL_DISTANCE = "distance";
        public static final String COL_LOCATIONS = "locations";
    }

    static final class UserEntry implements BaseColumns {
        static final String TABLE_NAME = "user_table";
        public static final String COL_ID = "id";
        public static final String COL_LASTNAME = "lastname";
        public static final String COL_FIRSTNAME = "firstname";
        public static final String COL_ISACTIVE = "active";
        public static final String COL_HINT = "hint";
        public static final String COL_THEME = "theme";
        public static final String COL_MAIL = "mail";
        public static final String COL_IMAGE = "image";
    }

}
