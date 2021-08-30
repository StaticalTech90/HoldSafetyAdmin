package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ViewReportsActivity extends AppCompatActivity {
    String id[], username[], location[];
    RecyclerView recyclerViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        recyclerViewReports = findViewById(R.id.recyclerviewReports);

        id = getResources().getStringArray(R.array.reportID);
        username = getResources().getStringArray(R.array.reportUsername);
        location = getResources().getStringArray(R.array.reportLocation);

        ReportsAdapter reportsAdapter = new ReportsAdapter(this, id, username, location);
        recyclerViewReports.setAdapter(reportsAdapter);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
    }

    public void generateReport(View view){
        //Toast.makeText(getApplicationContext(), "Generate Report", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (getApplicationContext(), GenerateReportActivity.class);
        startActivity(intent);
    }
}