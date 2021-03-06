package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class VerificationListActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    LogHelper logHelper;

    LinearLayout verificationView;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        logHelper = new LogHelper(VerificationListActivity.this, mAuth,user, this);

        verificationView = findViewById(R.id.linearUserList);
        btnBack = findViewById(R.id.backArrow);

        btnBack.setOnClickListener(view -> goBack());

        listUsers();
    }

    private void listUsers() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) { //USERS ARE FETCHED
                for(QueryDocumentSnapshot userSnap : task.getResult()) {
                    String displayName = userSnap.getString("FirstName") + " " + userSnap.getString("LastName");
                    Boolean isVerified = userSnap.getBoolean("isVerified");

                    View unverifiedView = getLayoutInflater().inflate(R.layout.verification_row, null, false);
                    if(!isVerified) {
                        //CHECK IF USER HAS ID PIC UPLOADED
                        FirebaseStorage.getInstance().getReference("id").child(userSnap.getId()).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    //ASSIGN TO UI
                                    TextView txtUserID = unverifiedView.findViewById(R.id.txtUserID);
                                    TextView txtUserName = unverifiedView.findViewById(R.id.txtUserName);

                                    txtUserID.setText(userSnap.getString("ID"));
                                    txtUserName.setText(displayName);

                                    //SET ONCLICK PER ROW
                                    unverifiedView.setOnClickListener(v -> {
                                        Intent selectedUser = new Intent(VerificationListActivity.this, RegistrationDetailsActivity.class);
                                        selectedUser.putExtra("userID", userSnap.getId());
                                        finish();
                                        startActivity(selectedUser);

                                    });

                                    logHelper.saveToFirebase("listUsers", "SUCCESS", "user added to view");
                                    //ADD TO VIEW
                                    verificationView.addView(unverifiedView);
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }
            }
        });
    }

    private void goBack() {
        startActivity(new Intent(VerificationListActivity.this, LandingActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}