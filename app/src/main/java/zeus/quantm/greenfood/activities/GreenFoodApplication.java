package zeus.quantm.greenfood.activities;

import android.app.Application;
import android.content.res.Resources;

/**
 * Created by QuanT on 6/26/2017.
 */

public class GreenFoodApplication extends Application {
    public static Resources resources;

    private static GreenFoodApplication instance;
    public static GreenFoodApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }
}
