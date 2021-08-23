package com.example.holdsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void userLogin(View view){
        //Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (getApplicationContext(), MenuActivity.class);
        startActivity(intent);
    }

    public void userLoginWithGoogle(View view){
        Intent intent = new Intent (getApplicationContext(), RegisterGoogleActivity.class);
        startActivity(intent);
    }

    public void userSignUp(View view){
        Intent intent = new Intent (getApplicationContext(), Register1Activity.class);
        startActivity(intent);
    }

    public void userForgotPassword(View view){
        Intent intent = new Intent (getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }




}