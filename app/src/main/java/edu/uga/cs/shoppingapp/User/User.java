package edu.uga.cs.shoppingapp.User;

import java.util.ArrayList;

import edu.uga.cs.shoppingapp.Item.Item;

public class User {

    private String email;
    private double spent;
    private ArrayList<Item> items;
    private String key;

    public User(String email, double spent, ArrayList<Item> items) {
        this.email = email;
        this.spent = spent;
        this.items = new ArrayList<>(items);
        this.key = null;
    }

    public User() {
        this.email = null;
        this.spent = 0;
        this.items = null;
        this.key = null;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
