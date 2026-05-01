package com.example.goldenbite.Classes;

public class Cart {
    private String Pname;
    private int count;
    private String size;
    private int price;

    public Cart(String pname, int count, String size, int price){
        this.Pname = pname;
        this.count = count;
        this.size = size;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public int getCount() {
        return count;
    }
    public String getPname() {
        return Pname;
    }

}
