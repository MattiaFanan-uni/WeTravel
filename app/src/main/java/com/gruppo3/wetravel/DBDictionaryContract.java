package com.gruppo3.wetravel;

import android.provider.BaseColumns;

/**
 * Defines DB structure
 */
public final class DBDictionaryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBDictionaryContract() {
    }

    /**
     * Defines data from subscribers table
     */
    public static class SubscriberEntity implements BaseColumns {
        public static final String TABLE_NAME = "subscribers";

        public static final String COLUMN_PHONE_NUMBER = "phone_number";
    }

    /**
     * Defines data from resources table
     */
    public static class ResourceEntity implements BaseColumns {
        public static final String TABLE_NAME = "resources";

        public static final String COLUMN_ID = "identifier";//don't use key cause is reserved in sql
        public static final String COLUMN_VALUE = "value";
    }
}
