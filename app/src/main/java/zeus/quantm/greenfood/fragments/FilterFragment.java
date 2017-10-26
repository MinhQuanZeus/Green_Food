package zeus.quantm.greenfood.fragments;

import android.app.Dialog;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import zeus.quantm.greenfood.R;

/**
 * Created by QuanT on 7/8/2017.
 */

public class FilterFragment extends AAH_FabulousFragment {
    ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    List<TextView> textviews = new ArrayList<>();

    TabLayout tabs_types;
    ImageButton imgbtn_refresh, imgbtn_apply;
    SectionsPagerAdapter mAdapter;
    private DisplayMetrics metrics;
    private SensorManager sensorManager;


    public static FilterFragment newInstance() {
        FilterFragment mff = new FilterFragment();
        return mff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  applied_filters = ((NewFeedFragment)getContext()).getApplied_filters();
        metrics = this.getResources().getDisplayMetrics();

//        for (Map.Entry<String, List<String>> entry : applied_filters.entrySet()) {
//            Log.d("k9res", "from activity: "+entry.getKey());
//            for(String s: entry.getValue()){
//                Log.d("k9res", "from activity val: "+s);
//
//            }
//        }
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.view_filter, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);

        ViewPager vp_types = (ViewPager) contentView.findViewById(R.id.vp_types);
        tabs_types = (TabLayout) contentView.findViewById(R.id.tabs_types);

        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter(applied_filters);
            }
        });
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TextView tv : textviews) {
                    tv.setTag("unselected");
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                applied_filters.clear();
            }
        });

        mAdapter = new SectionsPagerAdapter();
        vp_types.setOffscreenPageLimit(4);
        vp_types.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        tabs_types.setupWithViewPager(vp_types);


        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
//        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

    public class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_filters_sorters, collection, false);
            FlexboxLayout fbl = (FlexboxLayout) layout.findViewById(R.id.fbl);
//            LinearLayout ll_scroll = (LinearLayout) layout.findViewById(R.id.ll_scroll);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels-(104*metrics.density)));
//            ll_scroll.setLayoutParams(lp);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("price", fbl);
                    break;
                case 1:
                    inflateLayoutWithFilters("rating", fbl);
                    break;
                case 2:
                    inflateLayoutWithFilters("time", fbl);
                    break;
                case 3:
                    inflateLayoutWithFilters("sort", fbl);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Giá";
                case 1:
                    return "Đánh giá";
                case 2:
                    return "Thời gian";
                case 3:
                    return "Sắp xếp";
            }
            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
        List<String> keys = new ArrayList<>();
        switch (filter_category) {
            case "price":
                keys = getUniquePriceKeys();
                break;
            case "rating":
                keys = getUniqueRatingKeys();
                break;
            case "time":
                keys = getUniqueTimeKeys();
                break;
            case "sort":
                keys = getUniqueSortKeys();
                break;
        }
        if (!filter_category.equals("sort")) {
            for (int i = 0; i < keys.size(); i++) {
                View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
                final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
                tv.setText(keys.get(i));
                final int finalI = i;
                final List<String> finalKeys = keys;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tv.getTag() != null && tv.getTag().equals("selected")) {
                            tv.setTag("unselected");
                            tv.setBackgroundResource(R.drawable.chip_unselected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                            removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                        } else {
                            tv.setTag("selected");
                            tv.setBackgroundResource(R.drawable.chip_selected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                            addToSelectedMap(filter_category, finalKeys.get(finalI));
                        }
                    }
                });
                if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalI))) {
                    tv.setTag("selected");
                    tv.setBackgroundResource(R.drawable.chip_selected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                } else {
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                textviews.add(tv);

                fbl.addView(subchild);
            }
        }else{
            for (int i = 0; i < keys.size(); i++) {
                View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
                final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
                tv.setText(keys.get(i));
                final int finalI = i;
                final List<String> finalKeys = keys;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int j=0;j<finalKeys.size();j++){
                            tv.setTag("unselected");
                            tv.setBackgroundResource(R.drawable.chip_unselected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                            removeFromSelectedMap(filter_category, finalKeys.get(j));
                        }
                        if (tv.getTag() != null && tv.getTag().equals("selected")) {
                            tv.setTag("unselected");
                            tv.setBackgroundResource(R.drawable.chip_unselected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                            removeFromSelectedMap(filter_category, finalKeys.get(finalI));
                        } else {
                            tv.setTag("selected");
                            tv.setBackgroundResource(R.drawable.chip_selected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                            addToSelectedMap(filter_category, finalKeys.get(finalI));
                        }
                    }
                });
                if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalI))) {
                    tv.setTag("selected");
                    tv.setBackgroundResource(R.drawable.chip_selected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                } else {
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                textviews.add(tv);

                fbl.addView(subchild);
            }
        }


    }

    private void addToSelectedMap(String key, String value) {
        if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {
            applied_filters.get(key).add(value);
        } else {
            List<String> temp = new ArrayList<>();
            temp.add(value);
            applied_filters.put(key, temp);
        }
    }

    private void removeFromSelectedMap(String key, String value) {
        if(applied_filters.get(key)!=null) {
            if (applied_filters.get(key).size() == 1) {
                applied_filters.remove(key);
            } else {
                applied_filters.get(key).remove(value);
            }
        }
    }

    public List<String> getUniquePriceKeys() {
        List<String> price = new ArrayList<>();
        price.add("Miễn phí");
        price.add("0 - 100.000₫");
        price.add("100.000 - 200.000₫");
        price.add("200.000 - 500.000₫");
        price.add(">500.000₫");
        Collections.sort(price);
        return price;
    }

    public List<String> getUniqueRatingKeys() {
        List<String> rating = new ArrayList<>();
        rating.add("> 0");
        rating.add("> 1");
        rating.add("> 2");
        rating.add("> 3");
        rating.add("> 4");
        Collections.sort(rating);
        return rating;
    }

    public List<String> getUniqueTimeKeys() {
        List<String> rating = new ArrayList<>();
        rating.add("< 1h");
        rating.add("< 3h");
        rating.add("> 5h");
        rating.add("< 7h");
        rating.add("< 24h");
        Collections.sort(rating);
        return rating;
    }

    public List<String> getUniqueSortKeys() {
        List<String> rating = new ArrayList<>();
        rating.add("Theo thời gian");
        rating.add("Theo giá tăng dần");
        rating.add("Theo giá giảm dần");
        Collections.sort(rating);
        return rating;
    }

}
