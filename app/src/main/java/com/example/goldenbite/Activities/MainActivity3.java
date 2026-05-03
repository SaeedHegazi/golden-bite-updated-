package com.example.goldenbite.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.goldenbite.R;
import com.example.goldenbite.Fragments.adminOrderFrag;
import com.example.goldenbite.Fragments.adminSettingsFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity3 extends BaseActivity {
    private BottomNavigationView adminNav1;
    public static FrameLayout adminOrdersP,adminSettingsP;
    private adminOrderFrag adOrder;
    private adminSettingsFrag adSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        adminOrdersP = findViewById(R.id.adminOrderF);
        adminSettingsP = findViewById(R.id.adminSettingsF);
        adOrder=new adminOrderFrag();
        adSettings=new adminSettingsFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.adminOrderF, adOrder).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.adminSettingsF, adSettings).commit();
        adminOrdersP.setVisibility(View.VISIBLE);
        adminSettingsP.setVisibility(View.INVISIBLE);
        adminNav1=findViewById(R.id.adminNav);
        adminNav1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.adminHome){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(MainActivity3.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (menuItem.getItemId()==R.id.adminOrders){
                    adminOrdersP.setVisibility(View.VISIBLE);
                    adminSettingsP.setVisibility(View.INVISIBLE);
                }
                if (menuItem.getItemId()==R.id.adminSettings){
                    adminOrdersP.setVisibility(View.INVISIBLE);
                    adminSettingsP.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }
}