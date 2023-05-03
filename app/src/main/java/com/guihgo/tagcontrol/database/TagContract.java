package com.guihgo.tagcontrol.database;

import android.provider.BaseColumns;

public final class TagContract {
    private TagContract(){}

    public static class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tag";
        public  static final String COLUMN_NAME_ID = "tag_id";
        public  static final String COLUMN_NAME_NAME = "tag_name";
        public  static final String COLUMN_NAME_DESCRIPTION = "tag_description";
    }
}


