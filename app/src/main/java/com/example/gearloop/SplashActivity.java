
package com.example.gearloop;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(0xFF061A3C);
        getWindow().setNavigationBarColor(0xFF1976D2);

        View content = findViewById(R.id.splashContent);
        View logo = findViewById(R.id.logoContainer);

        content.setAlpha(0f);
        logo.setScaleX(0.75f);
        logo.setScaleY(0.75f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                content, "alpha", 0f, 1f
        );

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                logo, "scaleX", 0.75f, 1f
        );

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                logo, "scaleY", 0.75f, 1f
        );

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(fadeIn, scaleX, scaleY);
        animation.setDuration(950);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        handler.postDelayed(() -> {
            Intent intent = new Intent(
                    SplashActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
