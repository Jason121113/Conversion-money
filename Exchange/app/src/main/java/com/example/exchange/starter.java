package com.example.exchange;

import androidx.appcompat.app.AppCompatActivity;

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

public class starter extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    //Variables
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.starter);

        //Animation

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks

        image = findViewById(R.id.image);
        textView1 = findViewById(R.id.textView1);

        image.setAnimation(topAnim);
        textView1.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(starter.this, Login.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(image, "logo_image");
                pairs[1] = new Pair<View,String>(textView1, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(starter.this, pairs);
                startActivity(intent,options.toBundle());

            }
        },SPLASH_SCREEN);
    }
}
