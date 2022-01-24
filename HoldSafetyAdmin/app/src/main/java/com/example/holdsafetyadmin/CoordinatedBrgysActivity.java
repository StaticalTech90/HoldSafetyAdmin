package com.example.holdsafetyadmin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CoordinatedBrgysActivity extends AppCompatActivity {
    String[] name, city, mobileNum;
    LinearLayout linearCoordinatedBrgys;

    ImageView btnAdd;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated_brgys);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        linearCoordinatedBrgys = findViewById(R.id.linearViewCoordinatedBrgys);
        btnAdd = findViewById(R.id.addBarangay);

        name = getResources().getStringArray(R.array.brgyName);
        city = getResources().getStringArray(R.array.brgyCity);
        mobileNum = getResources().getStringArray(R.array.brgyMobileNum);

        CoordinatedBrgyAdapter coordinatedBrgyAdapter = new CoordinatedBrgyAdapter(this, name, city, mobileNum);

        btnAdd.setOnClickListener(this::addBrgy);

        getBarangay();
    }

    @SuppressLint("SetTextI18n")
    private void getBarangay() {
        FirebaseFirestore.getInstance()
                .collection("barangay").get()
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, CoordinatedBrgyDetailsActivity.class);
                    Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        //FOR EACH
                        //GET ALL ID
                        for (QueryDocumentSnapshot contactSnap : task.getResult()) {

                            //GET CONTACT DETAILS
                            String barangay = contactSnap.getString("Barangay");
                            String city = contactSnap.getString("City");
                            String email = contactSnap.getString("Email");
                            String latitude = contactSnap.getString("Latitude");
                            String longitude = contactSnap.getString("Longitude");
                            String mobileNumber = contactSnap.getString("MobileNumber");
                            String documentId = contactSnap.getId();

                            //DECLARE THE LAYOUT - CARDVIEW
                            View detailsView = getLayoutInflater().inflate(R.layout.coordinated_brgy_row, null, false);

                            //DECLARE TEXTS AND BUTTONS
                            TextView txtBarangayName = detailsView.findViewById(R.id.txtBrgyName);
                            TextView txtCityName = detailsView.findViewById(R.id.txtBrgyCity);
                            TextView txtMobileNumber = detailsView.findViewById(R.id.txtBrgyMobileNum);
                            TextView details = detailsView.findViewById(R.id.btnDetails);

                            //SET TEXTS
                            txtBarangayName.setText(barangay);
                            txtCityName.setText(city);
                            txtMobileNumber.setText(mobileNumber);

                            //SET DETAILS ONCLICK LISTENER
                            details.setOnClickListener(view -> {

                                Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

                                //PREPARE DATA TO PASS
                                intent.putExtra("barangay", barangay);
                                intent.putExtra("city", city);
                                intent.putExtra("email", email);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                intent.putExtra("mobileNumber", mobileNumber);
                                intent.putExtra("documentId", documentId);

                                startActivity(intent);
//                                //SHOW DETAILS DIALOG
//                                Dialog dialog = new Dialog(SelectContactActivity.this);
//                                View dialogView = getLayoutInflater().inflate(R.layout.activity_update_contact, null, false);
//                                dialog.setContentView(dialogView);
//                                dialog.setCancelable(false);
//
//                                //DECLARE DIALOG VIEWS
//                                TextView txtEmail = dialogView.findViewById(R.id.lblEmail);
//                                TextView txtFirstName = dialogView.findViewById(R.id.lblFirstName);
//                                TextView txtLastName = dialogView.findViewById(R.id.lblLastName);
//                                TextView txtMobileNumber = dialogView.findViewById(R.id.lblMobileNumber);
//                                TextView txtRelation = dialogView.findViewById(R.id.lblRelation);
//
//                                Button btnApplyUpdate = dialogView.findViewById(R.id.btnUpdateContact);
//
//
//                                //SET TEXT AND ON CLICK LISTENER
//                                txtEmail.setText(email);
//                                txtFirstName.setText(firstName);
//                                txtLastName.setText(lastName);
//                                txtMobileNumber.setText(mobileNumber);
//                                txtRelation.setText(relation);
//
//                                btnApplyUpdate.setOnClickListener(view1 -> {
//                                    //GET DATA I GUESS
//
//                                    //DISMISS DIALOG
//                                    dialog.dismiss();
//
//                                    startActivity(new Intent(this, UpdateContactActivity.class));
//                                });
//
//                                dialog.show();
                            });

                            //ADD TO LINEAR
                            linearCoordinatedBrgys.addView(detailsView);
                        }

                    } else {
                        //NO DATA AVAILABLE
                        Toast.makeText(this, "No Contacts Available", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void addBrgy(View view){
        startActivity(new Intent (getApplicationContext(), AddCoordinatedBrgyActivity.class));
    }
}