package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void adminLogin(View view){
        //Toast.makeText(getApplicationContext(), "Admin Login", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (getApplicationContext(), AdminLandingActivity.class);
        startActivity(intent);
    }
}