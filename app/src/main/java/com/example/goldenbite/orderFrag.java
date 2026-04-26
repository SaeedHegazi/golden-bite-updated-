package com.example.goldenbite;

import static com.example.goldenbite.cartFrag.phoneNum;

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
        String phone = cartFrag.getCustomerPhoneNum();
        if (TextUtils.isEmpty(phone)) {
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

    private static class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.VH> {

        private final List<Order> orders = new ArrayList<>();

        void setOrders(List<Order> next) {
            orders.clear();
            if (next != null) {
                orders.addAll(next);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Order o = orders.get(position);
            h.info.setText(o.getInfo());
            h.price.setText(String.format(Locale.getDefault(), "%.2f", o.getPrice()));
            h.purchase.setText(o.getPurchase());
            h.phone.setText(o.getPhoneNum());
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            final TextView info;
            final TextView price;
            final TextView purchase;
            final TextView phone;

            VH(@NonNull View itemView) {
                super(itemView);
                info = itemView.findViewById(R.id.order_info);
                price = itemView.findViewById(R.id.order_price);
                purchase = itemView.findViewById(R.id.order_purchase);
                phone = itemView.findViewById(R.id.order_phone);
            }
        }
    }
}
