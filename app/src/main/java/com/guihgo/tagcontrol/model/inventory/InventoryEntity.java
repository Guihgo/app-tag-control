package com.guihgo.tagcontrol.model.inventory;

public class InventoryEntity {

    public int id;

    public String tagId;
    public String tagName;
    public long expirationDate;
    public int quantity;

    public InventoryEntity(int id, String tagName, long expirationDate, int quantity) {
        this.id = id;
        this.tagName = tagName;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

}
