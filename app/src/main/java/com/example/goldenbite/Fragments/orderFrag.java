package com.example.goldenbite.Fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.goldenbite.Adapters.CustomerOrdersAdapter;
import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.Classes.PhoneNum;
import com.example.goldenbite.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

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
        adapter = new CustomerOrdersAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        attachOrdersListener();
    }
    @Override
    public void onPause() {
        super.onPause();
        detachOrdersListener();
    }
    public void attachOrdersListener() {
        detachOrdersListener();

        String currentPhone = PhoneNum.phoneNumber;

        if (TextUtils.isEmpty(currentPhone)) {
            updateEmptyState(true);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ordersListener = db.collection("Order")
                .whereEqualTo("done", false)
                .whereEqualTo("phoneNum", currentPhone.trim())
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snap != null) {
                        List<Order> list = new ArrayList<>();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            Order o = Order.fromSnapshot(doc);
                            if (o != null) list.add(o);
                        }
                        adapter.setOrders(list);
                        updateEmptyState(list.isEmpty());
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "notifications allowed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "didn't allow notifications", Toast.LENGTH_SHORT).show();
            }
        }
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
