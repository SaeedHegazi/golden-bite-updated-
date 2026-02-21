package com.example.goldenbite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button s,l;
    private EditText e,p,ephone;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s=findViewById(R.id.signup);
        l=findViewById(R.id.login);
        e=findViewById(R.id.email);
        p=findViewById(R.id.password);
        ephone=findViewById(R.id.phone);

        auth=FirebaseAuth.getInstance();

        //login as an admin and as a customer

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
                            Intent intent;
                            String pass= p.getText().toString();
                            if (pass.equals("admin")){
                                Toast.makeText(MainActivity.this, "signed in", Toast.LENGTH_LONG).show();
                                intent = new Intent(MainActivity.this, MainActivity3.class);
                                startActivity(intent);
                            }
                            else {
                                String phone = ephone.getText().toString().trim();
                                if (phone.isEmpty()){
                                    Toast.makeText(MainActivity.this, "you will need your phone number later", Toast.LENGTH_LONG).show();
                                }
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