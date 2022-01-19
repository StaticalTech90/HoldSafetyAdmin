package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

public class RejectUserActivity extends AppCompatActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_user);

        if(getIntent().hasExtra("userID")) {
            id = getIntent().getStringExtra("userID");

        }
        sendReasonForDisapproval();
    }

    public void sendReasonForDisapproval(){
        //TODO: Send reason to user's email address
        Toast.makeText(getApplicationContext(), "Reason for Disapproval Sent", Toast.LENGTH_SHORT).show();

        //DELETE USER'S PIC FROM FIRESTORE
        FirebaseStorage.getInstance().getReference("id").child(id).delete();
    }
}