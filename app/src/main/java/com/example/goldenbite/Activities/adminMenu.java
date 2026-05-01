package com.example.goldenbite.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Classes.Product;
import com.example.goldenbite.Adapters.ProductListAdapter;
import com.example.goldenbite.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class adminMenu extends BaseActivity {

    private RecyclerView productsRecycler;
    private final List<Product> products = new ArrayList<>();
    private ProductListAdapter adapter;
    private String selectedCategory;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);


        Spinner spinner = findViewById(R.id.change_category_spinner);
        productsRecycler = findViewById(R.id.change_products_recycler);
        productsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductListAdapter(products, this::onProductSelected);
        productsRecycler.setAdapter(adapter);

        String[] categories = getResources().getStringArray(R.array.menu_categories);
        selectedCategory = categories[0];

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
                loadProductsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        loadProductsByCategory(selectedCategory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedCategory != null) {
            loadProductsByCategory(selectedCategory);
        }
    }

    private void onProductSelected(Product product) {
        selectedProduct = product;
    }

    private void loadProductsByCategory(String category) {
        FirebaseFirestore.getInstance()
                .collection("Product")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(this, snapshot -> {
                    products.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String docId = doc.getId();
                        String name = doc.getString("name");
                        Long priceLong = doc.getLong("price");
                        Long sizeLong = doc.getLong("size");
                        String description = doc.getString("description");
                        if (description == null) description = doc.getString("descirption");
                        String imagUrl = doc.getString("imagUrl");
                        String cat = doc.getString("category");

                        int price = priceLong != null ? priceLong.intValue() : 0;
                        int size = sizeLong != null ? sizeLong.intValue() : 0;
                        Product p = new Product(docId, name != null ? name : "", price, size,
                                description != null ? description : "", imagUrl != null ? imagUrl : "", cat);
                        products.add(p);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    products.clear();
                    adapter.notifyDataSetChanged();
                });
    }

    public void goBack(View view) {
        Intent intent = new Intent(adminMenu.this, MainActivity3.class);
        startActivity(intent);
    }

    public void addP(View view) {
        Intent intent = new Intent(adminMenu.this, addProduct.class);
        startActivity(intent);
    }

    public void editProduct(View view) {
        if (selectedProduct == null) {
            Toast.makeText(this, "Select a product to edit", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(adminMenu.this, EditProductActivity.class);
        intent.putExtra(EditProductActivity.EXTRA_PRODUCT_DOC_ID, selectedProduct.getDocumentId());
        startActivity(intent);
    }


}