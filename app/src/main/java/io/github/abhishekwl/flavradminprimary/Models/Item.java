package io.github.abhishekwl.flavradminprimary.Models;

public class Item {

    private String itemName;
    private String itemCategory;
    private int itemCost;
    private boolean itemVeg;
    private String itemImageUrl;

    public Item(String itemName, String itemCategory, int itemCost, String itemImageUrl) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemCost = itemCost;
        this.itemImageUrl = itemImageUrl;
        this.itemVeg = true;
    }

    public Item(String itemName, String itemCategory, int itemCost, String itemImageUrl, boolean itemVeg) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemCost = itemCost;
        this.itemImageUrl = itemImageUrl;
        this.itemVeg = itemVeg;
    }

    public Item(String itemName, String itemCategory, int itemCost) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemCost = itemCost;
        this.itemImageUrl = "";
        this.itemVeg = true;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getItemCost() {
        return itemCost;
    }

    public void setItemCost(int itemCost) {
        this.itemCost = itemCost;
    }

    public boolean isItemVeg() {
        return itemVeg;
    }

    public void setItemVeg(boolean itemVeg) {
        this.itemVeg = itemVeg;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }
}
