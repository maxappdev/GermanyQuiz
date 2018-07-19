package com.quiz.admin.myquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    TextView hsText;
    RatingBar hsBar;
    Button startButton, settingsButton;
    SharedPreferences spf;
    boolean storeSound;
    static int highScore;
    static SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        spf = PreferenceManager.getDefaultSharedPreferences(this);
        storeSound = spf.getBoolean(getString(R.string.sound_preference), false);
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);

        startButton = (Button) findViewById(R.id.startButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        hsText = (TextView) findViewById(R.id.hsText);
        hsBar = (RatingBar) findViewById(R.id.ratingBar);

        String str = getResources().getString(R.string.HSprog);
        hsText.setText(String.valueOf(str + " " + highScore + "%"));
        if(highScore == 0) hsBar.setRating((float) 0.5);
        else hsBar.setRating((float) highScore / 20);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeSound)
                    playSound(R.raw.click);
                toMainActivity();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeSound)
                    playSound(R.raw.click);
                toSettings();
            }
        });
    }

    private void toMainActivity(){
        RelativeLayout layout = new RelativeLayout(this);
        TextView tw =  new TextView(MenuActivity.this, null);
        String str = getResources().getString(R.string.loading);
        tw.setText(String.valueOf(str));
        tw.setTypeface(Typeface.DEFAULT_BOLD);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(tw, params);
        setContentView(layout);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void toSettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void playSound(int a){
        if(MainActivity.player != null){
            MainActivity.player.reset();
            MainActivity.player.release();
            MainActivity.player = null;
        }
        MainActivity.player = MediaPlayer.create(this, a);
        MainActivity.player.start();
    }
}
