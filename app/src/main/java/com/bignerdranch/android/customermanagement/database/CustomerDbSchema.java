package com.bignerdranch.android.customermanagement.database;

/**
 * Created by Chaz-Rae on 8/24/2016.
 * Customer database table and column names
 *
 * Coded from Android Programming: The Big Nerd Ranch Guide 2nd Edition
 */
public class CustomerDbSchema {
    public static final class CustomerTable{
        public static final String NAME ="customers";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String BILLING = "billing";
            public static final String SESSIONS = "sessions";
        }
    }
}
