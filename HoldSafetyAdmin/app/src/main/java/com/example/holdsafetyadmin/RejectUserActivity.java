package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

public class RejectUserActivity extends AppCompatActivity {
    String id;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_user);

        btnSend = findViewById(R.id.btnSend);

        if(getIntent().hasExtra("userID")) {
            id = getIntent().getStringExtra("userID");

        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReasonForDisapproval();
            }
        });
    }

    public void sendReasonForDisapproval(){
        //TODO: Send reason to user's email address
        Toast.makeText(getApplicationContext(), "Reason for Disapproval Sent", Toast.LENGTH_SHORT).show();

        //DELETE USER'S PIC FROM FIRESTORE
        FirebaseStorage.getInstance().getReference("id").child(id).delete();
    }
}