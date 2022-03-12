package com.example.holdsafetyadmin;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GenerateReportWorker extends Worker {
    Context applicationContext = getApplicationContext();
    private static final String DEFAULT_START_TIME = "00:01";
    private static final String DEFAULT_END_TIME = "03:00";

    String reportLat;
    String reportLong;
    String reportAdd;
    String message;
    File reportFile;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference videoRef = FirebaseStorage.getInstance().getReference("reports/");

    public GenerateReportWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        DateFormat dateGeneratedFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        Date currentDate = calendar.getTime();
        String formatDateGenerated = dateGeneratedFormat.format(currentDate);

        //get last day of month
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();
        String formatLastDayOfMonth = dateFormat.format(lastDayOfMonth);

        //get first day of month
        calendar.set(Calendar.DAY_OF_MONTH,1);

        Date firstDayOfMonth = calendar.getTime();
        String formatFirstDayOfMonth = dateFormat.format(firstDayOfMonth);

        DateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String formattedHour = hourFormat.format(date);

        try {
            Date currentDay = hourFormat.parse(formattedHour);
            Date startDate = hourFormat.parse(DEFAULT_START_TIME);
            Date endDate = hourFormat.parse(DEFAULT_END_TIME);

            //if (day == 1 && currentDay.after(startDate) && currentDay.before(endDate)) {
                try {
                    db.collection("reports").orderBy("Report Date", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                Document document = new Document();
                                Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
                                Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

                                //Create a new file that points to the root directory, with the given name:
                                reportFile = new File(getApplicationContext().getExternalFilesDir(null), "report.pdf");
                                Uri path = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                                        BuildConfig.APPLICATION_ID + ".provider", reportFile);
                                getApplicationContext().grantUriPermission("com.example.holdsafetyadmin",
                                        path,
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

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
                                    PdfWriter.getInstance(document, new FileOutputStream(String.valueOf(reportFile)));

                                    document.open();
                                    document.addCreationDate();

                                    try {
                                        Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.holdsafety_login_admin);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        Image image = Image.getInstance(stream.toByteArray());
                                        image.scalePercent(10);
                                        image.setAlignment(Element.ALIGN_CENTER);
                                        document.add(image);
                                    } catch (IOException e) {
                                        Log.e("IOEXception", e.getMessage());
                                    }

                                    table.addCell(new Paragraph("Name", smallBold));
                                    table.addCell(new Paragraph("Address", smallBold));
                                    table.addCell(new Paragraph("Report Date", smallBold));

                                    for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                        Log.i("report snap", reportSnap.getId());

                                        Date tempDate = reportSnap.getTimestamp("Report Date").toDate();

                                        reportLat = reportSnap.getString("Lat");
                                        reportLong = reportSnap.getString("Lon");
                                        String reportAdd = getGeoLoc(reportLat, reportLong);

                                        count++;

                                        table.addCell(new Paragraph(reportSnap.getString("FirstName") + " " +
                                                reportSnap.getString("LastName"), smallNormal));
                                        table.addCell(new Paragraph(reportAdd, smallNormal));
                                        table.addCell(new Paragraph(tempDate.toString(), smallNormal));
                                    }

                                    //This will become link "Address: " + reportAdd
                                    message =  "Report Count: "+ count + "<br />";
                                    document.add(new Paragraph("Date generated: "+ formatDateGenerated, smallNormal));
                                    document.add(new Paragraph("Barangay: "+ "test", smallNormal));
                                    document.add(new Paragraph("Report Range: " + formatFirstDayOfMonth + " to " + formatLastDayOfMonth, smallNormal));
                                    document.add(new Paragraph("Number of Reports: " + count + "\n\n", smallNormal));
                                    document.add(table);
                                    document.close();
                                    Log.i("PDF", "PDF Generated");

                                    saveToFireBase(document);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                //NO DATA AVAILABLE
                                Log.e("Task Error", "No Barangays Available");

                            }
                        });
                } catch (Throwable throwable) {
                    Log.e(TAG, "Error", throwable);
                    return Result.failure();
                }
//            } else {
//                return Result.retry();
//            }
        } catch (Exception ignored) {
            Log.e("ParseException", ignored.getMessage());
            return Result.failure();
        }

        return Result.success();
    }

    private void saveToFireBase(Document document){
        FirebaseStorage.getInstance()
                .getReference("reports")
                .child(reportFile.getName())
                .putFile(Uri.fromFile(reportFile))
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(applicationContext, "Upload successful", Toast.LENGTH_SHORT).show();
                    videoRef.child(reportFile.getName()).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d("Video to Document", "Fetching report URI success");
                                //TODO SAVE TO FIREBASE STORAGE AS PDF THEN SEND
                                try {
                                    String email = "201801263@iacademy.edu.ph";
                                    String username = "holdsafety.ph@gmail.com";
                                    String password = "HoldSafety@4qmag";
                                    String subject = "AUTOMATED Alert Message - HoldSafety";

                                    List<String> recipients = Collections.singletonList(email);
                                    new MailTask(GenerateReportWorker.class).execute(username, password, recipients, subject, message+uri);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .addOnFailureListener(e -> Log.d("Video to Document", "Fetching video URI failed. Log: " + e.getMessage()));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(applicationContext, "Upload failed: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public String getGeoLoc(String reportLat, String reportLong) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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
}
