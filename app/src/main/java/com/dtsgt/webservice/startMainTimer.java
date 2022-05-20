package com.dtsgt.webservice;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

public class startMainTimer {

    public static void startService(Context context, String params) {
        try {
            ComponentName serviceComponent = new ComponentName(context, srvTimerService.class);
            PersistableBundle extras = new PersistableBundle();
            extras.putString("params",params);
            JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent).setOverrideDeadline(0L).setExtras(extras);
            builder.setMinimumLatency(30 * 1000); // wait at least
            builder.setOverrideDeadline(20 * 1000); // maximum delay

            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());

        } catch (Exception e) {
            String ss=e.getMessage();
        }
    }

}
