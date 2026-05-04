package com.example.goldenbite.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Adapters.AdminOrderList;
import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adminOrderFrag extends Fragment {

    private ListenerRegistration ordersListener;
    private MaterialCardView orderActionsBar;
    private Button btnDontAccept;
    private Button btnDone;
    private AdminOrderList ordersAdapter;

    public adminOrderFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderActionsBar = view.findViewById(R.id.admin_order_actions);
        btnDontAccept = view.findViewById(R.id.btn_dont_accept);
        btnDone = view.findViewById(R.id.btn_order_done);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        RecyclerView recycler = view.findViewById(R.id.admin_orders_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        ordersAdapter = new AdminOrderList(requireContext());
        recycler.setAdapter(ordersAdapter);

        setFloatingBarActive(false);
        ordersAdapter.setOnOrderSelectionListener((order, selected) ->
                setFloatingBarActive(selected && order != null));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        btnDontAccept.setOnClickListener(v -> applyOrderUpdate(db, true));
        btnDone.setOnClickListener(v -> applyOrderUpdate(db, false));

        ordersListener = db.collection("Order")
                .whereEqualTo("done", false)
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
                    ordersAdapter.setOrder(list);
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

    private void setFloatingBarActive(boolean active) {
        orderActionsBar.setAlpha(active ? 1f : 0.45f);
        btnDontAccept.setEnabled(active);
        btnDone.setEnabled(active);
    }

    private void applyOrderUpdate(FirebaseFirestore db, boolean reject) {
        Order selected = ordersAdapter.getSelectedOrder();
        if (selected == null || selected.getFirestoreId() == null) {
            return;
        }
        String id = selected.getFirestoreId();
        Map<String, Object> updates = new HashMap<>();
        if (reject) {
            updates.put("accept", false);
            updates.put("done", true);
        } else {
            updates.put("done", true);
        }
        db.collection("Order").document(id).update(updates)
                .addOnSuccessListener(unused -> ordersAdapter.clearSelection());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
    }
}
