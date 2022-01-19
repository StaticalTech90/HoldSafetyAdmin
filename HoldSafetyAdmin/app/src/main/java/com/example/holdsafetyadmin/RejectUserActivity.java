package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RejectUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_user);

        sendReasonForDisapproval();
    }

    public void sendReasonForDisapproval(){
        //TODO: Send reason to user's email address
        Toast.makeText(getApplicationContext(), "Reason for Disapproval Sent", Toast.LENGTH_SHORT).show();
    }
}