package com.example.goldenbite.Fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
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
import com.example.goldenbite.Classes.PhoneNum;
import com.example.goldenbite.R;
import com.example.goldenbite.Receivers.BootReceiver;
import com.example.goldenbite.Receivers.OrderReminderReceiver;


import java.util.Calendar;
import java.util.regex.Pattern;

public class cartFrag extends Fragment {

    private static final Pattern EXPIRY_MM_YY = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");

    public CartAdapter adapter;
    private TextView emptyView;
    private RecyclerView recyclerView;
    private EditText customerName;
    private EditText phone;
    private CheckBox delivery;
    private CheckBox cash;
    private CheckBox visa;
    private EditText cardNumber;
    private EditText cardExpiry;
    private EditText cardCvv;
    private Button orderButton;
    public Spinner spinner;
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
            private boolean isUpdating = false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                isUpdating = true;

                String originalText = s.toString().replace(" ", "");
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < originalText.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        sb.append(" ");
                    }
                    sb.append(originalText.charAt(i));
                }

                cardNumber.setText(sb.toString());
                cardNumber.setSelection(sb.length());

                isUpdating = false;
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
        Context context = getContext();
        if (context != null) {
            ComponentName component = new ComponentName(context, BootReceiver.class);
            context.getPackageManager().setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            );
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
        boolean isOrder = true;
        if (MainActivity2.cartItems.isEmpty()) {
            isOrder = false;
            toast(getString(R.string.order_error_cart_empty));
            return;
        }

        String name = customerName.getText() != null ? customerName.getText().toString().trim() : "";
        String phoneStr = phone.getText() != null ? phone.getText().toString().trim() : "";
        if (TextUtils.isEmpty(name)) {
            isOrder = false;
            toast(getString(R.string.order_error_name));
            return;
        }
        if (!(name.matches("^[a-zA-Z\\s]+$"))){
            isOrder = false;
            toast(getString(R.string.order_error_name_wrong));
            return;
        }
        if (TextUtils.isEmpty(phoneStr)) {
            isOrder = false;
            toast(getString(R.string.order_error_phone));
            return;
        }
        if (!(phoneStr.matches("^\\d{10}$"))){
            isOrder = false;
            toast(getString(R.string.order_error_phone_wrong));
            return;
        }

        boolean cashOn = cash.isChecked();
        boolean visaOn = visa.isChecked();
        if (cashOn && visaOn) {
            isOrder = false;
            toast(getString(R.string.order_error_payment_both));
            return;
        }
        if (!cashOn && !visaOn) {
            isOrder = false;
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

            String[] parts = expiry.split("/");
            int inputMonth = Integer.parseInt(parts[0]);
            int inputYear = Integer.parseInt(parts[1]) + 2000;

            Calendar now = Calendar.getInstance();
            int currentMonth = now.get(Calendar.MONTH) + 1;
            int currentYear = now.get(Calendar.YEAR);

            if (digits.length() != 16) {
                isOrder = false;
                toast(getString(R.string.order_error_card_number));
                return;
            }
            if (!EXPIRY_MM_YY.matcher(expiry).matches()) {
                isOrder = false;
                toast(getString(R.string.order_error_card_layout));
                return;
            }
            if (inputYear < currentYear || (inputYear == currentYear && inputMonth < currentMonth)) {
                isOrder = false;
                toast(getString(R.string.order_error_card_expiry));
                return;
            }
            if (!cvv.matches("\\d+") || (cvv.length() != 3 && cvv.length() != 4)) {
                isOrder = false;
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
        if (isOrder){
            String info = buildOrderInfo(name, delivery.isChecked());
            PhoneNum.phoneNumber = phoneStr;

            Order order;

            order = new Order(info, phoneStr, purchase, total, false, panValue, cvvValue);
            order.saveOrder();
            toast(getString(R.string.order_placed));
            scheduleOrderReminder();
        }
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
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }
}

