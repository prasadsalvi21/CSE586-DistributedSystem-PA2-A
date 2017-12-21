package edu.buffalo.cse.cse486586.groupmessenger1;

/**
 * Created by prasad-pc on 2/17/17.
 */
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupMessengerDB {

    public static final String key = "key";
    public static final String value = "value";

    private static final String LOG_TAG = "GroupMessengerDB";
    public static final String SQLITE_TABLE = "GroupMessenger";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    key + " ," +
                    value + ");";
    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }

}
