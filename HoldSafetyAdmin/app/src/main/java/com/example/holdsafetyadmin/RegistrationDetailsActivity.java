package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class RegistrationDetailsActivity extends AppCompatActivity {
    TextView userID, txtLastName, txtFirstName, txtMiddleName, txtBirthDate, txtSex, txtIdPic;
    ImageView imgIdPic;
    Button btnValidate, btnReject;

    String id, lastName, firstName, middleName, birthDate, sex, idPic; // USER DATA FOR DISPLAY
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

        userID = findViewById(R.id.lblUserID);
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtMiddleName = findViewById(R.id.txtMiddleName);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtSex = findViewById(R.id.txtSex);
        txtIdPic = findViewById(R.id.txtIdPic);
        imgIdPic = findViewById(R.id.imgIdPic);
        btnValidate = findViewById(R.id.btnValidate);
        btnReject = findViewById(R.id.btnReject);

        getData();

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectUser();
            }
        });
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
                                txtIdPic.setText(idPic); //THIS HAS TO BE HERE COZ FUCK ASYNC METHODS

                                //SHOW THE ID PIC IN IMAGEVIEW
                                Glide.with(RegistrationDetailsActivity.this)
                                        .load(downloadUri)
                                        .into(imgIdPic);
                                
                                Log.d("imgURI", "Image download link: " + downloadUri);
                            })
                            .addOnFailureListener(e -> Log.d("imgURI", "No download link fetched"));

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
                    Toast.makeText(getApplicationContext(), "No User Data from DB", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    //SET DATA IN UI
    private  void setData(){
        userID.setText("User ID: " + id);
        txtLastName.setText(lastName);
        txtFirstName.setText(firstName);
        txtMiddleName.setText(middleName);
        txtBirthDate.setText(birthDate);
        txtSex.setText(sex);
        txtIdPic.setText(idPic);
    }

    public void validateUser(){
        Map<String, Object> docUsers = new HashMap<>();
//        docUsers.put("ID", id);
//        docUsers.put("LastName", lastName);
//        docUsers.put("FirstName", firstName);
//        docUsers.put("MiddleName", middleName);
//        docUsers.put("Sex", sex);
//        docUsers.put("BirthDate", birthDate);
//
//        docUsers.put("MobileNumber", mobileNo);
//        docUsers.put("Email", email);
//        docUsers.put("profileComplete", true);
        docUsers.put("isVerified", true);

//        db.collection("users").document(id).set(docUsers)
        db.collection("users").document(id).update(docUsers)
                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "User " + firstName + " " + lastName + " has been validated.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "User NOT validated.", Toast.LENGTH_SHORT).show());

        //TODO: Remove user from list of unverified
        finish();
    }

    public void rejectUser(){
        Toast.makeText(getApplicationContext(), "Reject User", Toast.LENGTH_SHORT).show();
        Intent rejectReason = new Intent (getApplicationContext(), RejectUserActivity.class);
        rejectReason.putExtra("userID", id);
        startActivity(rejectReason);
    }
}