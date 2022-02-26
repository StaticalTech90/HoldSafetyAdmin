package com.example.holdsafetyadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatedBrgyDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    DocumentReference docRef;

    TextView textViewBrgyName, textViewBrgyCity, textViewBrgyID, btnRemoveBrgy,textViewBrgyAddress;
    EditText textViewBrgyMobileNum, textViewBrgyEmail, textViewBrgyLatitude, textViewBrgyLongitude;
    String brgyName, brgyCity, brgyMobileNum, brgyEmail, brgyLat, brgyLong, brgyID;
    Button btnBrgyUpdate;
    Boolean isNumberChanged, isEmailChanged, isLatChanged, isLongChanged;

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

        isNumberChanged = false;
        isEmailChanged = false;
        isLatChanged = false;
        isLongChanged = false;

        getData();

        try {
            getGeoLoc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setData();

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("barangay").document(brgyID);
        
        checkUpdates();

        btnBrgyUpdate.setOnClickListener(view -> updateBarangay());
        btnRemoveBrgy.setOnClickListener(view -> removeBarangay());

    }

    private void checkUpdates() {
        //check if there are changes for each edit texts
        textViewBrgyMobileNum.addTextChangedListener(new TextWatcher() {
            String newMobileNum;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newMobileNum = textViewBrgyMobileNum.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(brgyMobileNum.equals(newMobileNum)){
                    isNumberChanged = false;
                    Toast.makeText(CoordinatedBrgyDetailsActivity.this, "No Changes", Toast.LENGTH_SHORT).show();
                } else{
                    isNumberChanged = true;
                    //Toast.makeText(CoordinatedBrgyDetailsActivity.this, "New Barangay Number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewBrgyEmail.addTextChangedListener(new TextWatcher() {
            String newEmail;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newEmail = textViewBrgyEmail.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(brgyEmail.equals(newEmail)){
                    isEmailChanged = false;
                    Toast.makeText(CoordinatedBrgyDetailsActivity.this, "No Changes", Toast.LENGTH_SHORT).show();
                } else{
                    isEmailChanged = true;
                    //Toast.makeText(CoordinatedBrgyDetailsActivity.this, "New Barangay Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewBrgyLatitude.addTextChangedListener(new TextWatcher() {
            String newLat;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newLat = textViewBrgyLatitude.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(brgyLat.equals(newLat)){
                    isLatChanged = false;
                    Toast.makeText(CoordinatedBrgyDetailsActivity.this, "No Changes", Toast.LENGTH_SHORT).show();
                } else{
                    isLatChanged = true;
                    //Toast.makeText(CoordinatedBrgyDetailsActivity.this, "New Barangay Lat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewBrgyLongitude.addTextChangedListener(new TextWatcher() {
            String newLong;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newLong = textViewBrgyLongitude.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(brgyLong.equals(newLong)){
                    isLongChanged = false;
                    Toast.makeText(CoordinatedBrgyDetailsActivity.this, "No Changes", Toast.LENGTH_SHORT).show();
                } else{
                    isLongChanged = true;
                    //Toast.makeText(CoordinatedBrgyDetailsActivity.this, "New Barangay Longitude", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String mobileNumberRegex = "^(09|\\+639)\\d{9}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobileNumberPattern = Pattern.compile(mobileNumberRegex);
        Matcher emailMatcher = emailPattern.matcher(changedEmail);
        Matcher mobileNumberMatcher = mobileNumberPattern.matcher(changedMobileNumber);

        if(isNumberChanged || isLatChanged || isLongChanged || isEmailChanged){
            if(TextUtils.isEmpty(changedMobileNumber)){
                textViewBrgyMobileNum.setError("Please enter mobile number");
            } else if (!mobileNumberMatcher.matches()) {
                textViewBrgyMobileNum.setError("Please enter a valid mobile number");
            } else if(TextUtils.isEmpty(changedEmail)){
                textViewBrgyEmail.setError("Please enter email");
            } else if (!emailMatcher.matches()) {
                textViewBrgyEmail.setError("Please enter a valid email");
            } else if (TextUtils.isEmpty(changedLat)){
                textViewBrgyLatitude.setError("Please enter a latitude");
            } else if (TextUtils.isEmpty(changedLong)){
                textViewBrgyLongitude.setError("Please enter a longitude");
            } else {
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
        } else {
            Toast.makeText(CoordinatedBrgyDetailsActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
        }


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