package com.bignerdranch.android.customermanagement.database;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class SessionDbSchema {
    public static final class SessionTable{
        public static final String NAME = "sessions";

        public static final class Cols{
            public static final String SESSION_UUID = "session_uuid";
            public static final String CUSTOMER_UUID = "customer_uuid";
            public static final String DATE = "date";
            public static final String DESCRIPTION = "description";
        }
    }
}
