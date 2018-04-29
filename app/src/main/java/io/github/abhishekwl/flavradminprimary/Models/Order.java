package io.github.abhishekwl.flavradminprimary.Models;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class Order {

    private String orderClientName;
    private Date orderClientDate;
    private Location orderClientLocation;
    private ArrayList<Item> orderItemsArrayList;

    public Order(String orderClientName, Date orderClientDate, Location orderClientLocation, ArrayList<Item> orderItemsArrayList) {
        this.orderClientName = orderClientName;
        this.orderClientDate = orderClientDate;
        this.orderClientLocation = orderClientLocation;
        this.orderItemsArrayList = orderItemsArrayList==null ? new ArrayList<Item>() : orderItemsArrayList;
    }

    public String getOrderClientName() {
        return orderClientName;
    }

    public void setOrderClientName(String orderClientName) {
        this.orderClientName = orderClientName;
    }

    public Date getOrderClientDate() {
        return orderClientDate;
    }

    public void setOrderClientDate(Date orderClientDate) {
        this.orderClientDate = orderClientDate;
    }

    public Location getOrderClientLocation() {
        return orderClientLocation;
    }

    public void setOrderClientLocation(Location orderClientLocation) {
        this.orderClientLocation = orderClientLocation;
    }

    public ArrayList<Item> getOrderItemsArrayList() {
        return orderItemsArrayList;
    }

    public void setOrderItemsArrayList(ArrayList<Item> orderItemsArrayList) {
        this.orderItemsArrayList = orderItemsArrayList;
    }
}
