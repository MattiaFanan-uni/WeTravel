package com.gruppo3.wetravel;

import android.provider.BaseColumns;

public final class DBDictionaryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBDictionaryContract() {
    }

    /**
     * Defines data from subscribers table
     */
    public static class Subscibers implements BaseColumns {
        public static final String TABLE_NAME = "subscribers";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
    }

    /**
     * Defines data from resources table
     */
    public static class Resources implements BaseColumns {
        public static final String TABLE_NAME = "resources";
        //TODO change key
        public static final String COLUMN_KEY = "[key]";//because key is a reserved word in sql
        public static final String COLUMN_RESOURCE = "resource";
    }
}
