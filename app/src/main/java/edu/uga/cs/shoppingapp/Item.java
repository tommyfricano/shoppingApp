package edu.uga.cs.shoppingapp;

public class Item {

    private String key;
    private String name;
    private double cost;

    public Item(String name, double cost) {
        this.key = null;
        this.name = name;
        this.cost = cost;
    }

    public Item() {
        this.key = null;
        this.name = null;
        this.cost = 0.0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
