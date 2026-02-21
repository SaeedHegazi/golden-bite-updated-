package com.example.goldenbite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class adminMenu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);




    }


    public void goBack(View view) {
        Intent intent = new Intent(adminMenu.this, MainActivity2.class);
        startActivity(intent);
    }

    public void addP(View view) {
        Intent intent = new Intent(adminMenu.this, addProduct.class);
        startActivity(intent);

    }
}