package com.gruppo3.wetravel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.VisibleForTesting;

public class DBDictionaryHelper extends SQLiteOpenHelper {

    public DBDictionaryHelper(Context context) {
        super(context, context.getString(R.string.DATABASE_NAME), null, context.getResources().getInteger(R.integer.DATABASE_VERSION));
    }

    /**
     * Used to instantiate in memory DB faster for testing
     * @param context
     * @param name
     * @param version
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public DBDictionaryHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RESOURCES);
        db.execSQL(SQL_CREATE_SUBSCRIBERS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO select appropriate update policy
        db.execSQL(SQL_DELETE_RESOURCES);
        db.execSQL(SQL_DELETE_SUBSCRIBERS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String SQL_CREATE_RESOURCES =
            "CREATE TABLE " + DBDictionaryContract.Resources.TABLE_NAME +
                    " ( " +
                    DBDictionaryContract.Resources.COLUMN_ID + " TEXT PRIMARY KEY , "+
                    DBDictionaryContract.Resources.COLUMN_VALUE + " TEXT "+
                    ");";
    //TODO should we add a FK for Key to Phone Number?

    private static final String SQL_DELETE_RESOURCES =
            "DROP TABLE IF EXISTS " + DBDictionaryContract.Resources.TABLE_NAME;

    private static final String SQL_CREATE_SUBSCRIBERS =
            "CREATE TABLE " + DBDictionaryContract.Subscibers.TABLE_NAME +
                    " ( " +
                    DBDictionaryContract.Subscibers.COLUMN_PHONE_NUMBER + " VARCHAR(20) PRIMARY KEY "+
                    ");";

    private static final String SQL_DELETE_SUBSCRIBERS =
            "DROP TABLE IF EXISTS " + DBDictionaryContract.Subscibers.TABLE_NAME;

}
