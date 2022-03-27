package com.example.holdsafetyadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    Button btnLogin;
    FirebaseAuth fAuth;
    TextView txtToggle, btnForgotPass;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtToggle = findViewById(R.id.txtToggle);
        btnForgotPass = findViewById(R.id.btnForgotPassword);

        txtToggle.setVisibility(View.GONE);
        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtPassword.getText().length() > 0) {
                    txtToggle.setVisibility(View.VISIBLE);
                }
                else {
                    txtToggle.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtToggle.setOnClickListener(view -> {
            if(txtToggle.getText() == "SHOW"){
                txtToggle.setText("HIDE");
                txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            else{
                txtToggle.setText("SHOW");
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            txtPassword.setSelection(txtPassword.length());
        });

        fAuth = FirebaseAuth.getInstance();

        //redirects user to landing page if already logged in
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, LandingActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(view -> {
            String email, password;

            email = txtEmail.getText().toString().trim();
            password = txtPassword.getText().toString();

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
        });

        btnForgotPass.setOnClickListener(view -> forgotPassword());
    }

    public void loginAdmin(String email,String password){
        //authentication
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                fAuth = FirebaseAuth.getInstance();
                FirebaseUser admin = fAuth.getCurrentUser();

                //pass admin to check if it exists in admin table
                checkUserAccount(admin);
            }
            else{
                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserAccount(FirebaseUser admin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        docRef = db.collection("admin").document(admin.getUid());

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, LandingActivity.class));
                finish();
            }
            else {
                //account does not exist in admin table = you are a user not an admin
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginActivity.this, "You are NOT an admin. Why are you here...", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void forgotPassword() {
        Intent forgotPass = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(forgotPass);
    }
}
