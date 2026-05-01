package com.example.goldenbite.Fragments;

import static com.example.goldenbite.Fragments.cartFrag.phoneNum;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Adapters.CustomerOrdersAdapter;
import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class orderFrag extends Fragment {

    private ListenerRegistration ordersListener;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private CustomerOrdersAdapter adapter;

    public orderFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.order_frag_recycler);
        emptyView = view.findViewById(R.id.order_frag_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CustomerOrdersAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        attachOrdersListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachOrdersListener();
    }

    private void attachOrdersListener() {
        detachOrdersListener();
        if (!isAdded() || adapter == null) {
            return;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            adapter.setOrders(new ArrayList<>());
            updateEmptyState(true);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ordersListener = db.collection("Order")
                .whereEqualTo("done", false)
                .whereEqualTo("phoneNum", phoneNum)
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
                    updateEmptyState(list.isEmpty());
                });
    }

    private void detachOrdersListener() {
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
    }

    private void updateEmptyState(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        detachOrdersListener();
        super.onDestroyView();
    }

}
