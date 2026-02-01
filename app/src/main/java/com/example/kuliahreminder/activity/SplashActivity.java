package com.example.kuliahreminder.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppName, tvTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        ivLogo = findViewById(R.id.iv_logo);
        tvAppName = findViewById(R.id.tv_app_name);
        tvTagline = findViewById(R.id.tv_tagline);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Start animations
        startAnimations();
    }

    private void startAnimations() {
        // Fade in animation untuk logo
        ivLogo.setAlpha(0f);
        ivLogo.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null);

        // Slide up animation untuk app name
        tvAppName.setTranslationY(50f);
        tvAppName.setAlpha(0f);
        tvAppName.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(800)
                .setListener(null);

        // Fade in animation untuk tagline
        tvTagline.setAlpha(0f);
        tvTagline.animate()
                .alpha(1f)
                .setStartDelay(1000)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Setelah animasi selesai, pindah ke activity berikutnya
                        navigateToNextActivity();
                    }
                });
    }

    private void navigateToNextActivity() {
        // Delay sebentar sebelum pindah
        ivLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashActivity.this);

                Intent intent;
                if (sessionManager.isLoggedIn()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 500);
    }
}