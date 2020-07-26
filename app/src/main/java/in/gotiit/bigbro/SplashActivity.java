package in.gotiit.bigbro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import in.gotiit.bigbro.util.App;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        new Handler().postDelayed(() -> {
            if ( App.instance().getUser() != null) {
                MainActivity.start(SplashActivity.this);
            } else {
                LoginActivity.start(SplashActivity.this);
            }
            finish();
        }, SPLASH_DELAY);

    }

    @Override
    public void onBackPressed() {

    }
}
