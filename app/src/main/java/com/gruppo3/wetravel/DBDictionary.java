package com.gruppo3.wetravel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.eis.communication.network.NetDictionary;
import com.eis.communication.network.NetSubscriberList;
import com.eis.smslibrary.SMSPeer;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * DBDictionary add persistence to dictionaries used in {@link com.eis.smsnetwork.SMSJoinableNetManager}
 * @author Mattia Fanan
 */
//TODO these methods need to be executed with AsyncTask or IntentService.
public class DBDictionary implements NetDictionary<String, String>, NetSubscriberList<SMSPeer> {

    private DBDictionaryHelper helper;

    public DBDictionary(Context context) {
        this.helper = new DBDictionaryHelper(context);
    }

    /**
     * Used to instantiate in memory DB faster for testing
     *
     * @param context
     * @param version
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public DBDictionary(Context context, int version) {
        this.helper = new DBDictionaryHelper(context, null, version);
    }

    /**
     * Adds a resource to the network dictionary
     *
     * @param key      The key which defines the resource to be added
     * @param resource The resource to add
     */
    @Override
    public void addResource(@NonNull final String key, @NonNull final String resource) {

        SQLiteDatabase writableDB = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBDictionaryContract.ResourceEntity.COLUMN_ID, key);
        values.put(DBDictionaryContract.ResourceEntity.COLUMN_VALUE, resource);

        writableDB.insert(DBDictionaryContract.ResourceEntity.TABLE_NAME, null, values);
    }

    /**
     * Removes a resource from the dictionary
     *
     * @param key The key which defines the resource to be removed
     */
    @Override
    public void removeResource(@NonNull final String key) {
        SQLiteDatabase writableDB = helper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = DBDictionaryContract.ResourceEntity.COLUMN_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {key};
        // Issue SQL statement.
        writableDB.delete(DBDictionaryContract.ResourceEntity.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Returns a resource in the dictionary
     *
     * @param key The key which defines the resource to get
     * @return Returns a resource corresponding to the key if present in the dictionary,
     * else returns null
     */
    @Override
    public String getResource(String key) {
        SQLiteDatabase db = helper.getReadableDatabase();


        // Filter results
        String selection = DBDictionaryContract.ResourceEntity.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {key};

        Cursor cursor = db.query(
                DBDictionaryContract.ResourceEntity.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if (cursor.moveToNext())
            return cursor.getString(cursor.getColumnIndexOrThrow(DBDictionaryContract.ResourceEntity.COLUMN_VALUE));

        return null;
    }

    /**
     * Adds a subscriber to this network
     *
     * @param subscriber The subscriber to add to the net
     */
    @Override
    public void addSubscriber(SMSPeer subscriber) {
        SQLiteDatabase writableDB = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBDictionaryContract.SubscriberEntity.COLUMN_PHONE_NUMBER, subscriber.getAddress());

        writableDB.insert(DBDictionaryContract.SubscriberEntity.TABLE_NAME, null, values);
    }

    /**
     * @return Returns the set of all the current subscribers to the net
     */
    @Override
    public Set<SMSPeer> getSubscribers() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "select * from " + DBDictionaryContract.SubscriberEntity.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        HashSet<SMSPeer> subscribers = new HashSet<>();

        while (cursor.moveToNext()) {
            SMSPeer peer = new SMSPeer(
                    cursor.getString(cursor.getColumnIndexOrThrow(DBDictionaryContract.SubscriberEntity.COLUMN_PHONE_NUMBER))
            );
            subscribers.add(peer);
        }

        cursor.close();

        return subscribers;
    }

    /**
     * Removes a given subscriber from the subscribers
     *
     * @param subscriber The subscriber to remove
     */
    @Override
    public void removeSubscriber(SMSPeer subscriber) {

        SQLiteDatabase writableDB = helper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = DBDictionaryContract.SubscriberEntity.COLUMN_PHONE_NUMBER + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {subscriber.getAddress()};
        // Issue SQL statement.
        writableDB.delete(DBDictionaryContract.SubscriberEntity.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Gets all partakes closer than radius from a given position
     * @param position  the center of the area containing the partakes to return
     * @param radius    the radius of the area containing the partakes to return
     * @return all partakes closer than radius from a given position
     */
    public ArrayList<Partake> getClosestPartakes(LatLng position, Double radius) {

        ArrayList<Partake> toReturn = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "select * from " + DBDictionaryContract.ResourceEntity.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            int resIndex = cursor.getColumnIndexOrThrow(DBDictionaryContract.ResourceEntity.COLUMN_VALUE);
            int keyIndex = cursor.getColumnIndexOrThrow(DBDictionaryContract.ResourceEntity.COLUMN_ID);

            LatLng partakePosition = convertStringToLatLng(cursor.getString(resIndex));

            float[] result=new float[1];
            Location.distanceBetween(position.latitude,position.longitude,partakePosition.latitude,partakePosition.longitude,result);

            if (result[0] <= radius) {
                SMSPeer partakeOwner = new SMSPeer(cursor.getString(keyIndex));
                toReturn.add(new Partake(partakeOwner, partakePosition));
            }
        }

        return toReturn;
    }

    /**
     * Converts a LatLng to a String
     * @param latLng the position to convert
     * @return the string converted from the LatLng
     */
    public static String convertLatLngToString(LatLng latLng) {
        return latLng.latitude + "#" + latLng.longitude;
    }

    /**
     * Converts a String to a LatLng
     * @param string the String to convert
     * @return the LatLng converted from the String
     */
    public static LatLng convertStringToLatLng(String string) {
        String[] split = string.split("#");
        return new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

    /**
     * Closes the DB connection
     */
    public void close() {
        helper.close();
    }
}
