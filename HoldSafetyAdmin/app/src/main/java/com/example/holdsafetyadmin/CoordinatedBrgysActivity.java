package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CoordinatedBrgysActivity extends AppCompatActivity {
    String name[], city[], mobileNum[];
    RecyclerView recyclerViewCoordinatedBrgys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated_brgys);

        recyclerViewCoordinatedBrgys = findViewById(R.id.recyclerviewCoordinatedBrgys);

        name = getResources().getStringArray(R.array.brgyName);
        city = getResources().getStringArray(R.array.brgyCity);
        mobileNum = getResources().getStringArray(R.array.brgyMobileNum);

        CoordinatedBrgyAdapter coordinatedBrgyAdapter = new CoordinatedBrgyAdapter(this, name, city, mobileNum);
        recyclerViewCoordinatedBrgys.setAdapter(coordinatedBrgyAdapter);
        recyclerViewCoordinatedBrgys.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addBrgy(View view){
        //Toast.makeText(getApplicationContext(), "Add Coordinated Barangay", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (getApplicationContext(), AddCoordinatedBrgyActivity.class);
        startActivity(intent);
    }
}