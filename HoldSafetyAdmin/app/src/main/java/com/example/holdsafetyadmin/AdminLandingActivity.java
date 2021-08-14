package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AdminLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_landing);
    }

    public void viewReport(View view){
        Toast.makeText(getApplicationContext(), "View Report", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent (this, AccountDetails.class);
//        startActivity(intent);
    }

    public void manageCoordinatedBrgys(View view){
        Toast.makeText(getApplicationContext(), "Coordinated Barangays", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent (this, AccountDetails.class);
//        startActivity(intent);
    }

    public void validateUser(View view){
        //Toast.makeText(getApplicationContext(), "Verify Registration", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (this, ValidationListActivity.class);
        startActivity(intent);
    }

    public void adminLogout(View view){
        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent (this, AccountDetails.class);
//        startActivity(intent);
    }
}