package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddCoordinatedBrgyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinated_brgy);
    }

    public void saveBrgy(View view){
        Toast.makeText(getApplicationContext(), "Saved New Coordinated Barangay", Toast.LENGTH_LONG).show();
    }
}