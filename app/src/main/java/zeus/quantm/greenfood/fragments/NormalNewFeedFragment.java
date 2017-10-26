package zeus.quantm.greenfood.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Timer;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.activities.FoodDetailActivity;
import zeus.quantm.greenfood.models.Post;
import zeus.quantm.greenfood.models.User;
import zeus.quantm.greenfood.network.RetrofitFactory;
import zeus.quantm.greenfood.network.models.distance.MainObject;
import zeus.quantm.greenfood.network.services.GetDistanceService;
import zeus.quantm.greenfood.utils.DateTimeUntils;
import zeus.quantm.greenfood.utils.LibrarySupportManager;
import zeus.quantm.greenfood.utils.NetworkConnectionSupport;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NormalNewFeedFragment extends Fragment {

    private static final String NO_INTERNET_CONNECTION = "Vui lòng kiểm tra lại kết nối mạng";
    private RecyclerView rvNewFeed;
    SpinKitView ivLoading;
    private DatabaseReference databaseReference;
    private Timer timercheckconnection;
    FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter;

    public NormalNewFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();


        //get data from firebase
        Query query = databaseReference.orderByChild("time");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.item_food,
                PostViewHolder.class,
                query
        ) {
            @Override
            public Post getItem(int position) {
                return super.getItem(getItemCount() - (position+1));
            }

            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final Post model, int position) {
                //load data in view holder
                ivLoading.setVisibility(View.INVISIBLE);
                viewHolder.loadData(getContext(), model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), FoodDetailActivity.class).putExtra("post",model);
                        Log.d("Detail","Des: "+model.getDescription());
                        startActivity(intent);
                    }
                });
            }
        };
        rvNewFeed.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView tvFoodName;
        private TextView tvPrice;
        private ImageView ivFood;
        private TextView tvTime;
        private TextView tvUser;
        private ImageView ivUser;
        private TextView tvDistance;
        private TextView tvDescription;
        private String distance;
        View itemView;
        public PostViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;


            setupUI();
        }

        private void setupUI() {
            //Bind itemView
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvTime = itemView.findViewById(R.id.tv_current_time);
            tvUser = itemView.findViewById(R.id.tv_username);
            ivUser = itemView.findViewById(R.id.iv_avatar);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        private void loadData(Context context, Post post){
            //1. Load title(food name), description, price
            tvFoodName.setText(post.getTitle());
            tvPrice.setText(
                    LibrarySupportManager
                            .getInstance()
                            .formatCurrency(post.getPrice())
            );

            //get distance
            getDistanceFromLocation("22C Thành Công, Khu tập thể Bắc Thành Công, Thành Công, Ba Đình, Hà Nội, Vietnam", post.getAddress());

            //2. Load image food
            Picasso.with(context)
                    .load(post.getImage())
                    .into(ivFood);
            tvTime.setText(post.getTime());
            tvDescription.setText(post.getDescription());
//            Log.d("Date:",post.getTime()+"");
//            Log.d("Converted Date:",LibrarySupportManager.convertStringToDate(post.getTime())+"");
//            Log.d("New feed:","Time ago: "+ DateTimeUntils.toRelative(LibrarySupportManager.convertStringToDate(post.getTime()),new Date()));

            //3. Load Post Owner
            if(post.getUserID() != null) {
                getUser(context, post.getUserID());
            }
        }

        private String getDistanceFromLocation(String current, final String destinate){
            GetDistanceService getDistanceService = RetrofitFactory.getInstance("https://developers.google.com/maps/")
                    .createService(GetDistanceService.class);
            getDistanceService.getDistance("imperial", current, destinate, "AIzaSyBs7LWRp7vadlOd79qe_c01BTwRw_KF5VE")
                    .enqueue(new Callback<MainObject>() {
                        @Override
                        public void onResponse(Call<MainObject> call, Response<MainObject> response) {
                            if(response.code() == 200){
                                String statusGoogleAPI = response.body()
                                        .getStatusGoogleAPI();
                                if(statusGoogleAPI.equals("OK")) {
                                    String status = response.body()
                                            .getRows().get(0)
                                            .getElements().get(0)
                                            .getStatus();
                                    if(status.equals("OK")){
                                        distance = response.body()
                                                .getRows().get(0)
                                                .getElements().get(0)
                                                .getDistance()
                                                .getText();
                                    } else distance = "";
                                } else distance = "";
                                tvDistance.setText(LibrarySupportManager.getInstance().distanceFromLocationFormat(distance));
                            }
                            Log.d(TAG,"distance : "+distance);
                        }

                        @Override
                        public void onFailure(Call<MainObject> call, Throwable t) {

                        }
                    });
            return distance;
        }

        private void getUser(final Context context, String userID) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users")
                    .child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    tvUser.setText(user.getName());
                    Picasso.with(context)
                            .load(user.getAvatar())
                            .transform(new CropCircleTransformation())
                            .into(ivUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_normal_new_feed, container, false);
        setupUI(view);
        loadData();

        return view;
    }

    private void checkConnection() {
        if(!NetworkConnectionSupport.isConnected()){
            Toast.makeText(getContext(), NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("post");
    }

    private void setupUI(View view) {
        rvNewFeed = view.findViewById(R.id.rv_newfeed);
        rvNewFeed.setHasFixedSize(true);
        ivLoading = (SpinKitView)view.findViewById(R.id.iv_loading);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
        rvNewFeed.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onResume() {
        firebaseRecyclerAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
