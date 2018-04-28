package io.github.abhishekwl.flavradminprimary.Models;

import java.util.ArrayList;

public class Category {

    private String categoryName;
    private ArrayList<Item> itemArrayList;
    private int categoryCount;
    private String categoryImageUrl;

    public Category(String categoryName, ArrayList<Item> itemArrayList, int categoryCount, String categoryImageUrl) {
        this.categoryName = categoryName;
        this.itemArrayList = itemArrayList;
        this.categoryCount = categoryCount;
        this.categoryImageUrl = categoryImageUrl;
    }

    public Category(String categoryName, String categoryImageUrl) {
        this.categoryName = categoryName;
        this.itemArrayList = new ArrayList<>();
        this.categoryCount = this.itemArrayList.size();
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }

    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }
}
