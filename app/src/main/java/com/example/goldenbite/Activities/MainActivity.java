package com.example.goldenbite.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.goldenbite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {
    public boolean isLogin = false, isAdmin = false;
    private Button s,l;
    private EditText e,p;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            Intent intent;

            if (email != null && email.equals("saeedhigaze.3@gmail.com")) {
                intent = new Intent(MainActivity.this, MainActivity3.class);
            } else {
                intent = new Intent(MainActivity.this, MainActivity2.class);
            }
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        s=findViewById(R.id.signup);
        l=findViewById(R.id.login);
        e=findViewById(R.id.email);
        p=findViewById(R.id.password);

        auth=FirebaseAuth.getInstance();


        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = e.getText().toString().trim();
                String pass = p.getText().toString().trim();
                if (email.isEmpty()||pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length()<4){
                    Toast.makeText(MainActivity.this, "password must contain 5 num or chars", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "wrong mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            isLogin = true;
                            Intent intent;
                            String email = e.getText().toString();
                            if (email.equals("saeedhigaze.3@gmail.com")){
                                isAdmin = true;
                                Toast.makeText(MainActivity.this, "Admin signed in", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, MainActivity3.class);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, MainActivity2.class);
                            }
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "wrong mail or pass", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = e.getText().toString().trim();
                String pass = p.getText().toString().trim();
                if (email.isEmpty()||pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length()<4){
                    Toast.makeText(MainActivity.this, "password must contain 5 num or chars", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "wrong mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "user created", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "already created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "notifications allowed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "didn't allow notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }
}