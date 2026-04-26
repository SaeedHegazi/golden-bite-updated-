package com.example.goldenbite;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class AddToCartDialogFragment extends DialogFragment {

    public static final String ARG_NAME = "name";
    public static final String ARG_PRICE = "price";
    public static final String ARG_SIZE_MODE = "sizeMode";
    private String productName;
    private int basePrice;
    private int sizeMode;
    private int count = 1;
    private boolean largeSelected;

    private TextView tvCount;
    private MaterialButton btnSmall;
    private MaterialButton btnLarge;

    public static AddToCartDialogFragment newInstance(Product product) {
        AddToCartDialogFragment f = new AddToCartDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, product.getName());
        args.putInt(ARG_PRICE, product.getPrice());
        args.putInt(ARG_SIZE_MODE, product.getSize());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            dismiss();
            return;
        }
        productName = args.getString(ARG_NAME, "");
        basePrice = args.getInt(ARG_PRICE, 0);
        sizeMode = args.getInt(ARG_SIZE_MODE, 1);
        if (sizeMode == 1) {
            largeSelected = true;
        } else {
            largeSelected = false;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_to_cart, null);
        tvCount = root.findViewById(R.id.tv_product_count);
        MaterialButton btnMinus = root.findViewById(R.id.btn_count_minus);
        MaterialButton btnPlus = root.findViewById(R.id.btn_count_plus);
        btnSmall = root.findViewById(R.id.btn_size_small);
        btnLarge = root.findViewById(R.id.btn_size_large);
        MaterialButton btnAdd = root.findViewById(R.id.btn_add_to_cart);

        updateCountDisplay();
        configureSizeButtons();

        btnMinus.setOnClickListener(v -> {
            if (count > 1) {
                count--;
                updateCountDisplay();
            }
        });
        btnPlus.setOnClickListener(v -> {
            if (count < 99) {
                count++;
                updateCountDisplay();
            }
        });

        btnSmall.setOnClickListener(v -> {
            if (sizeMode != 2) return;
            largeSelected = false;
            refreshSizeSelectionUi();
        });
        btnLarge.setOnClickListener(v -> {
            largeSelected = true;
            refreshSizeSelectionUi();
        });

        btnAdd.setOnClickListener(v -> {
            int unitPrice = unitPriceForSelection();
            String sizeLabel = largeSelected
                    ? getString(R.string.size_large)
                    : getString(R.string.size_small);
            MainActivity2.cartItems.add(new Cart(productName, count, sizeLabel, unitPrice));
            dismiss();
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(productName)
                .setView(root)
                .create();
    }

    private void updateCountDisplay() {
        tvCount.setText(String.valueOf(count));
    }

    private void configureSizeButtons() {
        if (sizeMode == 1) {
            btnSmall.setEnabled(false);
            btnSmall.setClickable(false);
            btnSmall.setAlpha(0.38f);
            largeSelected = true;
        } else {
            btnSmall.setEnabled(true);
            btnSmall.setClickable(true);
            btnSmall.setAlpha(1f);
        }
        refreshSizeSelectionUi();
    }

    private void refreshSizeSelectionUi() {
        int accent = ContextCompat.getColor(requireContext(), R.color.add_to_cart_accent);
        int onAccent = ContextCompat.getColor(requireContext(), R.color.white);
        int surface = ContextCompat.getColor(requireContext(), R.color.white);
        int disabledBg = ContextCompat.getColor(requireContext(), R.color.cart_size_disabled_bg);
        int disabledFg = ContextCompat.getColor(requireContext(), R.color.cart_size_disabled_fg);

        applySizeButtonStyle(btnLarge, largeSelected, accent, onAccent, surface);
        applySizeButtonStyle(btnSmall, !largeSelected, accent, onAccent, surface);

        if (sizeMode == 1) {
            btnSmall.setBackgroundTintList(ColorStateList.valueOf(disabledBg));
            btnSmall.setTextColor(disabledFg);
            btnSmall.setStrokeWidth(0);
        }
    }

    private void applySizeButtonStyle(MaterialButton btn, boolean selected,
                                      int accent, int onAccent, int surface) {
        if (selected) {
            btn.setBackgroundTintList(ColorStateList.valueOf(accent));
            btn.setTextColor(onAccent);
            btn.setStrokeWidth(0);
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(surface));
            btn.setTextColor(accent);
            btn.setStrokeColor(ColorStateList.valueOf(accent));
            int strokePx = Math.max(1, (int) (btn.getResources().getDisplayMetrics().density + 0.5f));
            btn.setStrokeWidth(strokePx);
        }
    }

    private int unitPriceForSelection() {
        if (sizeMode == 1) {
            return basePrice;
        }
        return largeSelected ? basePrice + 5 : basePrice;
    }
}
