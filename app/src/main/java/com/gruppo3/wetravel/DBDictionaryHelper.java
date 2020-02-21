package com.gruppo3.wetravel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.VisibleForTesting;

/**
 * Helper class for operations on the DB
 */
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

    /**
     * Creates a DB over a SQLiteDatabase
     * @param db the connection to SQLiteDatabase
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RESOURCES);
        db.execSQL(SQL_CREATE_SUBSCRIBERS);
    }
    /**
     * Updates a DB over a SQLiteDatabase
     * @param db the connection to SQLiteDatabase
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO select appropriate update policy
        db.execSQL(SQL_DELETE_RESOURCES);
        db.execSQL(SQL_DELETE_SUBSCRIBERS);
        onCreate(db);
    }
    /**
     * Downgrades a DB over a SQLiteDatabase
     * @param db the connection to SQLiteDatabase
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String SQL_CREATE_RESOURCES =
            "CREATE TABLE " + DBDictionaryContract.ResourceEntity.TABLE_NAME +
                    " ( " +
                    DBDictionaryContract.ResourceEntity.COLUMN_ID + " TEXT PRIMARY KEY , "+
                    DBDictionaryContract.ResourceEntity.COLUMN_VALUE + " TEXT "+
                    ");";
    //TODO should we add a FK for Key to Phone Number?

    private static final String SQL_DELETE_RESOURCES =
            "DROP TABLE IF EXISTS " + DBDictionaryContract.ResourceEntity.TABLE_NAME;

    private static final String SQL_CREATE_SUBSCRIBERS =
            "CREATE TABLE " + DBDictionaryContract.SubscriberEntity.TABLE_NAME +
                    " ( " +
                    DBDictionaryContract.SubscriberEntity.COLUMN_PHONE_NUMBER + " VARCHAR(20) PRIMARY KEY "+
                    ");";

    private static final String SQL_DELETE_SUBSCRIBERS =
            "DROP TABLE IF EXISTS " + DBDictionaryContract.SubscriberEntity.TABLE_NAME;

}
