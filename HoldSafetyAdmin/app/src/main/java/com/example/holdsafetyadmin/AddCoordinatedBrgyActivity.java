package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddCoordinatedBrgyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText etBarangay;
    private EditText etCity;
    private EditText etMobileNumber;
    private EditText etEmail;
    private EditText etLatitude;
    private EditText etLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinated_brgy);

        mAuth = FirebaseAuth.getInstance();

        etBarangay = findViewById(R.id.txtBarangayName);
        etCity = findViewById(R.id.txtBrgyCity);
        etMobileNumber = findViewById(R.id.txtBrgyMobileNumber);
        etEmail = findViewById(R.id.txtBrgyEmail);
        etLatitude = findViewById(R.id.txtBrgyLatitude);
        etLongitude = findViewById(R.id.txtBrgyLongitude);

    }

    public void saveBrgy(View view){
        Toast.makeText(getApplicationContext(), "Saved New Coordinated Barangay", Toast.LENGTH_LONG).show();

        Map<String, Object> docBrgys = new HashMap<>();

        String barangay = etBarangay.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String latitude = etLatitude.getText().toString().trim();
        String longitude = etLongitude.getText().toString().trim();

        docBrgys.put("Barangay", barangay);
        docBrgys.put("City", city);
        docBrgys.put("MobileNumber", mobileNumber);
        docBrgys.put("Email", email);
        docBrgys.put("Latitude", latitude);
        docBrgys.put("Longitude", longitude);


        if(TextUtils.isEmpty(etBarangay.getText())){
            etBarangay.setHint("please enter barangay");
            etBarangay.setError("please enter barangay");
        } else if(TextUtils.isEmpty(etCity.getText())){
            etCity.setHint("please enter city");
            etCity.setError("please enter city");
        } else if(TextUtils.isEmpty(etMobileNumber.getText())){
            etMobileNumber.setHint("please enter mobile number");
            etMobileNumber.setError("please enter mobile number");
        } else if(TextUtils.isEmpty(etLatitude.getText())){
            etLatitude.setHint("please enter latitude");
            etLatitude.setError("please enter latitude");
        } else if(TextUtils.isEmpty(etLongitude.getText())){
            etLongitude.setHint("please enter longitude");
            etLongitude.setError("please enter longitude");
        } else {
            db.collection("barangay").add(docBrgys).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Successfully Added Barangay",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            Objects.requireNonNull(task.getException()).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}