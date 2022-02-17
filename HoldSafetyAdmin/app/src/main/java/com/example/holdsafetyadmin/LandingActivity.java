package com.example.holdsafetyadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {
    Button btnLogout, btnViewReport, btnManageCoordinatedBrgys, btnVerifyUser;

    private static final int WRITE_EXTERNAL_REQ_CODE = 1000;
    private static final int READ_EXTERNAL_REQ_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        btnViewReport = findViewById(R.id.btnViewReport);
        btnManageCoordinatedBrgys = findViewById(R.id.btnManageCoordinatedBrgys);
        btnVerifyUser = findViewById(R.id.btnVerifyUser);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewReport.setOnClickListener(view -> viewReport());
        btnManageCoordinatedBrgys.setOnClickListener(view -> manageCoordinatedBrgys());
        btnVerifyUser.setOnClickListener(view -> verifyUser());
        btnLogout.setOnClickListener(view -> adminLogout());
    }

    //checks required permissions
    private void setPermissions(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //DENIED LOCATION PERMISSION
            Log.d("location permission", "Please Grant Location Permission");
            //SHOW PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_REQ_CODE);
        } else if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //DENIED LOCATION PERMISSION
            Log.d("location permission", "Please Grant Location Permission");
            //SHOW PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_REQ_CODE) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //DENIED ONCE
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REQ_CODE);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                setPermissions(); //getcurrent
            }
            else {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                settingsIntent.setData(uri);
                startActivity(settingsIntent);
                Toast.makeText(this, "Please Grant Storage Permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_EXTERNAL_REQ_CODE) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //DENIED ONCE
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_REQ_CODE);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
            } else {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                settingsIntent.setData(uri);
                startActivity(settingsIntent);
                Toast.makeText(this, "Please Grant Access to Storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Asks for permissions on activity start
        setPermissions();
    }

    public void viewReport(){
        //Toast.makeText(getApplicationContext(), "View Report", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (getApplicationContext(), ViewReportsActivity.class);
        startActivity(intent);
    }

    public void manageCoordinatedBrgys(){
        //Toast.makeText(getApplicationContext(), "Coordinated Barangays", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (getApplicationContext(), CoordinatedBrgysActivity.class);
        startActivity(intent);
    }

    public void verifyUser(){
        //Toast.makeText(getApplicationContext(), "Verify Registration", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (getApplicationContext(), VerificationListActivity.class);
        startActivity(intent);
    }

    public void adminLogout(){
        Toast.makeText(getApplicationContext(), "Logout method", Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(LandingActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LandingActivity.this, LoginActivity.class));
        finish();
    }
}
