package de.trackcat;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;

public class ClearCallService  extends JobService {
    private static final int JOB_ID = 1;

    public static void schedule(Context context, int currentUser) {

        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("currentUser", currentUser);

        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName =
                new ComponentName(context, ClearCallService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setExtras(bundle);
        jobScheduler.schedule(builder.build());
    }

    public static void cancel(Context context) {
        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        /* executing a task synchronously */

        GlobalFunctions.deleteAllTempRecord(ClosingService.getInstance(), params.getExtras().getInt("currentUser"));

        if (true) {
            // To finish a periodic JobService,
            // you must cancel it, so it will not be scheduled more.
            ClearCallService.cancel(this);
        }

        // false when it is synchronous.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}