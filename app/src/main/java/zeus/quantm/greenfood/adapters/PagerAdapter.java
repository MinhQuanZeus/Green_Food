package zeus.quantm.greenfood.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import zeus.quantm.greenfood.fragments.MapViewNewFeedFragment;
import zeus.quantm.greenfood.fragments.NormalNewFeedFragment;

/**
 * Created by QuanT on 5/28/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NormalNewFeedFragment();
            case 1:
                return new MapViewNewFeedFragment();
            default:
                return new NormalNewFeedFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
