package edu.uga.cs.shoppingapp;

public class Item {

    private String key;
    private String name;
    private double cost;
    private String creator;
    private String buyer;

    public Item(String name, double cost, String creator, String buyer) {
        this.key = null;
        this.name = name;
        this.cost = cost;
        this.creator = creator;
        this.buyer = buyer;
    }

    public Item() {
        this.key = null;
        this.name = null;
        this.cost = 0.0;
        this.creator = null;
        this.buyer = null;
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

    public String getBuyer() {return buyer;}

    public void setBuyer(String buyer) {this.buyer = buyer;}

    public String getCreator() {return creator;}

    public void setCreator(String creator) {this.creator = creator;}
}
