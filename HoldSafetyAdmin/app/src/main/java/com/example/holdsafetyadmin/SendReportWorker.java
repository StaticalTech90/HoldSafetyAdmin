package com.example.holdsafetyadmin;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SendReportWorker extends Worker {
    Context applicationContext = getApplicationContext();
    private static final String DEFAULT_START_TIME = "00:01";
    private static final String DEFAULT_END_TIME = "03:00";

    String reportLat;
    String reportLong;
    String reportAdd;
    String message;

    public SendReportWorker(
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
                                    int count = 0;

                                    try {
                                        for (QueryDocumentSnapshot reportSnap : Objects.requireNonNull(task.getResult())) {
                                            Log.i("report snap", reportSnap.getId());

                                            Date tempDate = reportSnap.getTimestamp("Report Date").toDate();

                                            //CHECK IF START DATE IS GREATER THAN END DATE
                                            //declare long and lat of the report, for geoloc functionality
                                            reportLat = reportSnap.getString("Lat");
                                            reportLong = reportSnap.getString("Lon");
                                            reportAdd = getGeoLoc(reportLat, reportLong);
                                            count++;

                                        }

                                        //message here
                                        message = "Address: " + reportAdd +"\n Count: "+ count;

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //TODO EXECUTE SEND EMAIL
                                    try {
                                        String email = "201801336@iacademy.edu.ph";
                                        String username = "holdsafety.ph@gmail.com";
                                        String password = "HoldSafety@4qmag";
                                        String subject = "AUTOMATED Alert Message - HoldSafety";

                                        List<String> recipients = Collections.singletonList(email);
                                        //email of sender, password of sender, list of recipients, email subject, email body
                                        new MailTask(SendReportWorker.class).execute(username, password, recipients, subject, message);

                                        //Toast.makeText(LandingActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
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
           // }
        } catch (Exception ignored) {
            Log.e("ParseException", ignored.getMessage());
            return Result.failure();
        }

        return Result.success();
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
