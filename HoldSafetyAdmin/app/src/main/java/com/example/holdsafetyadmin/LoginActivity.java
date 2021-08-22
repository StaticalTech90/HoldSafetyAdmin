package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
      EditText txtEmail, txtPassword;
      Button btnLogin;
      FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        fAuth = FirebaseAuth.getInstance();

        //redirects user to landing page if already logged in
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, LandingActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;

                email = txtEmail.getText().toString().trim();
                password = txtPassword.getText().toString().trim();

                Toast.makeText(LoginActivity.this, "Button works", Toast.LENGTH_SHORT).show();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    txtEmail.setError("Email is required");
                    txtPassword.setError("Password is required");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    txtEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    txtPassword.setError("Password is required");
                    return;
                }

                loginAdmin(email,password);
            }
        });
    }

    public void loginAdmin(String email,String password){
        //authentication
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, LandingActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
