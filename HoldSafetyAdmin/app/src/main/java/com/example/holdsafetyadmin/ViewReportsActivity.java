package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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

    SearchView searchReport;
    LinearLayout displayReportView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        db = FirebaseFirestore.getInstance();

        displayReportView = findViewById(R.id.linearReportList);
        searchReport = findViewById(R.id.reportSearch);

        displayReports();

        //USER PRESSES ENTER AFTER/WHILE TYPING IN SEARCH
        searchReport.setOnKeyListener((v, keyCode, event) -> {
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
                    String userID = reportSnap.getId();

                    reportDB.document(userID).collection("reportDetails").get()
                            .addOnCompleteListener(taskDetails -> {
                                if(taskDetails.isSuccessful()) { //ALL REPORTS FETCHED
                                    for(QueryDocumentSnapshot detailsSnap : taskDetails.getResult()) {
                                        View reportListView = getLayoutInflater().inflate(R.layout.reports_row, null, false);

                                        String reportID = detailsSnap.getId();
                                        String displayName = detailsSnap.getString("LastName") + ", " + detailsSnap.getString("FirstName");
                                        String barangay = detailsSnap.getString("Barangay");
                                        String location = detailsSnap.getString("Lat") + ", " + detailsSnap.getString("Lon");

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

                                    searchReports();
                                }
                            });
               }
           }
        });
    }

    public void searchReports() {
        //TODO: Search function for reports
        searchReport.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for(int i=0; i < displayReportView.getChildCount(); i++){
                    View reportView = displayReportView.getChildAt(i);

                    //DECLARE TEXTS AND BUTTONS
                    TextView txtReportID = reportView.findViewById(R.id.txtReportID);
                    TextView txtReportUser = reportView.findViewById(R.id.txtReportUsername);
                    TextView txtReportLoc = reportView.findViewById(R.id.txtReportLocation);

                    //CHECK IF INPUT IS PRESENT IN EVERY TEXT VIEWS
                    String id = txtReportID.getText().toString().toLowerCase();
                    String user = txtReportUser.getText().toString().toLowerCase();
                    String loc = txtReportLoc.getText().toString().toLowerCase();

                    newText = newText.toLowerCase();

                    //CHECK IF INPUT IS PRESENT IN EVERY TET VIEWS
                    if(id.contains(newText)|| user.contains(newText) || loc.contains(newText)){
                        //CONTAINS
                        reportView.setVisibility(View.VISIBLE);

                    } else{
                        //HIDE THE VIEW IF SEARCH DOESNT MATCH ANY DATA ON DB
                        reportView.setVisibility(View.GONE);
                    }

                }
                return false;
            }
        });
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