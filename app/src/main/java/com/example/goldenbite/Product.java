package com.example.goldenbite;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private String name;
    private int price;
    private int size;
    /*
    * there are three conditions for size:
    * if size =0(remove from menu)
    * if size =1(there is only 1 size)
    * if size =2(there are two sizes (small\large))
    */
    private String description;
    private String imagUrl;

    public Product(String name,int price,int size,String description,String imagUrl) {
        this.name = name;
        this.price = price;
        this.size = size;
        this.description = description;
        this.imagUrl = imagUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagUrl() {
        return imagUrl;
    }

    public void setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
    }


    public void saveProduct(){
        Map<String, Object> product = new HashMap<>();
        product.put("name",this.name);
        product.put("price", this.price);
        product.put("size", this.size);
        product.put("descirption", this.description);
        product.put("imagUrl", this.imagUrl);

        FirebaseFirestore.getInstance().collection("Product").add(product);
    }




}
