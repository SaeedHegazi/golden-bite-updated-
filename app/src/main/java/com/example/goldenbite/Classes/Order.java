package com.example.goldenbite.Classes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private String firestoreId;
    private String info;
    private String phoneNum;
    private String purchase;
    private double price;
    private boolean done;
    private String pan;
    private String cvv;



    public Order(String info, String phoneNum, String purchase, double price, boolean done, String pan, String cvv) {
        this.info = info;
        this.phoneNum = phoneNum;
        this.purchase = purchase;
        this.price = price;
        this.done = done;
        this.pan = pan;
        this.cvv = cvv;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public static Order fromSnapshot(DocumentSnapshot doc) {
        Map<String, Object> m = doc.getData();
        if (m == null) {
            return null;
        }
        String info = str(m.get("info"));
        String phoneNum = str(m.get("phoneNum"));
        String purchase = str(m.get("purchase"));
        double price = toDouble(m.get("price"));
        boolean done = Boolean.TRUE.equals(m.get("done"));
        String pan = str(m.get("pan"));
        String cvv = str(m.get("cvc"));
        if (cvv.isEmpty()) {
            cvv = str(m.get("cvv"));
        }
        Order o = new Order(info, phoneNum, purchase, price, done, pan, cvv);
        o.setFirestoreId(doc.getId());
        return o;
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static double toDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        return 0d;
    }
    public String getInfo() {
        return info;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String getPurchase() {
        return purchase;
    }
    public String getPhoneNum() {
        return phoneNum;
    }


    public void saveOrder(){
        Map<String, Object> order = new HashMap<>();
        order.put("info", this.info);
        order.put("phoneNum", this.phoneNum);
        order.put("purchase", this.purchase);
        order.put("price", this.price);
        order.put("done", this.done);
        order.put("pan", this.pan);
        order.put("cvc", this.cvv);


        FirebaseFirestore.getInstance().collection("Order").add(order);
    }
}
