package com.example.goldenbite.Classes;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Product {
    public static final String CATEGORY_HOT_DRINKS = "Hot drinks";

    private String documentId;
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
    private String category;

    public Product(String name, int price, int size, String description, String imagUrl, String category) {
        this.name = name;
        this.price = price;
        this.size = size;
        this.description = description;
        this.imagUrl = imagUrl;
        this.category = category != null ? category : CATEGORY_HOT_DRINKS;
    }

    /* For loading from Firestore */
    public Product(String documentId, String name, int price, int size, String description, String imagUrl, String category) {
        this.documentId = documentId;
        this.name = name;
        this.price = price;
        this.size = size;
        this.description = description;
        this.imagUrl = imagUrl;
        this.category = category != null ? category : CATEGORY_HOT_DRINKS;
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

    public String getImagUrl() {
        return imagUrl;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void saveProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", this.name);
        product.put("price", this.price);
        product.put("size", this.size);
        product.put("descirption", this.description);
        product.put("imagUrl", this.imagUrl);
        product.put("category", this.category);

        FirebaseFirestore.getInstance().collection("Product").add(product);
    }

    public void updateProduct() {
        if (documentId == null || documentId.isEmpty()) return;
        Map<String, Object> product = new HashMap<>();
        product.put("name", this.name);
        product.put("price", this.price);
        product.put("size", this.size);
        product.put("descirption", this.description);
        product.put("imagUrl", this.imagUrl);
        product.put("category", this.category);

        FirebaseFirestore.getInstance().collection("Product").document(documentId).set(product);
    }




}
