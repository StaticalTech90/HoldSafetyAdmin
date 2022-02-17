package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class ReportDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;

    TextView txtReportID, txtUserID, txtUsername, txtLocation, txtDateAndTime, txtBarangay, txtEvidenceLink;
    String reportID, reportUsername, reportLocation, reportBarangay, reportEvidenceLink;
    Timestamp reportDateAndTime;
    String userID, latitude, longitude;

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

        db.collection("reports").document(reportID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        reportUsername = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                        latitude = documentSnapshot.getString("Lat");
                        longitude = documentSnapshot.getString("Lon");
                        reportLocation = getGeoLoc(latitude, longitude);
                        reportDateAndTime = documentSnapshot.getTimestamp("Report Date");
                        reportBarangay = documentSnapshot.getString("Barangay");
                        reportEvidenceLink = documentSnapshot.getString("Evidence");

                        setData();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "No report data fetched", Toast.LENGTH_SHORT).show());

        /*
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
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "No report data fetched", Toast.LENGTH_SHORT).show());*/
    }

    public String getGeoLoc(String latitude, String longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Double doubleLat = Double.parseDouble(latitude.trim());
        Double doubleLong = Double.parseDouble(longitude.trim());
        try {
            List<Address> addresses = geocoder.getFromLocation(doubleLat, doubleLong, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("User Report Address", strReturnedAddress.toString());
            } else {
                Log.w("User Report Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("User Report Address", "Cannot get Address!");
        }
        return strAdd;
        //textViewBrgyAddress.setText(strAdd);
        //Toast.makeText(getApplicationContext(), "Address: " + strAdd, Toast.LENGTH_SHORT).show();

    }

    private void setData() {
        txtReportID.setText("Report ID: " + reportID);
        txtUserID.setText(userID);
        txtUsername.setText(reportUsername);
        txtDateAndTime.setText(reportDateAndTime.toDate().toString());
        txtLocation.setText(reportLocation);
        txtBarangay.setText(reportBarangay);
        txtEvidenceLink.setText(reportEvidenceLink);
    }
}