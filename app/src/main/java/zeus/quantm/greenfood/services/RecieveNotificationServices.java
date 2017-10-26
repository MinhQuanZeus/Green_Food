package zeus.quantm.greenfood.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.activities.LoginRegisterActivity;
import zeus.quantm.greenfood.activities.MainActivity;
import zeus.quantm.greenfood.network.models.order.Order;
import zeus.quantm.greenfood.utils.LibrarySupportManager;

/**
 * Created by QuanT on 7/18/2017.
 */

public class RecieveNotificationServices extends FirebaseMessagingService {
    private static final String TAG = RecieveNotificationServices.class.getSimpleName();
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;
    private static int x=0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,"From: "+remoteMessage.getFrom());
        if (remoteMessage.getNotification().getBody().length() > 0) {
            Log.d(TAG, "foreground notification : " + remoteMessage.getNotification().getBody());
        }
        Gson gSon = new Gson();
        Order orderResponse = gSon.fromJson(remoteMessage.getNotification().getBody(), Order.class);
        if (orderResponse != null){
            Log.d(TAG, "Parse OK : " + orderResponse.toString());
            applyOrder(orderResponse);
        }


//        Map<String, String> data = remoteMessage.getData();
//        if (data == null) {
//            return;
//        }
//        Order order = new Order(
//                data.get("buyer_id"),
//                data.get("buyer_name"),
//                data.get("check_id"),
//                data.get("food_name"),
//                data.get("image_food"),
//                Long.parseLong(data.get("quantity")),
//                "orders",
//                data.get("time"));
//        if(order != null){
//            Log.d(TAG, "order test : " + order.toString());
//            applyOrder(order);
//        }
//        Log.d(TAG,"work : ");
    }

    private void applyOrder(Order order) {
        //1. Send to firebase
        //create order list in seller
//        String orderKey = mDatabaseReference.push().getKey();
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(order.getSellerID())
                .child("history");
        DatabaseReference newPost = mDatabaseReference.push();
        newPost.child("type").setValue("order");
        newPost.child("buyerID").setValue(order.getBuyerID());
        newPost.child("quantity").setValue(order.getQuantity());
        newPost.child("foodName").setValue(order.getFoodName());
        newPost.child("foodImgLink").setValue(order.getFoodImgLink());
        newPost.child("time").setValue(order.getTime());
        newPost.child("status").setValue("Unchecked");



        //2. Notification
        String title = order.getBuyerName() + " orders " + order.getQuantity() + " " + order.getFoodName();
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Intent rejectIntent = new Intent(getBaseContext(), MainActivity.class);
        rejectIntent.putExtra("order_key", mDatabaseReference.getRef().getKey());
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_android_black_24dp)
                        .setLargeIcon(LibrarySupportManager.getInstance().getBitmapFromURL(order.getFoodImgLink()))
                        .setContentTitle(title)
                        .setContentText(order.getTime())
                        .addAction(R.drawable.ic_done_black_24dp, "Đồng ý", resultPendingIntent)
                        .addAction(R.drawable.ic_close_black_24dp, "Huỷ bỏ", rejectPendingIntent);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100+x, mBuilder.build());
        x++;

    }



}

