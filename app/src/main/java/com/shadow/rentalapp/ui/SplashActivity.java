package com.shadow.rentalapp.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shadow.rentalapp.R;

public class SplashActivity extends AppCompatActivity {

    private TextView welcomeNameTv;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        //Animations
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        logoImageView = findViewById(R.id.logo_image_iv);
        welcomeNameTv = findViewById(R.id.welcome_name_tv);
        TextView sloganTv = findViewById(R.id.slogan_tv);


        logoImageView.setAnimation(topAnim);
        welcomeNameTv.setAnimation(bottomAnim);
        sloganTv.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(this, LoginActivity.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(logoImageView, "logo_image");
            pairs[1] = new Pair<View, String>(welcomeNameTv, "logo_name");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, options.toBundle());
            finish();

        }, 3000);


    }
}
