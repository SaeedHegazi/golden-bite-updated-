package com.example.goldenbite.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Cart;
import com.example.goldenbite.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    private Context context;
    private List<Cart> items;
    private CartUpdateListener listener;


    public interface CartUpdateListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<Cart> items, CartUpdateListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Cart c = items.get(position);
        holder.name.setText(c.getPname());
        holder.count.setText(String.valueOf(c.getCount()));
        holder.size.setText(c.getSize());
        holder.price.setText(String.valueOf(c.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            final Cart line = items.get(pos);

            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.cart_delete_title)
                    .setMessage(context.getString(R.string.cart_delete_message, line.getPname()))
                    .setNegativeButton(R.string.cart_delete_cancel, null)
                    .setPositiveButton(R.string.cart_delete_confirm, (dialog, which) -> {
                        items.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, items.size());


                        if (listener != null) listener.onCartChanged();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView name, count, size, price;
        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_item_name);
            count = itemView.findViewById(R.id.cart_item_count);
            size = itemView.findViewById(R.id.cart_item_size);
            price = itemView.findViewById(R.id.cart_item_price);
        }
    }
}
