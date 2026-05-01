package com.example.goldenbite.Activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.Adapters.OrderListAdapter;
import com.example.goldenbite.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class Orders extends BaseActivity {

    private ListenerRegistration ordersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);


        Button goBack = findViewById(R.id.btn_orders_go_back);
        goBack.setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.orders_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        OrderListAdapter adapter = new OrderListAdapter();
        recycler.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ordersListener = db.collection("Order")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) {
                        return;
                    }
                    List<Order> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Order o = Order.fromSnapshot(doc);
                        if (o != null) {
                            list.add(o);
                        }
                    }
                    adapter.setOrders(list);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
    }
}
