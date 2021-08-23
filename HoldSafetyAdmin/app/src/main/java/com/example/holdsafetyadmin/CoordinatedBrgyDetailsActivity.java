package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinatedBrgyDetailsActivity extends AppCompatActivity {
    TextView textViewBrgyName, textViewBrgyCity, textViewBrgyMobileNum;
    String brgyName, brgyCity, brgyMobileNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated_brgy_details);

        textViewBrgyName = findViewById(R.id.txtBrgyName);
        textViewBrgyCity = findViewById(R.id.txtBrgyCity);
        textViewBrgyMobileNum = findViewById(R.id.txtBrgyMobileNum);

        getData();
        setData();
    }


    public void getData(){
        if(getIntent().hasExtra("brgyName") && getIntent().hasExtra("brgyCity") && getIntent().hasExtra("brgyMobileNum")){
            brgyName = getIntent().getStringExtra("brgyName");
            brgyCity = getIntent().getStringExtra("brgyCity");
            brgyMobileNum = getIntent().getStringExtra("brgyMobileNum");
        }

        else{
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    public void setData(){
        textViewBrgyName.setText("Barangay: " + brgyName);
        textViewBrgyCity.setText(brgyCity);
        textViewBrgyMobileNum.setText(brgyMobileNum);
    }
}