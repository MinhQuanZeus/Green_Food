package zeus.quantm.greenfood.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import zeus.quantm.greenfood.fragments.FavoriteFragment;
import zeus.quantm.greenfood.fragments.MeFragment;
import zeus.quantm.greenfood.fragments.NewFeedFragment;
import zeus.quantm.greenfood.fragments.NotificationFragment;
import zeus.quantm.greenfood.managers.ScreenManager;
import zeus.quantm.greenfood.utils.BottomNavigationViewHelper;

import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.utils.Config;
import zeus.quantm.greenfood.utils.NotificationHandleUtils;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddFood;
    private FirebaseAuth firebaseAuth;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ScreenManager.openFragment(getSupportFragmentManager(),new NewFeedFragment(),R.id.content,false);
                    return true;
                case R.id.navigation_favorite:
                    ScreenManager.openFragment(getSupportFragmentManager(),new FavoriteFragment(),R.id.content,false);
                    return true;
                case R.id.navigation_fab:
                 //   mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_notifications:
                    ScreenManager.openFragment(getSupportFragmentManager(),new NotificationFragment(),R.id.content,false);
                    return true;
                case R.id.navigation_profile:
                    ScreenManager.openFragment(getSupportFragmentManager(),new MeFragment(),R.id.content,false);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d("Main","User ID: "+firebaseAuth.getCurrentUser().getUid());
    }

    private void setupUI() {
        fabAddFood = (FloatingActionButton) findViewById(R.id.fab_add_food);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fabAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, AddFoodActivity.class);
                startActivity(myIntent);
            }
        });
        ScreenManager.openFragment(getSupportFragmentManager(),new NewFeedFragment(),R.id.content,false);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() != 0){
            super.onBackPressed();
        } else {
            moveTaskToBack(true);
        }
    }
}
