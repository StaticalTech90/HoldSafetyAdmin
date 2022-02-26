package com.example.holdsafetyadmin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SendReportViewModel extends ViewModel {
    Context appContext;
    private WorkManager mWorkManager;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    public SendReportViewModel(@NonNull Application application) {
        super();
        mWorkManager = WorkManager.getInstance(application);

        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData("send_report_work_output");
    }

    LiveData<List<WorkInfo>> getOutputWorkInfo() {
        return mSavedWorkInfo;
    }

    void sendReport() {
        Log.v("INVOKED", "send invoked");
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(SendReportWorker.class,
                2, TimeUnit.HOURS)
                .addTag("send_report_work")
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork("send_report_work",
                        ExistingPeriodicWorkPolicy.KEEP, periodicWork);

        WorkManager.getInstance(appContext).enqueue(periodicWork);

    }

    void cancelWork() {
        mWorkManager.cancelUniqueWork("send_report_work");
    }
}
