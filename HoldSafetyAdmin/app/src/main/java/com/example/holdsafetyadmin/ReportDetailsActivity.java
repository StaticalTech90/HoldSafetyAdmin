package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ReportDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;

    TextView txtReportID, txtUserID, txtUsername, txtLocation, txtDateAndTime, txtBarangay;
    String reportID, reportUsername, reportLocation, reportDateAndTime, reportBarangay;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        db = FirebaseFirestore.getInstance();

        txtReportID = findViewById(R.id.txtReportID);
        txtUserID = findViewById(R.id.txtUserID);
        txtUsername = findViewById(R.id.txtUsername);
        txtDateAndTime = findViewById(R.id.txtDateAndTime);
        txtLocation = findViewById(R.id.txtReportLocation);
        txtBarangay = findViewById(R.id.txtBarangay);

        getData();
    }

    private void getData() {
        if(getIntent().hasExtra("userID")) {
            userID = getIntent().getStringExtra("userID");
        }
        if(getIntent().hasExtra("reportID")) {
            reportID = getIntent().getStringExtra("reportID");
        }

        db.collection("reportUser").document(userID).collection("reportDetails").document(reportID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            reportUsername = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                            reportLocation = documentSnapshot.getString("Lat") + ", " + documentSnapshot.getString("Lon");
                            reportDateAndTime = documentSnapshot.getString("Report Date");
                            reportBarangay = documentSnapshot.getString("Barangay");

                            setData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No report data fetched", Toast.LENGTH_SHORT).show();
            }
        });

//        if(getIntent().hasExtra("reportID") && getIntent().hasExtra("reportUsername") && getIntent().hasExtra("reportLocation")){
//            reportID = getIntent().getStringExtra("reportID");
//            reportUsername = getIntent().getStringExtra("reportUsername");
//            reportLocation = getIntent().getStringExtra("reportLocation");
//        }
//
//        else{
//            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setData() {
        txtReportID.setText("Report ID: " + reportID);
        txtUserID.setText(userID);
        txtUsername.setText(reportUsername);
        txtDateAndTime.setText(reportDateAndTime);
        txtLocation.setText(reportLocation);
        txtBarangay.setText(reportBarangay);
    }
}