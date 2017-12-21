package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
    static final String TAG = GroupMessengerProvider.class.getSimpleName();

    private GroupMessengerHelper dbHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String ins=values.toString();
        int index=ins.lastIndexOf("key=");
        String key_value=ins.substring(index+4,ins.length());
        Log.d("Key to be inserted",key_value);

        Cursor c=query(uri,null,key_value,null,null,null);
        if(c.moveToFirst())
        {
            Log.d(TAG,"Key "+key_value+" already present in DB, so update Value corresponding to key");
            update(uri,values,key_value,null);
        }
        else
        {
            long id = db.insert(GroupMessengerDB.SQLITE_TABLE, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            Log.v("Inserted values in DB", values.toString());
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        dbHelper=new GroupMessengerHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(GroupMessengerDB.SQLITE_TABLE);
        String selection2=GroupMessengerDB.key+" like '"+selection+"'";
        //Log.i("Selection",selection2);
        String msg=values.getAsString(GroupMessengerDB.value);
        values.put(GroupMessengerDB.value,msg);
        Log.v("DB updated with value", msg);
        db.update(GroupMessengerDB.SQLITE_TABLE, values, selection2, null);

        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(GroupMessengerDB.SQLITE_TABLE);
        String selection2=GroupMessengerDB.key+" like '"+selection+"'";
        //Log.i("Selection",selection2);
        Cursor cursor = queryBuilder.query(db, projection, selection2,
                selectionArgs, null, null, sortOrder);
        Log.v("Query Where clause", selection2);
        return cursor;
    }
}
