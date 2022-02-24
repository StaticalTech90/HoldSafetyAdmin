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
import java.util.Date;
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

public class SendReportWorker extends Worker {
    Context applicationContext = getApplicationContext();

    public SendReportWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            FirebaseFirestore.getInstance()
                    .collection("reports").orderBy("Report Date", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int count = 0;

                            try {
                                for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                    Log.i("report snap", reportSnap.getId());

                                    Date tempDate = reportSnap.getTimestamp("Report Date").toDate();

                                    //suppress lint warnings

                                    //TODO DISABLED CONDITION IN THE MEANTIME [WIP]
                                    //Toast.makeText(this, reportSnap.getId(), Toast.LENGTH_SHORT).show();
                                    //DATE NOTES
                                    //date.after(); // means date is date1 > date2
                                    //date.before(); // means date is date1 < date2

                                    //CHECK IF START DATE IS GREATER THAN END DATE
                                        //declare long and lat of the report, for geoloc functionality
                                        String reportLat = reportSnap.getString("Lat");
                                        String reportLong = reportSnap.getString("Lon");
                                        String reportAdd = getGeoLoc(reportLat, reportLong);

                                        count++;

                                    /*
                                    document.add(new Paragraph("Name: "
                                            + reportSnap.getString("FirstName") + " " +
                                            reportSnap.getString("LastName") + "\n", smallNormal));
                                    document.add(new Paragraph("Address: " + reportAdd + "\n", smallNormal));
                                    document.add(new Paragraph("Report Date: "
                                            + tempDate.toString() + "\n", smallNormal));
                                    document.add(new Paragraph("\n", smallNormal));
                                     */

                                        //BALIKTAD LNG PALA PLS DONT MODIFY IF ARGUMENT
                                    /*
                                    if(tempDate.compareTo(startDate)>=0 && tempDate.compareTo((endDate))<=0){
                                        Toast.makeText(this, "Hello Reports", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "No Date Found", Toast.LENGTH_SHORT).show();
                                    }
                                     */
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //TODO EXECUTE SEND EMAIL
                            try {
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            //NO DATA AVAILABLE
                            Log.e("Task Error", "No Barangays Available");
                        }
                    });
            return Result.success();
        } catch (Throwable throwable) {

            // Technically WorkManager will return Result.failure()
            // but it's best to be explicit about it.
            // Thus if there were errors, we're return FAILURE
            Log.e(TAG, "Error", throwable);
            return Result.failure();
        }

    }

    public String getGeoLoc(String reportLat, String reportLong) throws IOException {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(applicationContext, Locale.getDefault());
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
