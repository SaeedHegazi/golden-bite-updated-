package com.example.goldenbite.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenbite.Activities.MainActivity2;
import com.example.goldenbite.Adapters.CartAdapter;
import com.example.goldenbite.Classes.Cart;
import com.example.goldenbite.Classes.Order;
import com.example.goldenbite.R;
import com.example.goldenbite.Receivers.OrderReminderReceiver;


import java.util.regex.Pattern;

public class cartFrag extends Fragment {

    private static final Pattern EXPIRY_MM_YY = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");

    public CartAdapter adapter;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private EditText customerName;
    private EditText phone;
    public static String phoneNum;
    private CheckBox delivery;
    private CheckBox cash;
    private CheckBox visa;
    private EditText cardNumber;
    private EditText cardExpiry;
    private EditText cardCvv;
    private Button orderButton;

    Spinner spinner;


    public cartFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        emptyView = root.findViewById(R.id.cart_empty);
        recyclerView = root.findViewById(R.id.cart_recycler);
        customerName = root.findViewById(R.id.cart_customer_name);
        phone = root.findViewById(R.id.cart_phone);
        delivery = root.findViewById(R.id.cart_delivery);
        cash = root.findViewById(R.id.cart_cash);
        visa = root.findViewById(R.id.cart_visa);
        cardNumber = root.findViewById(R.id.cart_card_number);
        cardExpiry = root.findViewById(R.id.cart_card_expiry);
        cardCvv = root.findViewById(R.id.cart_card_cvv);
        spinner = root.findViewById(R.id.spinnerCountries);
        orderButton = root.findViewById(R.id.cart_order);

        cardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c)) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.countries_array,
                android.R.layout.simple_spinner_item
        );

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        int position = adapter1.getPosition("Israel");
        spinner.setSelection(position);



        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CartAdapter(requireContext(), MainActivity2.cartItems, new CartAdapter.CartUpdateListener() {
            @Override
            public void onCartChanged() {
                refreshCart();
            }
        });
        recyclerView.setAdapter(adapter);
        refreshEmptyState();

        orderButton.setOnClickListener(v -> submitOrder());
        return root;
    }



    @Override
    public void onResume() {
        super.onResume();
        refreshCart();
    }

    public void refreshCart(){
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            refreshEmptyState();
        }
    }


    private void refreshEmptyState() {
        boolean empty = MainActivity2.cartItems.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void submitOrder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                completeOrder();

            } else {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        } else {
            completeOrder();
        }
    }

    private static String cardDigitsOnly(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }


    private String buildOrderInfo(String name, boolean isDelivery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(name).append("\n");
        sb.append("Delivery: ").append(isDelivery ? "yes" : "no").append("\n");
        sb.append("Products:\n");
        for (Cart c : MainActivity2.cartItems) {
            sb.append(" - ")
                    .append(c.getPname())
                    .append(" x").append(c.getCount())
                    .append(" (").append(c.getSize()).append(")\n");
        }
        return sb.toString().trim();
    }

    public void completeOrder(){
        if (MainActivity2.cartItems.isEmpty()) {
            toast(getString(R.string.order_error_cart_empty));
            return;
        }

        String name = customerName.getText() != null ? customerName.getText().toString().trim() : "";
        String phoneStr = phone.getText() != null ? phone.getText().toString().trim() : "";
        if (TextUtils.isEmpty(name)) {
            toast(getString(R.string.order_error_name));
            return;
        }
        if (TextUtils.isEmpty(phoneStr)) {
            toast(getString(R.string.order_error_phone));
            return;
        }

        boolean cashOn = cash.isChecked();
        boolean visaOn = visa.isChecked();
        if (cashOn && visaOn) {
            toast(getString(R.string.order_error_payment_both));
            return;
        }
        if (!cashOn && !visaOn) {
            toast(getString(R.string.order_error_payment_none));
            return;
        }

        String purchase;
        String panValue = "";
        String cvvValue = "";
        if (visaOn) {
            String digits = cardDigitsOnly(cardNumber.getText() != null ? cardNumber.getText().toString() : "");
            String expiry = cardExpiry.getText() != null ? cardExpiry.getText().toString().trim() : "";
            String cvv = cardCvv.getText() != null ? cardCvv.getText().toString().trim() : "";

            if (digits.length() != 19) {
                toast(getString(R.string.order_error_card_number));
                return;
            }
            if (!EXPIRY_MM_YY.matcher(expiry).matches()) {
                toast(getString(R.string.order_error_card_expiry));
                return;
            }
            if (!cvv.matches("\\d+") || (cvv.length() != 3 && cvv.length() != 4)) {
                toast(getString(R.string.order_error_card_cvv));
                return;
            }
            purchase = "visa";
            panValue = digits;
            cvvValue = cvv;
        } else {
            purchase = "cash";
        }

        double total = 0;
        for (Cart c : MainActivity2.cartItems) {
            total += (double) c.getPrice() * c.getCount();
        }
        if (delivery.isChecked()) {
            total += 20;
        }

        String info = buildOrderInfo(name, delivery.isChecked());
        phoneNum = phoneStr;

        Order order;

        order = new Order(info, phoneNum, purchase, total, false, true, panValue, cvvValue);
        order.saveOrder();
        toast(getString(R.string.order_placed));
        scheduleOrderReminder();
        new OrderReminderReceiver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                completeOrder();
            } else {
                completeOrder();
                Toast.makeText(getContext(), "didn't allow notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleOrderReminder() {

        Intent intent = new Intent(getContext(), OrderReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        long triggerTime = System.currentTimeMillis() + 5 * 1000;

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }
}

