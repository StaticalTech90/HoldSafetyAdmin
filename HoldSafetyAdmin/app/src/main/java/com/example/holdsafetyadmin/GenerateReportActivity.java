package com.example.holdsafetyadmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Header;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GenerateReportActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Spinner spinnerBarangay;
    String selectedBarangay;
    Button btnSendReport;
    EditText etStartDate, etEndDate;

    final Calendar calendar = Calendar.getInstance();

    private static final int STORAGE_READ_REQ_CODE = 1000;
    private static final int STORAGE_WRITE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        //Get data from db and auto-input in the form
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //TODO DECLARE BOTH DATEPICKER
        //TODO DECLARE ONLICKLISTENER
        spinnerBarangay = findViewById(R.id.txtBarangayName);
        etStartDate = findViewById(R.id.txtStartDate);
        etEndDate = findViewById(R.id.txtEndDate);
        btnSendReport = findViewById(R.id.btnSend);

        dropdownBarangay();
        selectStartDate();
        selectEndDate();

        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generateReport(v);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        setPermissions();
    }

    //checks required permissions
    private void setPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //DENIED LOCATION PERMISSION
            Log.d("external storage", "Please Storage Permission");
            //SHOW PERMISSION
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    STORAGE_WRITE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_WRITE_CODE) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //DENIED ONCE
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_WRITE_CODE);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                setPermissions(); //getcurrent
            }
            else {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                settingsIntent.setData(uri);
                startActivity(settingsIntent);
                Toast.makeText(this, "Please Grant Storage Permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String dropdownBarangay(){
        final List<String> barangayList = new ArrayList<>();
        barangayList.add("Barangay* ");

        FirebaseFirestore.getInstance()
                .collection("barangay").get()
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, CoordinatedBrgyDetailsActivity.class);
                    //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        //FOR EACH
                        //GET ALL ID
                        for (QueryDocumentSnapshot contactSnap : task.getResult()) {
                            //GET BARANGAY NAME AND ADD TO ARRAY
                            String barangayName = contactSnap.getString("Barangay");
                            barangayList.add(barangayName);
                        }

                    } else {
                        //NO DATA AVAILABLE
                        Toast.makeText(this, "No Barangays Available", Toast.LENGTH_SHORT).show();
                    }
                });

        ArrayAdapter<String> spinnerBrgyAdapter = new ArrayAdapter<String>(this, R.layout.spinner, barangayList){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position==0){
                    tv.setTextColor(getResources().getColor(R.color.hint_color));
                }

                else{
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerBrgyAdapter.setDropDownViewResource(R.layout.spinner);
        spinnerBarangay.setAdapter(spinnerBrgyAdapter);

        spinnerBarangay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                    selectedBarangay = (String) spinnerBarangay.getSelectedItem().toString().trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return selectedBarangay;
    }

    //update START DATE value
    private void updateStartDate(){
        //matched with line 174
        String myFormat="MM-dd-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void selectStartDate(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateStartDate();
            }
        };

        //show DatePickerDialog using this listener
        etStartDate.setOnClickListener(view -> new DatePickerDialog(
                GenerateReportActivity.this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        );
    }

    //update END DATE value
    private void updateEndDate(){
        //matched with line 174
        String myFormat="MM-dd-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etEndDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void selectEndDate(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                updateEndDate();
            }
        };

        //show DatePickerDialog using this listener
        etEndDate.setOnClickListener(view -> new DatePickerDialog(
                GenerateReportActivity.this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        );
    }

    //TODO PDF REPORT LOGIC HERE
    @SuppressLint("SimpleDateFormat")
    public void generateReport(View view) throws ParseException {
//        Toast.makeText(getApplicationContext(), etStartDate.getText(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), etEndDate.getText(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), selectedBarangay, Toast.LENGTH_SHORT).show();
        //GET DATES
        String start = etStartDate.getText().toString();
        Date startDate = new SimpleDateFormat("MM-dd-yyyy").parse(start);
        String end = etEndDate.getText().toString();
        Date endDate=new SimpleDateFormat("MM-dd-yyyy").parse(end);

        //Spinner variable
        String barangay = selectedBarangay;

        //Store query-matched reports
        Map <String, String> reportMap = new HashMap<>();

        FirebaseFirestore.getInstance()
                .collection("reports").document(barangay)
                .collection("reportDetails").get()
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, CoordinatedBrgyDetailsActivity.class);
                    //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        //FOR EACH
                        //GET ALL ID
                        //startDate occurs before endDate
                            //TODO Match spinner to data
                            //TODO Dates must be between
                            for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                try {
                                    String stringDate = reportSnap.getString("Date");
                                    Date tempDate = new SimpleDateFormat("MM-dd-yyyy").parse(stringDate);

                                    //suppress lint warnings
                                    assert startDate != null;
                                    assert tempDate != null;

                                    //DATE NOTES
                                    //date.after() means date is date1 > date2
                                    //date.before() means date is date1 < date2
                                    //CHECK IF START DATE IS GREATER THAN END DATE
                                    if(startDate.before(endDate)){
                                        //BALIKTAD LNG PALA PLS DONT MODIFY IF ARGUMENT
                                        if(tempDate.compareTo(startDate)>=0 && tempDate.compareTo((endDate))<=0){

                                            //Show message
                                            Toast.makeText(this, reportSnap.getString("Lat"), Toast.LENGTH_SHORT).show();

                                            String dataBarangay = reportMap.put("barangay", reportSnap.getString("Barangay"));
                                            String dataDate = reportMap.put("date", reportSnap.getString("Date"));
                                            String dataLatitude = reportMap.put("latitude", reportSnap.getString("Lat"));
                                            String dataLongitude = reportMap.put("longitude", reportSnap.getString("Lon"));

                                        } else {
                                            Toast.makeText(this, "No Date Found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "Start Date greater than End Date", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        //TODO EXECUTE PDF CODE HERE
                        try {
                            generatePDF(reportMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //NO DATA AVAILABLE
                        Toast.makeText(this, "No Barangays Available", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //TODO REFACTOR TO PDF
    private void generatePDF(Map<String, String> map) throws Exception {
        String username = "holdsafety.ph@gmail.com";
        String password = "HoldSafety@4qmag";
        String subject = "REPORT SUMMARY - HoldSafety";
        String recipient = "201801336@iacademy.edu.ph";
        List<String> recipients = Collections.singletonList(recipient);


        ///////////////////////////////////////////////////////////////////

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});

        ///////////////////////////////////////////////////////////////////

        //BufferedWriter bf = null;
        Document document = new Document();
        Font smallNormal=new Font(Font.FontFamily.TIMES_ROMAN,12,Font.NORMAL);
        //Create a new file that points to the root directory, with the given name:
        File file = new File(getExternalFilesDir(null), "report.pdf");
        Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);


        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;

        try {
            //INITIALIZE PDF HERE
            PdfWriter.getInstance(document, new FileOutputStream(String.valueOf(file)));

            document.open();
            document.addAuthor(username);
            document.addCreationDate();
            document.add(new Paragraph("hello there, " + recipient, smallNormal));

            // create new BufferedWriter for the output file
            //bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, String> entry :
                    map.entrySet()) {

                // put key and value separated by a colon
//                bf.write(entry.getKey() + ":" + entry.getValue());
                document.add(new Paragraph(entry.getKey() + ":"
                        + entry.getValue()+"\n",smallNormal));
                // new line
                //bf.newLine();
            }

            //bf.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {

                // always close the writer

                //bf.close();
                document.close();

            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        //FOR EMAIL
//        new MailTask(this).execute(username, password, recipients, subject, path);

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (path != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        }
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Summary Report");
        this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Asks for permissions on activity start
        setPermissions();
    }
}
