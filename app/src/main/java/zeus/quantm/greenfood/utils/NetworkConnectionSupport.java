package zeus.quantm.greenfood.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import zeus.quantm.greenfood.application.MyApplication;

/**
 * Created by EDGY on 6/27/2017.
 */

public class NetworkConnectionSupport extends JobService{
    public static NetworkConnectionSupport networkConnectionSupport;
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public static NetworkConnectionSupport getInstance(){
        if(networkConnectionSupport == null){
            networkConnectionSupport = new NetworkConnectionSupport();
        }
        return networkConnectionSupport;
    }

    public static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication
                .getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetword = connectivityManager.getActiveNetworkInfo();
        return activeNetword != null && activeNetword.isConnectedOrConnecting();
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activedNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activedNetwork != null && activedNetwork.isConnectedOrConnecting();
        if(connectivityReceiverListener != null){
            connectivityReceiverListener.onNetwordConnectionChanged(isConnected);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public interface ConnectivityReceiverListener{
        void onNetwordConnectionChanged(boolean isConnected);
    }
}
