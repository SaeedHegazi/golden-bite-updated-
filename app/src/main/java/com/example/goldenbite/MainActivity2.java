package com.example.goldenbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity2 extends AppCompatActivity {
    private BottomNavigationView nav1;
    public static FrameLayout menuP,orderP,orderTimeP,aiP;
    private menuFrag menuFrag1;
    private cartFrag cartFrag1;
    private orderFrag orderFrag1;
    private aiFrag aiFrag1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        menuP = findViewById(R.id.menuF);
        orderP = findViewById(R.id.orderF);
        orderTimeP = findViewById(R.id.orderTimeF);
        aiP = findViewById(R.id.aiF);
        menuFrag1=new menuFrag();
        cartFrag1 =new cartFrag();
        orderFrag1 =new orderFrag();
        aiFrag1=new aiFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.menuF,menuFrag1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.orderF, cartFrag1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.orderTimeF, orderFrag1).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.aiF,aiFrag1).commit();
        menuP.setVisibility(View.VISIBLE);
        orderP.setVisibility(View.INVISIBLE);
        orderTimeP.setVisibility(View.INVISIBLE);
        aiP.setVisibility(View.INVISIBLE);
        nav1=findViewById(R.id.customerNav);
        nav1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.home){
                    Intent intent= new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(intent);
                }
                if(menuItem.getItemId()==R.id.menu){
                    menuP.setVisibility(View.VISIBLE);
                    orderP.setVisibility(View.INVISIBLE);
                    orderTimeP.setVisibility(View.INVISIBLE);
                    aiP.setVisibility(View.INVISIBLE);
                }
                if(menuItem.getItemId()==R.id.order){
                    menuP.setVisibility(View.INVISIBLE);
                    orderP.setVisibility(View.VISIBLE);
                    orderTimeP.setVisibility(View.INVISIBLE);
                    aiP.setVisibility(View.INVISIBLE);
                }
                if(menuItem.getItemId()==R.id.time){
                    menuP.setVisibility(View.INVISIBLE);
                    orderP.setVisibility(View.INVISIBLE);
                    orderTimeP.setVisibility(View.VISIBLE);
                    aiP.setVisibility(View.INVISIBLE);
                }
                if (menuItem.getItemId()==R.id.ai){
                    menuP.setVisibility(View.INVISIBLE);
                    orderP.setVisibility(View.INVISIBLE);
                    orderTimeP.setVisibility(View.INVISIBLE);
                    aiP.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

    }
}