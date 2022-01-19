package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class VerificationListActivity extends AppCompatActivity {
    FirebaseFirestore db;

    LinearLayout verificationView;
    RecyclerView validationListRecyclerView;
    String userID[], userFullName[];

    String firstName, lastName;
    Boolean isVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_list);

        db = FirebaseFirestore.getInstance();

        verificationView = findViewById(R.id.linearUserList);

//        userID = getResources().getStringArray(R.array.userID);
//        userFullName = getResources().getStringArray(R.array.userFullName);
//
//        validationListRecyclerView = findViewById(R.id.recyclerviewValidationList);
//
//        VerificationAdapter validationAdapter = new VerificationAdapter(this, userID, userFullName);
//        validationListRecyclerView.setAdapter(validationAdapter);
//        validationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        listUsers();
    }

    private void listUsers() {
        //TODO: Only show unverified users who also have an uploaded id-pic.
        db.collection("users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) { //USERS ARE FETCHED
                for(QueryDocumentSnapshot userSnap : task.getResult()) {
                    firstName = userSnap.getString("FirstName");
                    lastName = userSnap.getString("LastName");
                    isVerified = userSnap.getBoolean("isVerified");
                    Toast.makeText(getApplicationContext(), "Query: " + lastName, Toast.LENGTH_LONG).show();

                    View unverifiedView = getLayoutInflater().inflate(R.layout.verification_row, null, false);
                    if(!isVerified) {
                        //CHECK IF USER HAS ID PIC UPLOADED
                        FirebaseStorage.getInstance().getReference("id").child(userSnap.getId()).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        firstName = userSnap.getString("FirstName");
                                        lastName = userSnap.getString("LastName");
                                        isVerified = userSnap.getBoolean("isVerified");

                                        //DISPLAY USER IN VIEW
                                        TextView txtUserID = unverifiedView.findViewById(R.id.txtUserID);
                                        TextView txtUserName = unverifiedView.findViewById(R.id.txtUserName);

                                        txtUserID.setText(userSnap.getId());
                                        txtUserName.setText(lastName + ", " + firstName);

                                        Toast.makeText(getApplicationContext(), "onSuccess: " + lastName, Toast.LENGTH_LONG).show();


                                        unverifiedView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent selectedUser = new Intent(VerificationListActivity.this, RegistrationDetailsActivity.class);
                                                selectedUser.putExtra("userID", userSnap.getId());
                                                startActivity(selectedUser);
                                            }
                                        });
                                        verificationView.addView(unverifiedView);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(VerificationListActivity.this, "onFailure: " + lastName + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                        });
                    }
                }
            } else {
                //DB NOT ACCESSED
            }
        });
    }
}