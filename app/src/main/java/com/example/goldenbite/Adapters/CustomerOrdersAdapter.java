package com.example.goldenbite.Adapters;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.VH> {

    private final List<Order> orders = new ArrayList<>();
    private Context context;

    public CustomerOrdersAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<Order> next) {
        int previousCount = orders.size();

        orders.clear();
        if (next != null) {
            orders.addAll(next);
        }
        if (next != null && next.size() < previousCount && previousCount > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendOrderReadyNotification();

                } else {
                    Toast.makeText(context, "didn't allow notifications", Toast.LENGTH_SHORT).show();
                }
            } else {
                sendOrderReadyNotification();
            }
        }
        notifyDataSetChanged();
    }

    private void sendOrderReadyNotification() {
        String channelId = "customer_notifications";
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Order Updates", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.app)
                .setContentTitle("Your order is ready")
                .setContentText("your order is ready, enjoy!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
        else {
            Toast.makeText(context, "order ready,allow notifications in settings", Toast.LENGTH_LONG).show();
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

