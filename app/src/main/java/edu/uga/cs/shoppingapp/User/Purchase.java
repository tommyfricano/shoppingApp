package edu.uga.cs.shoppingapp.User;

public class Purchase {
    private int spent;
    private String userEmail;


    public Purchase(int spent, String userEmail) {
        this.spent = spent;
        this.userEmail = userEmail;
    }

    public int getSpent() {
        return this.spent;
    }
    public String getUserEmail() {
        return this.userEmail;
    }
}
