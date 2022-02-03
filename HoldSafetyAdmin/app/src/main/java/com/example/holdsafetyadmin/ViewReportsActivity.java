package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewReportsActivity extends AppCompatActivity {
    FirebaseFirestore db;

    String id[], username[], location[];
    EditText search;
    LinearLayout displayReportView;
    RecyclerView recyclerViewReports;

    String reportID, userID, firstName, lastName;
    String barangay, date, lon, lat;
    DocumentReference docUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        db = FirebaseFirestore.getInstance();

        displayReportView = findViewById(R.id.linearReportList);
//        recyclerViewReports = findViewById(R.id.recyclerviewReports);

//        id = getResources().getStringArray(R.array.reportID);
//        username = getResources().getStringArray(R.array.reportUsername);
//        location = getResources().getStringArray(R.array.reportLocation);
        search = findViewById(R.id.txtSearch);

//        ReportsAdapter reportsAdapter = new ReportsAdapter(this, id, username, location);
//        recyclerViewReports.setAdapter(reportsAdapter);
//        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));

        displayReports();

        //USER PRESSES ENTER AFTER/WHILE TYPING IN SEARCH
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean enter = false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    searchReports();
                    enter = true;
                }
                if (event.getAction()!=KeyEvent.ACTION_DOWN) {
                    enter = false;
                }
                return enter;
            }
        });


    }

    public void displayReports() {
        //GET REPORT BASED ON USER ID
        db.collection("reportUser").get().addOnCompleteListener(taskReport -> {
           if(taskReport.isSuccessful()) { //USER ID FOR REPORTS ARE FETCHED
               for(QueryDocumentSnapshot reportSnap : taskReport.getResult()) {
                    userID = reportSnap.getId();
//                    docUsers = db.collection("users").document(userID);

                    View reportListView = getLayoutInflater().inflate(R.layout.reports_row, null, false);

                    //TODO: Optimize this code
                    db.collection("reportUser").document(userID).collection("reportDetails").get()
                            .addOnCompleteListener(taskDetails -> {
                                if(taskDetails.isSuccessful()) { //ALL REPORTS FETCHED
                                    for(QueryDocumentSnapshot detailsSnap : taskDetails.getResult()) {
                                        reportID = detailsSnap.getId();

                                        //ASSIGN TO UI
                                        TextView txtReportID = reportListView.findViewById(R.id.txtReportID);
                                        TextView txtReportUsername = reportListView.findViewById(R.id.txtReportUsername);
                                        TextView txtReportLocation = reportListView.findViewById(R.id.txtReportLocation);

                                        txtReportID.setText(detailsSnap.getId());
                                        txtReportUsername.setText(detailsSnap.getString("LastName") + ", " + detailsSnap.getString("FirstName"));
                                        txtReportLocation.setText(detailsSnap.getString("Lat") + ", " + detailsSnap.getString("Lon"));
                                    }
                                }
                            });

                    //UTILIZE THIS IF CODE BLOCK ABOVE DOESN'T WORK
//                    //GET USER DATA
//                    docUsers.get().addOnSuccessListener(documentSnapshot -> {
//                        if(documentSnapshot.exists()) {
//                            firstName = documentSnapshot.getString("FirstName");
//                            lastName = documentSnapshot.getString("LastName");
//                        }
//                    });

                   reportListView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent selectedReport = new Intent(ViewReportsActivity.this, ReportDetailsActivity.class);
                           selectedReport.putExtra("userID", userID);
                           selectedReport.putExtra("reportID", reportID);
                           startActivity(selectedReport);
                       }
                   });

                   displayReportView.addView(reportListView);
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