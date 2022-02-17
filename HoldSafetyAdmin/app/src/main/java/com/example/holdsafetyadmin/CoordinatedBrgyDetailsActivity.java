package com.example.holdsafetyadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CoordinatedBrgyDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    DocumentReference docRef;

    TextView textViewBrgyName, textViewBrgyCity, textViewBrgyID, btnRemoveBrgy,textViewBrgyAddress;
    EditText textViewBrgyMobileNum, textViewBrgyEmail, textViewBrgyLatitude, textViewBrgyLongitude;
    String brgyName, brgyCity, brgyMobileNum, brgyEmail, brgyLat, brgyLong, brgyID;
    Button btnBrgyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated_brgy_details);

        textViewBrgyName = findViewById(R.id.txtBrgyName);
        textViewBrgyCity = findViewById(R.id.txtBrgyCity);
        textViewBrgyMobileNum = findViewById(R.id.txtBrgyMobileNum);
        textViewBrgyEmail = findViewById(R.id.txtBrgyEmail);
        textViewBrgyLatitude = findViewById(R.id.txtBrgyLatitude);
        textViewBrgyLongitude = findViewById(R.id.txtBrgyLongitude);
        textViewBrgyAddress = findViewById(R.id.txtBrgyAddress);
        textViewBrgyID = findViewById(R.id.txtBrgyID);
        
        btnBrgyUpdate = findViewById(R.id.btnSaveChanges);
        btnRemoveBrgy = findViewById(R.id.btnRemoveBrgy);

        getData();
        try {
            getGeoLoc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setData();

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("barangay").document(brgyID);

        btnBrgyUpdate.setOnClickListener(view -> updateBarangay());
        btnRemoveBrgy.setOnClickListener(view -> removeBarangay());
    }

    private void removeBarangay() {
        AlertDialog.Builder dialogRemoveAccount;
        dialogRemoveAccount = new AlertDialog.Builder(CoordinatedBrgyDetailsActivity.this);
        dialogRemoveAccount.setTitle("Confirm Deletion");
        dialogRemoveAccount.setMessage("Are you sure you want to delete this barangay?");

        dialogRemoveAccount.setPositiveButton("Delete", (dialog, which) -> {
            docRef.delete();
            //Reload Activity After deleting contact
            Intent intent = new Intent(this, CoordinatedBrgyDetailsActivity.class);
            finish();
            startActivity(intent);
        });

        dialogRemoveAccount.setNegativeButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = dialogRemoveAccount.create();
        alertDialog.show();
    }

    private void updateBarangay() {
        String changedMobileNumber = textViewBrgyMobileNum.getText().toString().trim();
        String changedEmail = textViewBrgyEmail.getText().toString().trim();
        String changedLat = textViewBrgyLatitude.getText().toString().trim();
        String changedLong = textViewBrgyLongitude.getText().toString().trim();

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                docRef.update("MobileNumber", changedMobileNumber);
                docRef.update("Email", changedEmail);
                docRef.update("Latitude", changedLat);
                docRef.update("Longitude", changedLong);

                Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, CoordinatedBrgyDetailsActivity.class));
                finish();
            }
            else {
                Toast.makeText(CoordinatedBrgyDetailsActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(){
        if(getIntent().hasExtra("barangay") && getIntent().hasExtra("city") && getIntent().hasExtra("latitude")
                && getIntent().hasExtra("longitude") && getIntent().hasExtra("mobileNumber") && getIntent().hasExtra("barangayID")){
            brgyName = getIntent().getStringExtra("barangay");
            brgyCity = getIntent().getStringExtra("city");
            brgyMobileNum = getIntent().getStringExtra("mobileNumber");
            brgyLat = getIntent().getStringExtra("latitude");
            brgyLong = getIntent().getStringExtra("longitude");
            brgyID = getIntent().getStringExtra("barangayID");

            if(getIntent().hasExtra("email")){
                brgyEmail = getIntent().getStringExtra("email");
            }
        } else{
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void getGeoLoc() throws IOException {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        double doubleLat = Double.parseDouble(brgyLat.trim());
        double doubleLong = Double.parseDouble(brgyLong.trim());
        try {
            List<Address> addresses = geocoder.getFromLocation(doubleLat, doubleLong, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Barangay Address", strReturnedAddress.toString());
            } else {
                Log.w("Barangay Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Barangay Address", "Cannot get Address!");
        }
        textViewBrgyAddress.setText(strAdd);
        //Toast.makeText(getApplicationContext(), "Address: " + strAdd, Toast.LENGTH_SHORT).show();
    }

    public void setData(){
        textViewBrgyName.setText("Barangay: " + brgyName);
        textViewBrgyCity.setText(brgyCity);
        textViewBrgyMobileNum.setText(brgyMobileNum);
        textViewBrgyEmail.setText(brgyEmail);
        textViewBrgyLatitude.setText(brgyLat);
        textViewBrgyLongitude.setText(brgyLong);
        textViewBrgyID.setText(brgyID);
    }

}