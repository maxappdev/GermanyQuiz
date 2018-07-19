package com.quiz.admin.myquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    TextView scoreTextView, noteTextView, highScoreView;
    Button newGameButton, toMenuButton;
    static int questNumber, procNumber;
    Resources res;
    SharedPreferences spf;
    boolean storeSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
        highScoreView = (TextView) findViewById(R.id.highScoreView);
        newGameButton = (Button) findViewById(R.id.newGameButton);
        toMenuButton = (Button) findViewById(R.id.toMenuButton);
        questNumber = MainActivity.getCorrectCount() + MainActivity.getMistakesCount();
        procNumber = (int)((double)(MainActivity.getCorrectCount()) / (double)(questNumber) * 100);
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        res = getResources();
        storeSound = spf.getBoolean(getString(R.string.sound_preference), false);
        scoreTextView.setText(String.valueOf(procNumber + "%"));
        setNoteText();
        setHighScore();

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeSound)
                    playSound(R.raw.click);
                toMainActivity();
            }
        });
        toMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeSound)
                    playSound(R.raw.click);
                toMenuActivity();
            }
        });
    }

    /////////////////////////// Methods///////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        toMenuActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toSettings();
        return true;
    }

    public void setNoteText() {
        String best = res.getString(R.string.best), nullquest = res.getString(R.string.nullquest),
                onequest = res.getString(R.string.onequest), toomanypartone = res.getString(R.string.toomanypartone),
                toomanyparttwo = res.getString(R.string.toomanyparttwo);
        if(MainActivity.getCorrectCount() == MainActivity.getQstSize()) noteTextView.setText(String.valueOf(best));
        else if (MainActivity.getCorrectCount() == 0) noteTextView.setText(String.valueOf(nullquest));
        else if (MainActivity.getCorrectCount() == 1) noteTextView.setText(String.valueOf(onequest));
        else noteTextView.setText(String.valueOf(toomanypartone + " " + MainActivity.getCorrectCount() + " " + toomanyparttwo));
    }

    public void setHighScore(){
        if(procNumber > MenuActivity.highScore){
            String str = res.getString(R.string.newHS);
            highScoreView.setText(String.valueOf(str + " " + procNumber + "%"));
            SharedPreferences.Editor editor = MenuActivity.settings.edit();
            editor.putInt("HIGH_SCORE", procNumber);
            editor.apply();
        }
        else{
            String str = res.getString(R.string.HSprog);
            highScoreView.setText(String.valueOf(str + " " + MenuActivity.highScore + "%"));
        }
    }

    public void toMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void toMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
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
