package zeus.quantm.greenfood.managers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by QuanT on 5/30/2017.
 */

public class ScreenManager {
    public static void openFragment(FragmentManager fragmentManager, Fragment fragment,
                                    int layoutID, boolean addToBackStack){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(layoutID,fragment);
        if(addToBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public static void backFragment(FragmentManager fragmentManager){
        fragmentManager.popBackStackImmediate();
    }

}
