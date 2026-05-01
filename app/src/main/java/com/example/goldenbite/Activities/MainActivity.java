package com.example.goldenbite.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.goldenbite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity {
    public boolean isLogin = false, isAdmin = false;
    private Button s,l;
    private EditText e,p;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                    Toast.makeText(MainActivity.this, "fill all fields", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.length()<4){
                    Toast.makeText(MainActivity.this, "password must contain 5 num or chars", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "wrong mail", Toast.LENGTH_LONG).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()){
                            isLogin = true;
                            Intent intent;
                            String pass= p.getText().toString();
                            if (pass.equals("admin")){
                                isAdmin = true;
                                Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, MainActivity3.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "wrong mail or pass", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "fill all fields", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.length()<4){
                    Toast.makeText(MainActivity.this, "password must contain 5 num or chars", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "wrong mail", Toast.LENGTH_LONG).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "user created", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "already created", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });



    }

}