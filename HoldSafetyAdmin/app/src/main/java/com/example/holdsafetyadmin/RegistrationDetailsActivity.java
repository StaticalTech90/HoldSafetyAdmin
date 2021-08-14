package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationDetailsActivity extends AppCompatActivity {
    TextView userID;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        userID = findViewById(R.id.lblUserID);
        getData();
        setData();
    }

    private void getData(){
        if(getIntent().hasExtra("userID")){
            id = getIntent().getStringExtra("userID");
        }

        else{
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    private  void setData(){
        userID.setText("User ID: " + id);
    }

    public void validateUser(View view){
        Toast.makeText(getApplicationContext(), "Validate User", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent (this, AdminLandingActivity.class);
//        startActivity(intent);
    }

    public void rejectUser(View view){
        //Toast.makeText(getApplicationContext(), "Reject User", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (this, RejectUserActivity.class);
        startActivity(intent);
    }
}