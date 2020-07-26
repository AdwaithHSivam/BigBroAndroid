package in.gotiit.bigbro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import in.gotiit.bigbro.util.User;
import in.gotiit.bigbro.util.MyCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEt;
    private EditText usernameEt;
    private TextView btnLogin;
    private ProgressBar mProgress;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(20f);
        }

        usernameEt = findViewById(R.id.et_username);
        passwordEt = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.tv_btn_login);
        mProgress = findViewById(R.id.login_progress);
        mProgress.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> {
            final String password = passwordEt.getText().toString();
            final String username = usernameEt.getText().toString();
            if (username.length() < 3) {
                usernameEt.setError("Username too short");
            } else {
                btnLogin.setClickable(false);
                mProgress.setVisibility(View.VISIBLE);
                User.validate(username, password, new MyCallback() {
                    @Override
                    public void OnFailure(String errorMessage) {
                        btnLogin.setClickable(true);
                        mProgress.setVisibility(View.GONE);
                        Snackbar.make(findViewById(R.id.layout_root),
                                errorMessage, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnSuccess() {
                        btnLogin.setClickable(true);
                        mProgress.setVisibility(View.GONE);
                        Snackbar.make(findViewById(R.id.layout_root),
                                "Login Successful", Snackbar.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            MainActivity.start(LoginActivity.this);
                            finish();
                        }, 100);
                    }

                });
            }

        });
    }


}

