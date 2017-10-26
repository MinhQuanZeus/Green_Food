package zeus.quantm.greenfood.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.fragments.LoginFragment;
import zeus.quantm.greenfood.fragments.NewFeedFragment;
import zeus.quantm.greenfood.managers.ScreenManager;

public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ScreenManager.openFragment(getSupportFragmentManager(),new LoginFragment(),R.id.content,false);
    }
}
