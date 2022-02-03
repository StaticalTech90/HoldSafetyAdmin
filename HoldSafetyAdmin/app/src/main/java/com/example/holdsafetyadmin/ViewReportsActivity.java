package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewReportsActivity extends AppCompatActivity {
    FirebaseFirestore db;

    EditText search;
    LinearLayout displayReportView;

    String reportID, userID;
    String displayName, barangay, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        db = FirebaseFirestore.getInstance();

        displayReportView = findViewById(R.id.linearReportList);
        search = findViewById(R.id.txtSearch);

        displayReports();

        //USER PRESSES ENTER AFTER/WHILE TYPING IN SEARCH
        search.setOnKeyListener((v, keyCode, event) -> {
            boolean enter = false;
            if(keyCode == KeyEvent.KEYCODE_ENTER ){
                searchReports();
                enter = true;
            }
            if (event.getAction()!=KeyEvent.ACTION_DOWN) {
                enter = false;
            }
            return enter;
        });
    }

    public void displayReports() {
        CollectionReference reportDB = db.collection("reportUser");

        //GET REPORT BASED ON USER ID
        reportDB.get().addOnCompleteListener(taskReport -> {
           if(taskReport.isSuccessful()) { //USER ID FOR REPORTS ARE FETCHED
               for(QueryDocumentSnapshot reportSnap : taskReport.getResult()) {
                    userID = reportSnap.getId();

                    reportDB.document(userID).collection("reportDetails").get()
                            .addOnCompleteListener(taskDetails -> {
                                if(taskDetails.isSuccessful()) { //ALL REPORTS FETCHED
                                    for(QueryDocumentSnapshot detailsSnap : taskDetails.getResult()) {
                                        View reportListView = getLayoutInflater().inflate(R.layout.reports_row, null, false);

                                        userID = reportSnap.getId();
                                        reportID = detailsSnap.getId();
                                        displayName = detailsSnap.getString("LastName") + ", " + detailsSnap.getString("FirstName");
                                        barangay = detailsSnap.getString("Barangay");
                                        location = detailsSnap.getString("Lat") + ", " + detailsSnap.getString("Lon");

                                        //ASSIGN TO UI
                                        TextView txtReportID = reportListView.findViewById(R.id.txtReportID);
                                        TextView txtReportUsername = reportListView.findViewById(R.id.txtReportUsername);
                                        TextView txtReportLocation = reportListView.findViewById(R.id.txtReportLocation);

                                        txtReportID.setText(detailsSnap.getId());
                                        txtReportUsername.setText(displayName);
                                        txtReportLocation.setText(location);

                                        //SET ONCLICK PER ROW
                                        reportListView.setOnClickListener(v -> {
                                            Intent selectedReport = new Intent(ViewReportsActivity.this, ReportDetailsActivity.class);
                                            selectedReport.putExtra("userID", userID);
                                            selectedReport.putExtra("reportID", reportID);
                                            startActivity(selectedReport);
                                        });
                                        //ADD TO VIEW
                                        displayReportView.addView(reportListView);
                                    }
                                }
                            });
               }
           }
        });
    }

    public void searchReports() {
        //TODO: Search function for reports
    }

    public void sortReports() {
        //TODO: Sort function for reports
    }

    public void generateReport(View view){
        //Toast.makeText(getApplicationContext(), "Generate Report", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent (getApplicationContext(), GenerateReportActivity.class);
        startActivity(intent);
    }
}