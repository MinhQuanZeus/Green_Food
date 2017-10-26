package zeus.quantm.greenfood.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.network.RetrofitFactory;
import zeus.quantm.greenfood.network.models.order.Order;
import zeus.quantm.greenfood.network.models.order.SendOrderRequest;
import zeus.quantm.greenfood.network.models.order.SendOrderResponse;
import zeus.quantm.greenfood.network.services.SendOrderService;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodDetailFragment extends Fragment implements View.OnClickListener {

    public FoodDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);
        return view;
    }


    @Override
    public void onClick(View view) {

    }
}
