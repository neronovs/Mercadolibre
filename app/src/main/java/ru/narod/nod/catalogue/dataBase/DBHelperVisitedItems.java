package ru.narod.nod.catalogue.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 17.05.2017.
 */

public class DBHelperVisitedItems extends SQLiteOpenHelper {

    private static final String TAG = "myLogs." + DBHelperVisitedItems.class.getSimpleName();
    private static final String DATABASE_NAME = "favorites.db"; //DB name
    private static final int DATABASE_VERSION = 1; //DB version

    public DBHelperVisitedItems(Context context) {
        // конструктор суперкласса
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //A creation table string
        String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE IF NOT EXISTS " + DataKeepContract.TableFields.TABLE_NAME + " ("
                        + DataKeepContract.TableFields._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"//INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + DataKeepContract.TableFields.COLUMN_NUMBER + " TEXT "// NOT NULL, "
                        + ");";

        //Starting a creating of a table
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        Log.d("onCreate", " is finished");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "An updating from the version " + oldVersion + " to the version " + newVersion);

        //Killing the old table
        db.execSQL("DROP TABLE IF EXISTS " + DataKeepContract.TableFields.TABLE_NAME);
//        SQLiteDatabase.deleteDatabase(new File("data/data/ru.narod.nod.otc/database/favorites.db")); //killing the DB
        //and creating a new one
        onCreate(db);
    }

    //Erasing db
    public void clearDB(SQLiteDatabase db) {
        Log.d(TAG, "--- Clear mytable: ---");
        //Deleting all data from a table
        int clearCount = db.delete(DataKeepContract.TableFields.TABLE_NAME, "1", null);
        Log.d(TAG, "deleted rows count = " + clearCount);
    }

    //Outputs in Log the DB consistence
    public void readDB(SQLiteDatabase db) {
        Log.d(TAG, "--- Rows in mytable: ---");
        //Making a request for all data from the mytable and getting Cursor
        Cursor c = db.query(DataKeepContract.TableFields.TABLE_NAME, null, null, null, null, null, null);

        //Setting a cursor position to the first string
        //If there no a string then it's returning "false"
        if (c.moveToFirst()) {
            do {
                String SQL_CREATE_FAVORITES_TABLE =
                        DataKeepContract.TableFields._ID + c.getString(c.getColumnIndex("_id"))
                                + DataKeepContract.TableFields.COLUMN_NUMBER + " " + c.getString(c.getColumnIndex("number"))
                                + ");";

                Log.d(TAG, SQL_CREATE_FAVORITES_TABLE);
                //Going to the next string or the cycle stops
            } while (c.moveToNext());
        }
    }

    //Getting data from a table in DB
    public ArrayList<String> getDB(SQLiteDatabase db) {
        Log.d(TAG, "--- Rows in mytable: ---");
        //Making a request for all data from the mytable and getting Cursor
        Cursor c = db.query(DataKeepContract.TableFields.TABLE_NAME, null, null, null, null, null, null);

        ArrayList<String> arrayList = new ArrayList<>();

        //Setting a cursor position to the first string
        //If there no a string then it's returning "false"
        if (c.moveToFirst()) {
            do {
                String SQL_CREATE_FAVORITES_TABLE =
                        DataKeepContract.TableFields._ID + c.getString(c.getColumnIndex("_id"))
                                + DataKeepContract.TableFields.COLUMN_NUMBER + " " + c.getString(c.getColumnIndex("number"))
                                + ");";

                Log.d(TAG, SQL_CREATE_FAVORITES_TABLE);
                arrayList.add(c.getString(c.getColumnIndex("number")));

                //Going to the next string or the cycle stops
            } while (c.moveToNext());
        }

        return arrayList;
    }
}
