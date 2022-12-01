package edu.uga.cs.shoppingapp;

import java.util.List;

public class User {

    private String email;
    private double spent;
    private List<Item> items;

    public User(String email, double spent, List<Item> items) {
        this.email = email;
        this.spent = spent;
        this.items = items;
    }

    public String getEmail() {
        return this.email;
    }

}
