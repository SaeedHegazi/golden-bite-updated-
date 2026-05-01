package com.example.goldenbite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.VH> {

    private final List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> next) {
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

