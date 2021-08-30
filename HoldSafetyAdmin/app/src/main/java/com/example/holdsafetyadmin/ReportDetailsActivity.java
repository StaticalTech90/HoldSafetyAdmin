package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ReportDetailsActivity extends AppCompatActivity {
    TextView textViewID, textViewUsername, textViewLocation;
    String reportID, reportUsername, reportLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        textViewID = findViewById(R.id.txtReportID);
        textViewUsername = findViewById(R.id.txtUsername);
        textViewLocation = findViewById(R.id.txtReportLocation);

        getData();
        setData();
    }

    private void getData(){
        if(getIntent().hasExtra("reportID") && getIntent().hasExtra("reportUsername") && getIntent().hasExtra("reportLocation")){
            reportID = getIntent().getStringExtra("reportID");
            reportUsername = getIntent().getStringExtra("reportUsername");
            reportLocation = getIntent().getStringExtra("reportLocation");
        }

        else{
            Toast.makeText(getApplicationContext(), "No Intent Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(){
        textViewID.setText("Report ID: " + reportID);
        textViewUsername.setText(reportUsername);
        textViewLocation.setText(reportLocation);
    }
}