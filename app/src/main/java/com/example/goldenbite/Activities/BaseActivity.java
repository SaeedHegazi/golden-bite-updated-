package com.example.goldenbite.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldenbite.R;

public class BaseActivity extends AppCompatActivity {

    private View noInternetView;
    private LinearLayout layoutNoInternet;
    private Button btnRetry;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noInternetView = getLayoutInflater().inflate(R.layout.layout_no_internet, null);

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(noInternetView);

        initNoInternetView();
        setupConnectivityCallback();

    }

    private void initNoInternetView() {
        layoutNoInternet = noInternetView.findViewById(R.id.layoutNoInternet);
        btnRetry = noInternetView.findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> checkAndUpdateUI());
    }

    private void setupConnectivityCallback() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(() -> layoutNoInternet.setVisibility(View.GONE));
            }

            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> layoutNoInternet.setVisibility(View.VISIBLE));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (connectivityManager != null && networkCallback != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        }


        checkAndUpdateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAndUpdateUI() {
        if (isInternetAvailable()) {
            layoutNoInternet.setVisibility(View.GONE);
        } else {
            layoutNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);

            return capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } else {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
    }
}