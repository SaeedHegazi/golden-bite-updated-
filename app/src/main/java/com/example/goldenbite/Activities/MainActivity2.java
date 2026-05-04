package com.example.goldenbite.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.goldenbite.Classes.Cart;
import com.example.goldenbite.R;
import com.example.goldenbite.Fragments.cartFrag;
import com.example.goldenbite.Fragments.menuFrag;
import com.example.goldenbite.Fragments.orderFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity2 extends BaseActivity {

    public static final ArrayList<Cart> cartItems = new ArrayList<>();
    private BottomNavigationView nav1;
    public static FrameLayout menuP,cartP,orderP;
    private menuFrag menuFrag1;
    private cartFrag cartFrag1;
    private orderFrag orderFrag1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        menuP = findViewById(R.id.menuF);
        cartP = findViewById(R.id.cartF);
        orderP = findViewById(R.id.orderF);
        menuFrag1=new menuFrag();
        cartFrag1 =new cartFrag();
        orderFrag1 =new orderFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.menuF,menuFrag1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.cartF, cartFrag1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.orderF, orderFrag1).commit();
        menuP.setVisibility(View.VISIBLE);
        cartP.setVisibility(View.INVISIBLE);
        orderP.setVisibility(View.INVISIBLE);
        nav1=findViewById(R.id.customerNav);

        nav1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.home){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent= new Intent(MainActivity2.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if(menuItem.getItemId()==R.id.menu){
                    menuP.setVisibility(View.VISIBLE);
                    cartP.setVisibility(View.INVISIBLE);
                    orderP.setVisibility(View.INVISIBLE);
                }
                if(menuItem.getItemId()==R.id.cart){
                    menuP.setVisibility(View.INVISIBLE);
                    cartP.setVisibility(View.VISIBLE);
                    orderP.setVisibility(View.INVISIBLE);
                    cartFrag1.refreshCart();
                }
                if(menuItem.getItemId()==R.id.order){
                    menuP.setVisibility(View.INVISIBLE);
                    cartP.setVisibility(View.INVISIBLE);
                    orderP.setVisibility(View.VISIBLE);
                    if (orderFrag1 instanceof orderFrag) {
                        ((orderFrag) orderFrag1).attachOrdersListener();
                    }
                }
                return false;
            }
        });

    }
}