package io.github.abhishekwl.flavradminprimary.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.abhishekwl.flavradminprimary.R;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashAppLogoImageView) ImageView appLogoImageView;

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    private final int SPLASH_DELAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_splash);

        unbinder = ButterKnife.bind(SplashActivity.this);
        initializeViews();
    }

    private void initializeViews() {
        Glide.with(getApplicationContext()).load(R.drawable.logo_white).into(appLogoImageView);
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser()==null) startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                else startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_DELAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
