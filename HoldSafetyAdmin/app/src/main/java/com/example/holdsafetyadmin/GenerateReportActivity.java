package com.example.holdsafetyadmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
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
    private SendReportViewModel srViewModel;

    ImageView btnBack;
    Spinner spinnerBarangay;
    String selectedBarangay;
    Button btnSendReport;
    EditText etStartDate, etEndDate;
    Date startDate, endDate;

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

        //get viewmodel
        srViewModel = new SendReportViewModel(getApplication());

        //TODO DECLARE BOTH DATEPICKER
        //TODO DECLARE ONLICKLISTENER
        spinnerBarangay = findViewById(R.id.txtBarangayName);
        etStartDate = findViewById(R.id.txtStartDate);
        etEndDate = findViewById(R.id.txtEndDate);
        btnSendReport = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.backArrow);

        dropdownBarangay();
        selectStartDate();
        selectEndDate();

        btnBack.setOnClickListener(view -> goBack());
        btnSendReport.setOnClickListener(v -> {
            try {
                validateInput();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        setPermissions();

        //srViewModel.cancelWork();
        srViewModel.sendReport();
        srViewModel.getOutputWorkInfo().observe(this, listOfWorkInfo -> {
            if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                return;
            }

            WorkInfo workInfo = listOfWorkInfo.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                Log.i("FINISHED", "NOT Done");
            } else {
                Log.i("FINISHED", "Done");
            }
        });
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
        barangayList.add("Barangay *");

        FirebaseFirestore.getInstance()
                .collection("barangay").get()
                .addOnCompleteListener(task -> {
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
        String myFormat = "MM-dd-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etStartDate.setText(dateFormat.format(calendar.getTime()));
        etStartDate.setError(null);
    }

    private void selectStartDate() {
        Calendar currentCal = Calendar.getInstance();
        int mYear = currentCal.get(Calendar.YEAR);
        int mMonth = currentCal.get(Calendar.MONTH);
        int mDay = currentCal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener startDateListener = (arg0, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateStartDate();
            Log.d("onDateSet()", "arg0 = [" + arg0 + "], year = [" + year + "], monthOfYear = [" + monthOfYear + "], dayOfMonth = [" + dayOfMonth + "]");
        };

        etStartDate.setOnClickListener(view -> {
            DatePickerDialog dpDialog = new DatePickerDialog(GenerateReportActivity.this, startDateListener, mYear, mMonth, mDay);
            dpDialog.getDatePicker().setMaxDate(currentCal.getTimeInMillis());
            dpDialog.show();
        });
    }

    //update END DATE value
    private void updateEndDate() {
        String myFormat = "MM-dd-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        etEndDate.setText(dateFormat.format(calendar.getTime()));
        etEndDate.setError(null);
    }

    private void selectEndDate() {
        Calendar currentCal = Calendar.getInstance();
        int mYear = currentCal.get(Calendar.YEAR);
        int mMonth = currentCal.get(Calendar.MONTH);
        int mDay = currentCal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener startDateListener = (arg0, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateEndDate();
            Log.d("onDateSet()", "arg0 = [" + arg0 + "], year = [" + year + "], monthOfYear = [" + monthOfYear + "], dayOfMonth = [" + dayOfMonth + "]");
        };

        etEndDate.setOnClickListener(view -> {
            DatePickerDialog dpDialog = new DatePickerDialog(GenerateReportActivity.this, startDateListener, mYear, mMonth, mDay);
            dpDialog.getDatePicker().setMaxDate(currentCal.getTimeInMillis());
            dpDialog.show();
        });
    }

    //TODO PDF REPORT LOGIC HERE
    @SuppressLint("SimpleDateFormat")
    public void validateInput() throws ParseException {
        try{
            //GET DATES
            String start = etStartDate.getText().toString().trim();
            String end = etEndDate.getText().toString().trim();

            if(TextUtils.isEmpty(start)) {
                etStartDate.getText().clear();
                etStartDate.setHint("Enter a start date");
                etStartDate.setError("Enter a start date");
            } else if (TextUtils.isEmpty(end)) {
                etEndDate.getText().clear();
                etEndDate.setHint("Enter a start date");
                etEndDate.setError("Enter a start date");
            } else {
                startDate = new SimpleDateFormat("MM-dd-yyyy").parse(start);
                endDate = new SimpleDateFormat("MM-dd-yyyy").parse(end);

                if(startDate.after(endDate)){
                    etEndDate.getText().clear();
                    etEndDate.setHint("End date should be later than start date");
                    etEndDate.setError("End date should be later than start date");
                } else if(spinnerBarangay.getSelectedItem().equals("Barangay *")) {
                    ((TextView)spinnerBarangay.getSelectedView()).setError("Please select barangay");
                } else {
                    generateReport();
                }
            }
        } catch (ParseException parseException){
            Log.w("Generate Report", parseException.getMessage());
        }
    }

    public void generateReport(){
        FirebaseFirestore.getInstance()
                .collection("reports")
                .whereEqualTo("Barangay", selectedBarangay).orderBy("Report Date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //startDate occurs before endDate
                        //TODO Match spinner to data
                        //TODO Dates must be between
                        //TODO Handle if theres no report
                        Document document = new Document();
                        Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
                        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

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
                            PdfPTable table = new PdfPTable(3);
                            PdfWriter.getInstance(document, new FileOutputStream(String.valueOf(file)));

                            document.open();
                            document.addCreationDate();

                            try {
                                //InputStream ims = getAssets().open("holdsafety_login_admin.png");
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.holdsafety_logo);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                Image image = Image.getInstance(stream.toByteArray());
                                image.scalePercent(10);
                                image.setAlignment(Element.ALIGN_CENTER);
                                document.add(image);
                            } catch (IOException e) {
                                Toast.makeText(this, "Img Catch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            table.addCell(new Paragraph("Name", smallBold));
                            table.addCell(new Paragraph("Address", smallBold));
                            table.addCell(new Paragraph("Report Date", smallBold));

                            for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                Log.i("report snap", reportSnap.getId());

                                Date tempDate = reportSnap.getTimestamp("Report Date").toDate();

                                //suppress lint warnings
                                assert startDate != null;
                                assert tempDate != null;

                                //TODO DISABLED CONDITION IN THE MEANTIME [WIP]
                                //Toast.makeText(this, reportSnap.getId(), Toast.LENGTH_SHORT).show();
                                //DATE NOTES
                                //date.after(); // means date is date1 > date2
                                //date.before(); // means date is date1 < date2

                                //CHECK IF START DATE IS GREATER THAN END DATE
                                if(tempDate.after(startDate) && tempDate.before(endDate)){
                                    //declare long and lat of the report, for geoloc functionality
                                    String reportLat = reportSnap.getString("Lat");
                                    String reportLong = reportSnap.getString("Lon");
                                    String reportAdd = getGeoLoc(reportLat, reportLong);

                                    //Column 2
                                    count++;
                                    table.addCell(new Paragraph(reportSnap.getString("FirstName") + " " +
                                            reportSnap.getString("LastName"), smallNormal));
                                    table.addCell(new Paragraph(reportAdd, smallNormal));
                                    table.addCell(new Paragraph(tempDate.toString(), smallNormal));

                                } else {
                                    //Toast.makeText(this, "Report Date is not within the range of selected dates", Toast.LENGTH_SHORT).show();
                                }
                            }
                            document.add(new Paragraph("Barangay: " + selectedBarangay, smallNormal));
                            document.add(new Paragraph("Report Range: " + startDate + " to " + endDate, smallNormal));
                            document.add(new Paragraph("Number of Reports: " + String.valueOf(count) + "\n\n", smallNormal));
                            document.add(table);
                            document.close();
                            Log.i("PDF", "PDF Generated");

                            if(count<1){
                                Toast.makeText(this, "No reports within the range", Toast.LENGTH_SHORT).show();
                            } else {
                                //TODO EXECUTE SEND EMAIL
                                try {
                                    sendReport();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error Catch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } finally {
                            try {
                                document.close();
                            } catch (Exception e) {
                                Toast.makeText(this, "Error Finally Catch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Error", e.getMessage());
                            }
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
        String subject = "REPORT SUMMARY - HoldSafety";
        String recipient = "201801336@iacademy.edu.ph"; // 201801263@iacademy.edu.ph

        ///////////////////////////////////////////////////////////////////

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});

        ///////////////////////////////////////////////////////////////////
        //Create a new file that points to the root directory, with the given name:
        File file = new File(getExternalFilesDir(null), "report.pdf");
        Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        this.grantUriPermission(String.valueOf(this.getPackageManager().queryIntentActivities(
                emailIntent,
                PackageManager.MATCH_DEFAULT_ONLY)),
                path,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //KEEP FOR DEBUGGING
        //emailIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //FOR EMAIL
        //new MailTask(this).execute(username, password, recipients, subject, path);

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (path != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        }
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Summary Report");
        this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        finish(); //end this activity
    }

    public String getGeoLoc(String reportLat, String reportLong) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        double doubleLat = Double.parseDouble(reportLat.trim());
        double doubleLong = Double.parseDouble(reportLong.trim());
        try {
            List<Address> addresses = geocoder.getFromLocation(doubleLat, doubleLong, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Barangay Address", strReturnedAddress.toString());
            } else {
                Log.w("Barangay Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Barangay Address", "Cannot get Address!");
        }
        return strAdd.trim();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Asks for permissions on activity start
        setPermissions();
    }

    private void goBack() {
        finish();
    }
}