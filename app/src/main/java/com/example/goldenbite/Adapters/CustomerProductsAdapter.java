package com.example.goldenbite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.goldenbite.Classes.Product;
import com.example.goldenbite.R;

import java.util.List;


public class CustomerProductsAdapter extends RecyclerView.Adapter<CustomerProductsAdapter.ViewHolder> {

    private final List<Product> list;
    private final OnProductClickListener productClickListener;


    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public CustomerProductsAdapter(List<Product> list, OnProductClickListener productClickListener) {
        this.list = list;
        this.productClickListener = productClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = list.get(position);
        holder.name.setText(p.getName());
        holder.description.setText(p.getDescription());
        holder.price.setText(String.valueOf(p.getPrice()));

        String url = p.getImagUrl();
        if (url != null && !url.isEmpty()) {
            // استخدام context الخاص بـ itemView للـ Glide
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .centerCrop()
                    .into(holder.image);
        } else {
            holder.image.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name, price, description;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            description = itemView.findViewById(R.id.product_description);
        }
    }
}

