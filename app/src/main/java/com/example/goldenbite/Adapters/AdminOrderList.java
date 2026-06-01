package com.example.goldenbite.Adapters;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderList extends RecyclerView.Adapter<AdminOrderList.VH> {

    public interface OnOrderSelectionListener {
        void onOrderSelectionChanged(@Nullable Order order, boolean selected);
    }

    private final List<Order> orders = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnOrderSelectionListener selectionListener;
    private Context context;
    public AdminOrderList(Context context) {
        this.context = context;
    }

    public void setOnOrderSelectionListener(OnOrderSelectionListener l) {
        selectionListener = l;
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

    public void setOrder(List<Order> next) {
        int previousCount = orders.size();

        orders.clear();
        if (next != null) {
            orders.addAll(next);
        }

        if (next != null && (previousCount == 0 && next.size() > 0 || next.size() > previousCount)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendNewOrderNotification();

                } else {
                    Toast.makeText(context, "didn't allow notifications", Toast.LENGTH_SHORT).show();
                }
            } else {
                sendNewOrderNotification();
            }
        }

        int old = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
        if (old != RecyclerView.NO_POSITION && selectionListener != null) {
            selectionListener.onOrderSelectionChanged(null, false);
        }
    }

    private void sendNewOrderNotification() {
        String channelId = "new_orders_channel";
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "New Orders", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.app)
                .setContentTitle("New Order!")
                .setContentText("Check the new order")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            NotificationManager notificationManager2 =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager2.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            Toast.makeText(context, "there is an order!, ensure that allow notifications in settings", Toast.LENGTH_LONG).show();
            return;
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


