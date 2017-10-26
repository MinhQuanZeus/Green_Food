package zeus.quantm.greenfood.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zeus.quantm.greenfood.events.GetUserFromIDListener;
import zeus.quantm.greenfood.models.User;
import zeus.quantm.greenfood.network.services.GetDistanceService;
import zeus.quantm.greenfood.network.RetrofitFactory;
import zeus.quantm.greenfood.network.models.distance.MainObject;

import static android.content.ContentValues.TAG;

/**
 * Created by QuanT on 6/29/2017.
 */

public class MapsUltils {

    public static String getDistanceFromLocation(String current, final String destinate, final TextView textView){
        final String[] distance = new String[1];
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
                                    distance[0] = response.body()
                                            .getRows().get(0)
                                            .getElements().get(0)
                                            .getDistance()
                                            .getText();
                                } else distance[0] = "";
                            } else distance[0] = "";
                            textView.setText(LibrarySupportManager.getInstance().distanceFromLocationFormat(distance[0]));
                        }
                        Log.d(TAG,"distance : "+ distance[0]);
                    }

                    @Override
                    public void onFailure(Call<MainObject> call, Throwable t) {

                    }
                });
        return distance[0];
    }

    public static void getUser(final Context context, String userID, final GetUserFromIDListener getUserFromIDListener) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        getUserFromIDListener.getUserFromID(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
