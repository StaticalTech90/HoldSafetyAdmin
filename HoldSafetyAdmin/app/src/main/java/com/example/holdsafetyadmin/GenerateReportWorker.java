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
import androidx.core.content.FileProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date date = c.getTime();
        String formattedDate = dateFormat.format(date);

        try {
            Date currentDate = dateFormat.parse(formattedDate);
            Date startDate = dateFormat.parse(DEFAULT_START_TIME);
            Date endDate = dateFormat.parse(DEFAULT_END_TIME);

            //if (day == 1 && currentDate.after(startDate) && currentDate.before(endDate)) {
                try {

                    FirebaseFirestore.getInstance()
                            .collection("reports").orderBy("Report Date", Query.Direction.ASCENDING)
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
//                                        //INITIALIZE PDF HERE
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
                                            count++;
                                            table.addCell(new Paragraph(reportSnap.getString("FirstName") + " " +
                                                    reportSnap.getString("LastName"), smallNormal));
                                            //table.addCell(new Paragraph(reportAdd, smallNormal));
                                            table.addCell(new Paragraph(tempDate.toString(), smallNormal));
                                        }

                                        //This will become link "Address: " + reportAdd
                                        message =  "Report Count: "+ count + "<br />";
                                        document.add(new Paragraph("Barangay: "+ "test", smallNormal));
                                        document.add(new Paragraph("Report Range: " + startDate + " to " + endDate, smallNormal));
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
                                    String email = "201801336@iacademy.edu.ph";
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
}
