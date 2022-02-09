package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ReportDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;

    TextView txtReportID, txtUserID, txtUsername, txtLocation, txtDateAndTime, txtBarangay, txtEvidenceLink;
    String reportID, reportUsername, reportLocation, reportDateAndTime, reportBarangay, reportEvidenceLink;
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
        txtEvidenceLink = findViewById(R.id.txtEvidenceLink);

        getData();
    }

    private void getData() {
        if(getIntent().hasExtra("userID")) { userID = getIntent().getStringExtra("userID"); }
        if(getIntent().hasExtra("reportID")) { reportID = getIntent().getStringExtra("reportID"); }

        db.collection("reportUser").document(userID).collection("reportDetails").document(reportID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        reportUsername = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                        reportLocation = documentSnapshot.getString("Lat") + ", " + documentSnapshot.getString("Lon");
                        reportDateAndTime = documentSnapshot.getString("Report Date");
                        reportBarangay = documentSnapshot.getString("Barangay");
                        reportEvidenceLink = documentSnapshot.getString("Evidence");

                        setData();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "No report data fetched", Toast.LENGTH_SHORT).show());
    }

    private void setData() {
        txtReportID.setText("Report ID: " + reportID);
        txtUserID.setText(userID);
        txtUsername.setText(reportUsername);
        txtDateAndTime.setText(reportDateAndTime);
        txtLocation.setText(reportLocation);
        txtBarangay.setText(reportBarangay);
        txtEvidenceLink.setText(reportEvidenceLink);
    }
}