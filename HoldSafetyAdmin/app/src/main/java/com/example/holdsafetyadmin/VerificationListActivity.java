package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class VerificationListActivity extends AppCompatActivity {
    FirebaseFirestore db;

    LinearLayout verificationView;
    //String displayName;
    //Boolean isVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_list);

        db = FirebaseFirestore.getInstance();

        verificationView = findViewById(R.id.linearUserList);

        listUsers();
    }

    private void listUsers() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) { //USERS ARE FETCHED
                for(QueryDocumentSnapshot userSnap : task.getResult()) {
                    String displayName = userSnap.getString("FirstName") + " " + userSnap.getString("LastName");
                    Boolean isVerified = userSnap.getBoolean("isVerified");
                    Toast.makeText(getApplicationContext(), "Query: " + displayName, Toast.LENGTH_LONG).show();

                    View unverifiedView = getLayoutInflater().inflate(R.layout.verification_row, null, false);
                    if(!isVerified) {
                        //CHECK IF USER HAS ID PIC UPLOADED
                        FirebaseStorage.getInstance().getReference("id").child(userSnap.getId()).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    //ASSIGN TO UI
                                    TextView txtUserID = unverifiedView.findViewById(R.id.txtUserID);
                                    TextView txtUserName = unverifiedView.findViewById(R.id.txtUserName);

                                    txtUserID.setText(userSnap.getId());
                                    txtUserName.setText(displayName);

                                    Toast.makeText(getApplicationContext(), "onSuccess: " + displayName, Toast.LENGTH_LONG).show();

                                    //SET ONCLICK PER ROW
                                    unverifiedView.setOnClickListener(v -> {
                                        Intent selectedUser = new Intent(VerificationListActivity.this, RegistrationDetailsActivity.class);
                                        selectedUser.putExtra("userID", userSnap.getId());
                                        startActivity(selectedUser);
                                    });
                                    //ADD TO VIEW
                                    verificationView.addView(unverifiedView);
                                })
                                .addOnFailureListener(e -> Toast.makeText(VerificationListActivity.this, "onFailure: " + displayName + " " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }
            }
        });
    }
}