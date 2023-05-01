package com.guihgo.inventorycontroll.database;

import android.provider.BaseColumns;

public final class TagContract {
    private TagContract(){}

    public static class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tag";
        public  static final String COLUMN_NAME_ID = "id";
        public  static final String COLUMN_NAME_NAME = "name";
        public  static final String COLUMN_NAME_DESCRIPTION = "description";
    }
}


