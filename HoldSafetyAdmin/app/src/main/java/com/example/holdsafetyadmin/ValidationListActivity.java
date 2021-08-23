package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ValidationListActivity extends AppCompatActivity {
    RecyclerView validationListRecyclerView;
    String userID[], userFullName[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_list);

        userID = getResources().getStringArray(R.array.userID);
        userFullName = getResources().getStringArray(R.array.userFullName);

        validationListRecyclerView = findViewById(R.id.recyclerviewValidationList);

        ValidationAdapter validationAdapter = new ValidationAdapter(this, userID, userFullName);
        validationListRecyclerView.setAdapter(validationAdapter);
        validationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}