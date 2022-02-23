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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
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

    private static final int WRITE_EXTERNAL_REQ_CODE = 1000;
    private static final int READ_EXTERNAL_REQ_CODE = 1001;

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

        btnSendReport.setOnClickListener(v -> {
            try {
                generateReport();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        setPermissions();

    }

    //checks required permissions
    private void setPermissions() {
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
            } else {
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

    public String dropdownBarangay() {
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

        ArrayAdapter<String> spinnerBrgyAdapter = new ArrayAdapter<String>(this, R.layout.spinner, barangayList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.hint_color));
                } else {
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
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                    selectedBarangay = spinnerBarangay.getSelectedItem().toString().trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return selectedBarangay;
    }

    //update START DATE value
    private void updateStartDate() {
        //matched with line 174
        String myFormat = "MM-dd-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void selectStartDate() {
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateStartDate();
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
    private void updateEndDate() {
        //matched with line 174
        String myFormat = "MM-dd-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etEndDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void selectEndDate() {
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateEndDate();
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
    public void generateReport() throws ParseException {
//       Toast.makeText(getApplicationContext(), etStartDate.getText(), Toast.LENGTH_SHORT).show();
//       Toast.makeText(getApplicationContext(), etEndDate.getText(), Toast.LENGTH_SHORT).show();
//       Toast.makeText(getApplicationContext(), selectedBarangay, Toast.LENGTH_SHORT).show();
        //GET DATES
        String start = etStartDate.getText().toString();
        Date startDate = new SimpleDateFormat("MM-dd-yyyy").parse(start);
        String end = etEndDate.getText().toString();
        Date endDate = new SimpleDateFormat("MM-dd-yyyy").parse(end);

        //Spinner variable
        String barangay = selectedBarangay;

        //Store query-matched reports
        Map<String, String> reportMap = new HashMap<>();

        FirebaseFirestore.getInstance()
                .collection("reports").whereEqualTo("Barangay", selectedBarangay).get()
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, CoordinatedBrgyDetailsActivity.class);
                    //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        //FOR EACH
                        //GET ALL ID
                        //startDate occurs before endDate
                        //TODO Match spinner to data
                        //TODO Dates must be between
                        //TODO Handle if theres no report
                        Document document = new Document();
                        Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
                        //Create a new file that points to the root directory, with the given name:
                        File file = new File(getExternalFilesDir(null), "report.pdf");
                        Uri path = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file);
                        this.grantUriPermission("com.example.holdsafetyadmin", path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        int count = 0;
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
                            PdfPCell cell = new PdfPCell();
                            PdfPTable table = new PdfPTable(2);
                            PdfWriter.getInstance(document, new FileOutputStream(String.valueOf(file)));

                            document.open();
                            document.addCreationDate();


                            for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                Log.i("report snap", reportSnap.getId());

                                try {

                                    String stringDate = reportSnap.getString("Report Date");
                                    Date tempDate = new SimpleDateFormat("MM-dd-yyyy").parse(stringDate);

                                    //suppress lint warnings
                                    assert startDate != null;
                                    assert tempDate != null;
                                    count++;

                                    //TODO DISABLED CONDITION IN THE MEANTIME [WIP]
                                    //Toast.makeText(this, reportSnap.getId(), Toast.LENGTH_SHORT).show();
                                    //DATE NOTES
                                    //date.after() means date is date1 > date2
                                    //date.before() means date is date1 < date2
                                    //CHECK IF START DATE IS GREATER THAN END DATE
//                                    if(tempDate.after(startDate) && tempDate.before(endDate)){
//                                        //BALIKTAD LNG PALA PLS DONT MODIFY IF ARGUMENT
//                                        if(tempDate.compareTo(startDate)>=0
//                                           && tempDate.compareTo((endDate))<=0){
//                                    } else {
//                                            Toast.makeText(this, "No Date Found", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        Toast.makeText(this, "Start Date greater than End Date", Toast.LENGTH_SHORT).show();
//                                    }
                                    //Column 2

//                                    document.add(new Paragraph("Name: "
//                                            + reportSnap.getString("FirstName") +
//                                            reportSnap.getString("LastName") + "\n", smallNormal));
//                                    document.add(new Paragraph("Latitude: "
//                                            + reportSnap.getString("Lat") + "\n", smallNormal));
//                                    document.add(new Paragraph("Longitude: "
//                                            + reportSnap.getString("Lon") + "\n", smallNormal));
//                                    document.add(new Paragraph("Report Date: "
//                                            + reportSnap.getString("Report Date") + "\n", smallNormal));
//                                    document.add(new Paragraph("\n", smallNormal));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            document.add(new Paragraph(selectedBarangay, smallNormal));
                            document.add(new Paragraph(String.valueOf(count), smallNormal));

                            document.close();
                            Log.i("PDF", "PDF Generated");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                document.close();
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                            }
                        }
                        //TODO EXECUTE SEND EMAIL
                        try {
                            sendReport();
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
    @SuppressLint("QueryPermissionsNeeded")
    private void sendReport() {
        String username = "holdsafety.ph@gmail.com";
        String password = "HoldSafety@4qmag";
        String subject = "REPORT SUMMARY - HoldSafety";
        String recipient = "201801263@iacademy.edu.ph"; //, 201801336@iacademy.edu.ph
        List<String> recipients = Collections.singletonList(recipient);

        ///////////////////////////////////////////////////////////////////

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});

        ///////////////////////////////////////////////////////////////////

        Document document = new Document();
        Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        //Create a new file that points to the root directory, with the given name:
        File file = new File(getExternalFilesDir(null), "report.pdf");
        Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        this.grantUriPermission(String.valueOf(this.getPackageManager().queryIntentActivities(
                emailIntent,
                PackageManager.MATCH_DEFAULT_ONLY)),
                path,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //KEEP FOR DEBUGGING
        //emailIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
