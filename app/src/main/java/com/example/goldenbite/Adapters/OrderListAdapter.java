package com.example.goldenbite.Adapters;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.VH> {

    public interface OnOrderSelectionListener {
        void onOrderSelectionChanged(@Nullable Order order, boolean selected);
    }

    private final List<Order> orders = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnOrderSelectionListener selectionListener;

    public void setOnOrderSelectionListener(OnOrderSelectionListener l) {
        selectionListener = l;
    }

    public void setOrders(List<Order> next) {
        orders.clear();
        if (next != null) {
            orders.addAll(next);
        }
        int old = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
        if (old != RecyclerView.NO_POSITION && selectionListener != null) {
            selectionListener.onOrderSelectionChanged(null, false);
        }
    }

    @Nullable
    public Order getSelectedOrder() {
        if (selectedPosition == RecyclerView.NO_POSITION || selectedPosition >= orders.size()) {
            return null;
        }
        return orders.get(selectedPosition);
    }

    public void clearSelection() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return;
        }
        int old = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyItemChanged(old);
        if (selectionListener != null) {
            selectionListener.onOrderSelectionChanged(null, false);
        }
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
        boolean sel = position == selectedPosition;
        h.itemView.setSelected(sel);
        if (sel) {
            h.itemView.setBackgroundColor(Color.argb(40, 33, 150, 243));
        } else {
            TypedValue tv = new TypedValue();
            h.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
            h.itemView.setBackgroundResource(tv.resourceId);
        }

        h.itemView.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                return;
            }
            int prev = selectedPosition;
            if (selectedPosition == pos) {
                selectedPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(pos);
                if (selectionListener != null) {
                    selectionListener.onOrderSelectionChanged(null, false);
                }
            } else {
                selectedPosition = pos;
                if (prev != RecyclerView.NO_POSITION) {
                    notifyItemChanged(prev);
                }
                notifyItemChanged(pos);
                if (selectionListener != null) {
                    selectionListener.onOrderSelectionChanged(orders.get(pos), true);
                }
            }
        });
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
