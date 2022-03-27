package com.example.holdsafetyadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegistrationDetailsActivity extends AppCompatActivity {
    TextView userID, txtLastName, txtFirstName, txtMiddleName, txtBirthDate, txtSex, txtIdPic;
    ImageView btnBack, imgIdPic;
    Button btnValidate, btnReject;

    FirebaseAuth mAuth;
    FirebaseUser user;
    LogHelper logHelper;

    String userId, id, lastName, firstName, middleName, birthDate, sex, idPic; // USER DATA FOR DISPLAY
    String mobileNo, email; // USER DATA FOR PROFILE UPDATE

    FirebaseFirestore db;
    StorageReference storageReference;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        logHelper =  new LogHelper(RegistrationDetailsActivity.this, mAuth, user,this);

        userID = findViewById(R.id.lblUserID);
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtMiddleName = findViewById(R.id.txtMiddleName);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtSex = findViewById(R.id.txtSex);
        txtIdPic = findViewById(R.id.txtIdPic);
        imgIdPic = findViewById(R.id.imgIdPic);
        btnBack = findViewById(R.id.backArrow);
        btnValidate = findViewById(R.id.btnValidate);
        btnReject = findViewById(R.id.btnReject);

        btnBack.setOnClickListener(view -> goBack());
        btnValidate.setOnClickListener(v -> validateUser());
        btnReject.setOnClickListener(v -> rejectUser());

        getData();
    }

    //GET USER DATA BASED ON ID
    private void getData(){
        if(getIntent().hasExtra("userID")) {
            id = getIntent().getStringExtra("userID");

            docRef = db.collection("users").document(id);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()) {
                    FirebaseStorage.getInstance().getReference("id").child("/" + id).getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                idPic = downloadUri.toString();
                                txtIdPic.setText(idPic);

                                //SHOW THE ID PIC IN IMAGEVIEW
                                Glide.with(RegistrationDetailsActivity.this)
                                        .load(downloadUri)
                                        .into(imgIdPic);
                                Log.d("imgURI", "Image download link: " + downloadUri);
                            })
                            .addOnFailureListener(e -> Log.d("imgURI", "No download link fetched"));

                    userId = documentSnapshot.getString("ID");
                    lastName = documentSnapshot.getString("LastName");
                    firstName = documentSnapshot.getString("FirstName");
                    middleName = documentSnapshot.getString("MiddleName");
                    birthDate = documentSnapshot.getString("BirthDate");
                    sex = documentSnapshot.getString("Sex");
                    Log.d("VERIFY", lastName + ", " + firstName + " " + middleName + " Born on " + birthDate + " " + sex);

                    //FOR PROFILE UPDATE
                    mobileNo = documentSnapshot.getString("MobileNumber");
                    email = documentSnapshot.getString("Email");

                    setData();
                } else {
                    logHelper.saveToFirebase("getData", "NO USER", "No user data from db");
                    Toast.makeText(getApplicationContext(), "No User Data from DB", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    //SET DATA IN UI
    private  void setData(){
        userID.setText("User ID: " + userId);
        txtLastName.setText(lastName);
        txtFirstName.setText(firstName);
        txtMiddleName.setText(middleName);
        txtBirthDate.setText(birthDate);
        txtSex.setText(sex);
        txtIdPic.setText(idPic);
    }

    public void validateUser(){
        Map<String, Object> docUsers = new HashMap<>();
        docUsers.put("isVerified", true);

        db.collection("users").document(id).update(docUsers)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        logHelper.saveToFirebase("validateUser", "USER VALIDATED", "User has been validated");
                        Toast.makeText(getApplicationContext(),
                                "User " + firstName + " " + lastName + " has been validated.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logHelper.saveToFirebase("validateUser", "ERROR", e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), "User NOT validated.", Toast.LENGTH_SHORT).show();
                    }
                });
        finish();
    }

    public void rejectUser(){
        logHelper.saveToFirebase("rejectUser", "ACTIVITY REDIRECT", "Go to RejectUserActivity");
        Intent rejectReason = new Intent (getApplicationContext(), RejectUserActivity.class);
        rejectReason.putExtra("userID", id);
        startActivity(rejectReason);
    }

    private void goBack() {
        finish();
    }
}