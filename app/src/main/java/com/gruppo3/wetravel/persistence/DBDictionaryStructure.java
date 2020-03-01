package com.gruppo3.wetravel.Persistence;

import android.provider.BaseColumns;

/**
 * Defines Database structure
 *
 * @author Riccardo Crociani
 */
public final class DBDictionaryStructure {

    private static final String SUBSCRIBERS = "subscribers";
    private static final String PHONE_NUMBER = "phone number";
    private static final String RESOURCES = "resources";
    private static final String IDENTIFIER = "identifier";
    private static final String VALUE = "value";

    // To prevent someone from accidentally instantiating the structure class,
    // make the constructor private.
    private DBDictionaryStructure() {
    }

    /**
     * Defines data from subscribers table
     */
    static class SubscriberEntity implements BaseColumns {
        static final String TABLE_NAME = SUBSCRIBERS;

        static final String COLUMN_PHONE_NUMBER = PHONE_NUMBER;
    }

    /**
     * Defines data from resources table
     */
    static class ResourceEntity implements BaseColumns {
        static final String TABLE_NAME = RESOURCES;

        static final String COLUMN_ID = IDENTIFIER;
        static final String COLUMN_VALUE = VALUE;
    }
}