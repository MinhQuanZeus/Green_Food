package zeus.quantm.greenfood.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zeus.quantm.greenfood.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private Toolbar toolbar;
    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        toolbar =(Toolbar) view.findViewById(R.id.tb_main);
        toolbar.setTitle("Me");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        return view;
    }

}
