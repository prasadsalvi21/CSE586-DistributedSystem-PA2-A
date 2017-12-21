package edu.buffalo.cse.cse486586.groupmessenger1;

/**
 * Created by prasad-pc on 2/17/17.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupMessengerHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "PA2";
    private static final int DATABASE_VERSION = 1;

    GroupMessengerHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GroupMessengerDB.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GroupMessengerDB.onUpgrade(db, oldVersion, newVersion);
    }

}
