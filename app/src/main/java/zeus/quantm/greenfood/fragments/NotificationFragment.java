package zeus.quantm.greenfood.fragments;


import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.activities.FoodDetailActivity;
import zeus.quantm.greenfood.adapters.NotificationAdapter;
import zeus.quantm.greenfood.events.GetUserFromIDListener;
import zeus.quantm.greenfood.models.Notification;
import zeus.quantm.greenfood.models.Post;
import zeus.quantm.greenfood.models.User;
import zeus.quantm.greenfood.network.models.order.Order;
import zeus.quantm.greenfood.utils.MapsUltils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    FirebaseRecyclerAdapter<Order, NotificationViewHolder> firebaseRecyclerAdapter;

    public NotificationFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        setupUI(view);
        loadData();
        return view;
    }

    private void loadData() {
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "my UID : " + firebaseAuth.getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("history");
    }

    private void setupUI(View view) {
        toolbar = view.findViewById(R.id.tb_main);
        toolbar.setTitle("Notification");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        recyclerView = view.findViewById(R.id.rv_notification);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

//        List<Notification> notificationList = new ArrayList<>();
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        notificationList.add(new Notification("Title","http://media.phunutoday.vn/files/upload_images/2015/11/10/cach-lam-banh-da-cua-1-phunutoday_vn.jpg","This is description"));
//        NotificationAdapter notificationAdapter = new NotificationAdapter(notificationList,getContext());
//        recyclerView.setAdapter(notificationAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void onStart() {
        super.onStart();
        Query query = databaseReference.orderByChild("time");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, NotificationViewHolder>(
                Order.class,
                R.layout.item_notification,
                NotificationViewHolder.class,
                query
        ) {
            @Override
            public Order getItem(int position) {
                return super.getItem(getItemCount() - (position+1));
            }

            @Override
            protected void populateViewHolder(NotificationViewHolder viewHolder, Order model, int position) {
                Log.d(TAG, " my history " + model.getFoodImgLink());
                viewHolder.loadData(getContext(), model);

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        private ImageView ivNotification;
        private TextView tvTitle;
        private TextView tvDescription;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            setupUI();
        }

        private void setupUI() {
            ivNotification = itemView.findViewById(R.id.iv_notification);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        private void loadData(Context context, final Order order){
            //notification image loader
            Picasso.with(context)
                    .load(order.getFoodImgLink())
                    .into(ivNotification);
            if(order.getType().equals("order")){
                MapsUltils.getUser(context, order.getBuyerID(), new GetUserFromIDListener() {
                    @Override
                    public void getUserFromID(User user) {
                        tvTitle.setText(user.getName() + " order " +  + order.getQuantity() + " " + order.getFoodName());
                        tvDescription.setText(order.getTime());
                    }
                });
            } else {
                MapsUltils.getUser(context, order.getSellerID(), new GetUserFromIDListener() {
                    @Override
                    public void getUserFromID(User user) {
                        tvTitle.setText("Bạn đã order " + order.getQuantity() + " " + order.getFoodName() + " của " + user.getName());
                        tvDescription.setText(order.getTime());
                    }
                });
            }

        }
    }

    @Override
    public void onResume() {
        firebaseRecyclerAdapter.notifyDataSetChanged();
        super.onResume();
    }

}
