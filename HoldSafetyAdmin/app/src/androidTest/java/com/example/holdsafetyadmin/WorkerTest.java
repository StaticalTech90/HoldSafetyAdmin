package com.example.holdsafetyadmin;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestWorkerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RunWith(JUnit4.class)
public class WorkerTest {
    private Context context;
    private Executor executor;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        executor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void testSleepWorker() {
        SendReportWorker worker =
                TestWorkerBuilder.from(context,
                        SendReportWorker.class,
                        executor)
                        .build();

        ListenableWorker.Result result = worker.doWork();
        assertThat(result, is(ListenableWorker.Result.success()));
}}
