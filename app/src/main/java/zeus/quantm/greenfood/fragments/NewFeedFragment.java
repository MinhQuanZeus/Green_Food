package zeus.quantm.greenfood.fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.adapters.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFeedFragment extends Fragment implements AAH_FabulousFragment.Callbacks {

    private ArrayMap<String, List<String>> applied_filters =new ArrayMap<>();

  //  @BindView(R.id.tab_layout)
    TabLayout tabLayout;
 //   @BindView(R.id.view_pager)
    ViewPager viewPager;
    FloatingActionButton fab_filter;
    private Toolbar toolbar;
    public NewFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_feed, container, false);
        tabLayout =view.findViewById(R.id.tab_layout);
        viewPager =view.findViewById(R.id.view_pager);
        toolbar =(Toolbar) view.findViewById(R.id.tb_main);
        toolbar.setTitle("Green Food");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setupUI(view);

        return view;

    }

    public void setupUI(View view) {
        ButterKnife.bind(this,view);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_shopping_basket_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_location_on_black_24dp));
        fab_filter = view.findViewById(R.id.fab_sort);

        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.icon_selected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(),2);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(getResources().getColor(R.color.icon_selected), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabLayout.getTabAt(tab.getPosition()).getIcon().setColorFilter(getResources().getColor(R.color.icon_unselected), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterFragment dialogFrag = FilterFragment.newInstance();
                dialogFrag.setParentFab(fab_filter);
                dialogFrag.show(getActivity().getSupportFragmentManager(), dialogFrag.getTag());
            }
        });
    }
    public ArrayMap<String, List<String>> getApplied_filters() {
        return applied_filters;
    }


    @Override
    public void onResult(Object result) {

    }
}
