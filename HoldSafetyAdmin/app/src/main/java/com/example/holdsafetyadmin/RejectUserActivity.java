package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RejectUserActivity extends AppCompatActivity {
    FirebaseFirestore db;

    String id, userEmail;
    ImageView btnBack;
    Button btnSend;
    EditText etReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_user);

        db = FirebaseFirestore.getInstance();
        etReason = findViewById(R.id.txtReason);
        btnBack = findViewById(R.id.backArrow);
        btnSend = findViewById(R.id.btnSend);

        if(getIntent().hasExtra("userID")) {
            id = getIntent().getStringExtra("userID");
        }

        btnBack.setOnClickListener(view -> goBack());
        btnSend.setOnClickListener(v -> sendReasonForDisapproval());
    }

    public void sendReasonForDisapproval(){
        String reason = etReason.getText().toString().trim();

        if(TextUtils.isEmpty(reason)){
            etReason.setError("Please enter a message");
        } else {
            Toast.makeText(getApplicationContext(), "Reason for Disapproval Sent", Toast.LENGTH_SHORT).show();
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        userEmail = documentSnapshot.getString("Email");

                        Toast.makeText(getApplicationContext(), "User Email: " + userEmail, Toast.LENGTH_SHORT).show();

                        if(userEmail!=null){
                            //Send Email
                            String username = "holdsafety.ph@gmail.com";
                            String password = "HoldSafety@4qmag";
                            String subject = "Registration Failed - HoldSafety";
                            String message = "We're sorry to tell you that your registration to HoldSafety has been rejected." +
                                    "\nReason for failed registration: " + reason;

                            List<String> recipients = Collections.singletonList(userEmail);
                            new MailTask(RejectUserActivity.this).execute(username, password, recipients, subject, message);

                            Toast.makeText(RejectUserActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
                            updateUserDetails();
                        }
                    } else{
                        Toast.makeText(getApplicationContext(), "Snapshot Does Not Exist", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void updateUserDetails() {
        Map<String, Object> docUsers = new HashMap<>();
        docUsers.put("profileComplete", false);
        docUsers.put("isVerified", false);
        docUsers.put("imgUri", FieldValue.delete());

        db.collection("users").document(id).update(docUsers)
                .addOnSuccessListener(unused -> {
                    //DELETE USER'S PIC FROM FIRESTORE
                    FirebaseStorage.getInstance().getReference("id").child(id).delete();
                    Toast.makeText(getApplicationContext(), "User " + id +  " has changed details in db", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "User NOT validated.", Toast.LENGTH_SHORT).show());
        goBack();
    }

    private void goBack() {
        finish();
    }
}