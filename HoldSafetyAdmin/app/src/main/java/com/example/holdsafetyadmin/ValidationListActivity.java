package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class ValidationListActivity extends AppCompatActivity {
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
//        ValidationAdapter validationAdapter = new ValidationAdapter(this, userID, userFullName);
//        validationListRecyclerView.setAdapter(validationAdapter);
//        validationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        listUsers();
    }

    private void listUsers() {
        //Get the user from db
        db.collection("users").get().addOnCompleteListener(task -> {
            Intent thisActivity = new Intent(this, ValidationListActivity.class);
            //Toast.makeText(this, "Users fetched", Toast.LENGTH_SHORT).show();

            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot userSnap : task.getResult()) {
                    firstName = userSnap.getString("FirstName");
                    lastName = userSnap.getString("LastName");
                    isVerified = userSnap.getBoolean("isVerified");
                    Log.d("VERIFY", firstName + " " + lastName + "\nisVerified: " + isVerified); // this works

                    //Check if user has an ID pic uploaded
                    FirebaseStorage.getInstance()
                            .getReference("id").child(userSnap.getId()).getDownloadUrl() // this doesnt
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                //User has uploaded an id pic to be verified
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(!isVerified) {
                                        //Toast.makeText(ValidationListActivity.this, "BOOLEAN: " + isVerified, Toast.LENGTH_SHORT).show();
                                        View unverifiedView = getLayoutInflater().inflate(R.layout.unverified_row, null, false);

                                        TextView txtUserName = unverifiedView.findViewById(R.id.txtUserName);
                                        ImageView btnVerify = unverifiedView.findViewById(R.id.btnVerify);
                                        ImageView btnReject = unverifiedView.findViewById(R.id.btnReject);

                                        txtUserName.setText(firstName + " " + lastName);

                                        unverifiedView.setOnClickListener(view -> {
                                            //TODO: show image/img link on-tap
                                            //String url = FirebaseStorage.getInstance().getReference("id").child(userSnap.getString("ID")).getDownloadUrl().toString();
                                        });

                                        btnVerify.setOnClickListener(view -> {
                                            //User image is accepted
                                            Map<String, Object> docUsers = new HashMap<>();
                                            docUsers.put("isVerified", true);

                                            //Refresh page to remove already-verified user
                                            finish();
                                            startActivity(thisActivity);
                                        });

                                        btnReject.setOnClickListener(view -> {
                                            //No
                                            AlertDialog.Builder dialogRemoveAccount;
                                            dialogRemoveAccount = new AlertDialog.Builder(ValidationListActivity.this);
                                            dialogRemoveAccount.setTitle("Confirm Deletion");
                                            dialogRemoveAccount.setMessage("Are you sure you want to reject this user?");

                                            //Delete the image in user's acc if rejected
                                            dialogRemoveAccount.setPositiveButton("Reject", (dialog, which) -> {
                                                FirebaseStorage.getInstance()
                                                        .getReference("id").child(userSnap.getString("ID")).delete()
                                                        .addOnSuccessListener(v -> {
                                                            Toast.makeText(ValidationListActivity.this, "ID picture rejected. Deleted from storage.", Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener(v -> {
                                                            Toast.makeText(ValidationListActivity.this, "ID picture rejected. FAILED to delete.", Toast.LENGTH_LONG).show();
                                                        });
                                            });
                                            dialogRemoveAccount.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });

                                            //Refresh page to remove rejected user
                                            finish();
                                            startActivity(thisActivity);
                                        });
                                    } else {
                                        Toast.makeText(ValidationListActivity.this, "All users are verified.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                            //User has not uploaded a pic to be verified
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ValidationListActivity.this, "No unverified users.", Toast.LENGTH_LONG).show();
                                    }
                    });
                }
            } else {
                Toast.makeText(ValidationListActivity.this, "Get users task failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}