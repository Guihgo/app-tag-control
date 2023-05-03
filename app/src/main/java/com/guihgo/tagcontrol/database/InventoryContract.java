package com.guihgo.tagcontrol.database;

import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract(){}

    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";

        public  static final String COLUMN_NAME_ID = "inventory_id";
        public  static final String COLUMN_NAME_TAG_ID = "inventory_tag_id";
        public  static final String COLUMN_NAME_EXPIRATION = "inventory_expiration";
        public  static final String COLUMN_NAME_QUANTITY = "inventory_quantity";

    }
}
