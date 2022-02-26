package com.example.holdsafetyadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCoordinatedBrgyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText etBarangay, etCity, etMobileNumber, etEmail, etLatitude, etLongitude;
    Button btnAddBrgy;

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
        btnAddBrgy = findViewById(R.id.btnAddBrgy);

        btnAddBrgy.setOnClickListener(view -> saveBrgy());
    }

    public void saveBrgy(){
        Toast.makeText(getApplicationContext(), "Saved New Coordinated Barangay", Toast.LENGTH_LONG).show();

        Map<String, Object> docBrgys = new HashMap<>();

        String barangay = etBarangay.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String latitude = etLatitude.getText().toString().trim();
        String longitude = etLongitude.getText().toString().trim();

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String mobileNumberRegex = "^(09|\\+639)\\d{9}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern mobileNumberPattern = Pattern.compile(mobileNumberRegex);
        Matcher emailMatcher = emailPattern.matcher(email);
        Matcher mobileNumberMatcher = mobileNumberPattern.matcher(mobileNumber);

        docBrgys.put("Barangay", barangay);
        docBrgys.put("City", city);
        docBrgys.put("MobileNumber", mobileNumber);
        docBrgys.put("Email", email);
        docBrgys.put("Latitude", latitude);
        docBrgys.put("Longitude", longitude);


        if(TextUtils.isEmpty(barangay)){
            etBarangay.setError("Please enter barangay");
        } else if(TextUtils.isEmpty(city)){
            etCity.setError("Please enter city");
        } else if(TextUtils.isEmpty(mobileNumber)){
            etMobileNumber.setError("Please enter mobile number");
        } else if (!mobileNumberMatcher.matches()) {
            etMobileNumber.setError("Please enter a valid mobile number");
        } else if(TextUtils.isEmpty(email)){
            etEmail.setError("Please enter email");
        } else if (!emailMatcher.matches()) {
            etEmail.setError("Please enter a valid email");
        } else if(TextUtils.isEmpty(latitude)){
            etLatitude.setError("Please enter latitude");
        } else if(TextUtils.isEmpty(longitude)){
            etLongitude.setError("Please enter longitude");
        } else {
            db.collection("barangay").add(docBrgys).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Successfully Added Barangay",
                            Toast.LENGTH_SHORT).show();

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);

                } else {
                    Toast.makeText(getApplicationContext(),
                            Objects.requireNonNull(task.getException()).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}