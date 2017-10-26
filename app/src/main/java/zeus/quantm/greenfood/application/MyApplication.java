package zeus.quantm.greenfood.application;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import zeus.quantm.greenfood.utils.NetworkConnectionSupport;

/**
 * Created by EDGY on 6/27/2017.
 */

public class MyApplication extends Application{
    private static MyApplication myApplication;

    public static synchronized MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public void setConnectivityListener(NetworkConnectionSupport.ConnectivityReceiverListener listener){
        NetworkConnectionSupport.connectivityReceiverListener = listener;
    }

    @Override
    public void onTerminate() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
        super.onTerminate();
    }
}
