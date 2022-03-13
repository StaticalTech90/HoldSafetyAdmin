package com.example.holdsafetyadmin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCoordinatedBrgyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LogHelper logHelper;

    ImageView btnBack;
    private EditText etBarangay, etCity, etMobileNumber, etEmail, etLatitude, etLongitude;
    Button btnAddBrgy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinated_brgy);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        logHelper = new LogHelper(AddCoordinatedBrgyActivity.this, mAuth, user, this);

        etBarangay = findViewById(R.id.txtBarangayName);
        etCity = findViewById(R.id.txtBrgyCity);
        etMobileNumber = findViewById(R.id.txtBrgyMobileNumber);
        etEmail = findViewById(R.id.txtBrgyEmail);
        etLatitude = findViewById(R.id.txtBrgyLatitude);
        etLongitude = findViewById(R.id.txtBrgyLongitude);
        btnBack = findViewById(R.id.backArrow);
        btnAddBrgy = findViewById(R.id.btnAddBrgy);

        btnBack.setOnClickListener(view -> goBack());
        btnAddBrgy.setOnClickListener(view -> saveBrgy());
    }

    public void saveBrgy(){
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

        if(TextUtils.isEmpty(barangay)) {
            etBarangay.setError("Please enter barangay");
        } else if(TextUtils.isEmpty(city)) {
            etCity.setError("Please enter city");
        } else if(TextUtils.isEmpty(mobileNumber)) {
            etMobileNumber.setError("Please enter mobile number");
        } else if (!mobileNumberMatcher.matches()) {
            etMobileNumber.setError("Please enter a valid mobile number");
        } else if(TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter email");
        } else if (!emailMatcher.matches()) {
            etEmail.setError("Please enter a valid email");
        } else if(TextUtils.isEmpty(latitude)) {
            etLatitude.setError("Please enter latitude");
        } else if(TextUtils.isEmpty(longitude)) {
            etLongitude.setError("Please enter longitude");
        } else {
            if(haveNetworkConnection()){
                db.collection("barangay").add(docBrgys).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        logHelper.saveToFirebase("saveBrgy", "SUCCESS", "Barangay added successfully");
                        Toast.makeText(getApplicationContext(),
                                "Successfully Added Barangay",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        logHelper.saveToFirebase("saveBrgy", "ERROR", task.getException().getLocalizedMessage());
                        Toast.makeText(getApplicationContext(),
                                Objects.requireNonNull(task.getException()).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                logHelper.saveToFirebase("saveBrgy", "ERROR", "Unstable network connection");
                Toast.makeText(getApplicationContext(),
                        "Your internet is not connected or unstable. Adding Barangay Failed",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void goBack() {
        finish();
    }
}