package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LandingActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                finish();
            }
        });
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
}
