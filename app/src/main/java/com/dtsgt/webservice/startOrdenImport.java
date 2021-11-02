package com.dtsgt.webservice;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

public class startOrdenImport {

    public static void startService(Context context, String params) {
        try {
            ComponentName serviceComponent = new ComponentName(context, srvOrdenImport.class);

            PersistableBundle extras = new PersistableBundle();
            extras.putString("params",params);
            JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent).setOverrideDeadline(0L).setExtras(extras);

            builder.setMinimumLatency(29 * 1000); // wait at least
            builder.setOverrideDeadline(21 * 1000); // maximum delay

            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        } catch (Exception e) {
            String ss=e.getMessage();
        }

    }
}
