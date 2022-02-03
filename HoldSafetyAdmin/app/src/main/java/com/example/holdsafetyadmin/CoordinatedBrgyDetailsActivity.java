package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CoordinatedBrgyDetailsActivity extends AppCompatActivity {
    TextView textViewBrgyName, textViewBrgyCity, textViewBrgyMobileNum, textViewBrgyEmail, textViewBrgyLatitude, textViewBrgyLongitude, textViewBrgyID;
    String brgyName, brgyCity, brgyMobileNum, brgyEmail, brgyLat, brgyLong, brgyID;

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
        textViewBrgyID = findViewById(R.id.txtBrgyID);

        getData();
        //getGeoLoc();
        setData();
    }


    public void getData(){
        if(getIntent().hasExtra("barangay") && getIntent().hasExtra("city") && getIntent().hasExtra("email")
                && getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude") && getIntent().hasExtra("mobileNumber")
                && getIntent().hasExtra("barangayID")){
            brgyName = getIntent().getStringExtra("barangay");
            brgyCity = getIntent().getStringExtra("city");
            brgyMobileNum = getIntent().getStringExtra("mobileNumber");
            brgyEmail = getIntent().getStringExtra("email");
            brgyLat = getIntent().getStringExtra("latitude");
            brgyLong = getIntent().getStringExtra("longitude");
            brgyID = getIntent().getStringExtra("barangayID");
        }

        else{
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void getGeoLoc(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    Double.parseDouble(brgyLat), Double.parseDouble(brgyLong), 2);
            String address = addresses.get(1).getAddressLine(0);

            Toast.makeText(this, "GeoLoc: " + address, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

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