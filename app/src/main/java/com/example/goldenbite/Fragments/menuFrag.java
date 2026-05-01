package com.example.goldenbite.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Adapters.MenuCategoryAdapter;
import com.example.goldenbite.Adapters.CustomerProductsAdapter;
import com.example.goldenbite.Dialogs.AddToCartDialogFragment;
import com.example.goldenbite.Classes.Product;
import com.example.goldenbite.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class menuFrag extends Fragment {

    private static final List<String> MENU_CATEGORIES = Arrays.asList(
            "Hot drinks",
            "Cold drinks",
            "Pancakes"
    );

    private RecyclerView productsRecycler;
    private TextView productsSectionTitle;
    private final List<Product> products = new ArrayList<>();
    private CustomerProductsAdapter productsAdapter;

    public menuFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        RecyclerView categoriesRecycler = view.findViewById(R.id.menu_categories_recycler);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));


        MenuCategoryAdapter.OnCategoryClickListener listener = category -> {
            productsSectionTitle.setVisibility(View.VISIBLE);
            productsSectionTitle.setText(category);
            loadProductsForCategory(category);
        };

        MenuCategoryAdapter categoryAdapter = new MenuCategoryAdapter(MENU_CATEGORIES, listener);
        categoriesRecycler.setAdapter(categoryAdapter);



        productsSectionTitle = view.findViewById(R.id.products_section_title);
        productsRecycler = view.findViewById(R.id.menu_products_recycler);
        productsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        productsAdapter = new CustomerProductsAdapter(products, product ->
                AddToCartDialogFragment.newInstance(product)
                        .show(getParentFragmentManager(), "addToCart"));
        productsRecycler.setAdapter(productsAdapter);

        return view;
    }

    private void loadProductsForCategory(String category) {
        FirebaseFirestore.getInstance()
                .collection("Product")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(getActivity(), snapshot -> {
                    products.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Long sizeLong = doc.getLong("size");
                        int size = sizeLong != null ? sizeLong.intValue() : 0;
                        if (size == 0) continue; // removed from menu

                        String docId = doc.getId();
                        String name = doc.getString("name");
                        Long priceLong = doc.getLong("price");
                        String description = doc.getString("description");
                        if (description == null) description = doc.getString("descirption");
                        String imagUrl = doc.getString("imagUrl");
                        String cat = doc.getString("category");

                        int price = priceLong != null ? priceLong.intValue() : 0;
                        Product p = new Product(docId, name != null ? name : "", price, size,
                                description != null ? description : "", imagUrl != null ? imagUrl : "", cat);
                        products.add(p);
                    }
                    productsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(getActivity(), e -> {
                    products.clear();
                    productsAdapter.notifyDataSetChanged();
                });
    }
}
