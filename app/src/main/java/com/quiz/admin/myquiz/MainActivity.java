package com.quiz.admin.myquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {
    TextView questionTextView, correctAnswersTextView, mistakesTextView;
    ImageView questionImageView;
    SharedPreferences spf;
    String storeMistCount, storeTopic;
    static boolean storeSound;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    ArrayList<QuestItem> questItems;
    Resources res;
    Animation shakeAnimation;
    Button answerButton1, answerButton2, answerButton3, answerButton4;
    public static MediaPlayer player;
    private InterstitialAd mInterstitialAd;


    Random r;
    int turn;
    static int correct, mistakes, size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-4499242905014001~1123419768");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4499242905014001/8658294850");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        correctAnswersTextView = (TextView) findViewById(R.id.correctAnswersTextView);
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        storeMistCount = spf.getString(getString(R.string.pref_key_mistcount), "3");
        storeTopic = spf.getString(getString(R.string.pref_key_topic), "1");
        storeSound = spf.getBoolean(getString(R.string.sound_preference), false);
        mistakesTextView = (TextView) findViewById(R.id.mistakesTextView);
        questionImageView = (ImageView) findViewById(R.id.questionImageView);
        answerButton1 = (Button) findViewById(R.id.answerButton1);
        answerButton2 = (Button) findViewById(R.id.answerButton2);
        answerButton3 = (Button) findViewById(R.id.answerButton3);
        answerButton4 = (Button) findViewById(R.id.answerButton4);
        questItems = new ArrayList<QuestItem>();
        res = getResources();
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        r = new Random();
        turn = 0;
        correct = 0; mistakes = 0;

        mDBHelper = new DatabaseHelper(this);

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                toResultActivity();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        //try {
            mDb = mDBHelper.getWritableDatabase();
        //} catch (SQLException mSQLException) {
            //throw mSQLException;
        //}
        loadQuestions(Integer.valueOf(storeTopic));

        size = questItems.size();
        Collections.shuffle(questItems);
        newQuestion(turn);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.answerButton1: checkAnswer(answerButton1, turn);
                        break;
                    case R.id.answerButton2: checkAnswer(answerButton2, turn);
                        break;
                    case R.id.answerButton3: checkAnswer(answerButton3, turn);
                        break;
                    case R.id.answerButton4: checkAnswer(answerButton4, turn);
                        break;
                }
            }
        };

        answerButton1.setOnClickListener(onClickListener);
        answerButton2.setOnClickListener(onClickListener);
        answerButton3.setOnClickListener(onClickListener);
        answerButton4.setOnClickListener(onClickListener);
    }

    ///////////////////////////////////Methods/////////////////////////////////////////////////////

    public void loadQuestions(int a){
        Cursor cursor;
        if (a == 1)cursor = mDb.rawQuery("SELECT * FROM bundesland", null);
        else if (a == 2)cursor = mDb.rawQuery("SELECT * FROM staedte", null);
        else if (a == 3)cursor = mDb.rawQuery("SELECT * FROM persons", null);
        else if (a == 4)cursor = mDb.rawQuery("SELECT * FROM places", null);
        else cursor = mDb.rawQuery("SELECT * FROM persons", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int str = res.getIdentifier(cursor.getString(1), "string", getPackageName());
                String topic = res.getString(str);
                //
                int img = res.getIdentifier(cursor.getString(2), "drawable", getPackageName());
                Drawable image = res.getDrawable(img);
                //
                String answer = cursor.getString(3);
                //
                String opt1 = cursor.getString(4);
                //
                String opt2 = cursor.getString(5);
                //
                String opt3 = cursor.getString(6);
                //
                QuestItem questItem = new QuestItem(topic, image, answer, opt1, opt2, opt3);
                questItems.add(questItem);
                cursor.moveToNext();
            }
            cursor.close();
    }

    public void newQuestion(int c){
        int num = r.nextInt(4) + 1;
        questionTextView.setText(questItems.get(c).getTopic());
        questionImageView.setImageDrawable(questItems.get(c).getImage());
        switch (num){
            case 1:answerButton1.setText(questItems.get(c).getAnswer());
                answerButton2.setText(questItems.get(c).getOpt1());
                answerButton3.setText(questItems.get(c).getOpt2());
                answerButton4.setText(questItems.get(c).getOpt3());
                break;
            case 2:answerButton1.setText(questItems.get(c).getOpt1());
                answerButton2.setText(questItems.get(c).getAnswer());
                answerButton3.setText(questItems.get(c).getOpt2());
                answerButton4.setText(questItems.get(c).getOpt3());
                break;
            case 3:answerButton1.setText(questItems.get(c).getOpt1());
                answerButton2.setText(questItems.get(c).getOpt2());
                answerButton3.setText(questItems.get(c).getAnswer());
                answerButton4.setText(questItems.get(c).getOpt3());
                break;
            case 4:answerButton1.setText(questItems.get(c).getOpt1());
                answerButton2.setText(questItems.get(c).getOpt2());
                answerButton3.setText(questItems.get(c).getOpt3());
                answerButton4.setText(questItems.get(c).getAnswer());
                break;
        }
    }

    public void checkAnswer(Button button, int c){
        boolean isCorrect = button.getText().toString().equalsIgnoreCase(questItems.get(c).getAnswer());
        final Button AllButtons[] = {answerButton1, answerButton2, answerButton3, answerButton4};
        Handler h = new Handler();
        if(isCorrect && turn < questItems.size()-1) {
            if(storeSound)
                playSound(R.raw.correct);
            for(Button b : AllButtons){
                b.setEnabled(false);
                b.setTextColor(Color.BLACK);
            }
            button.setTextColor(Color.GREEN);
            turn++;
            correct++;
            String str = res.getString(R.string.correct);
            correctAnswersTextView.setText(String.valueOf(str + " " + correct));
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    newQuestion(turn);
                    for(Button b : AllButtons){
                        b.setTextColor(Color.BLACK);
                        b.setEnabled(true);
                    }
                }
            }, 700);
        }
        else if (isCorrect && turn >= questItems.size()-1) {
            if(storeSound)
                playSound(R.raw.correct);
            correct++;
            if (mInterstitialAd.isLoaded())mInterstitialAd.show();
            else toResultActivity();
        }
        else{
            if(storeSound)
                playSound(R.raw.wrong);
            questionImageView.startAnimation(shakeAnimation);
            button.setEnabled(false);
            button.setTextColor(Color.RED);
            String str = res.getString(R.string.falsestr);
            mistakes++;
            mistakesTextView.setText(String.valueOf(str + " " + mistakes));
            if(mistakes >= Integer.valueOf(storeMistCount)){
                if (mInterstitialAd.isLoaded())mInterstitialAd.show();
                else toResultActivity();
            }
        }
    }

    public void toResultActivity() {
        questItems.clear();
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    public void onBackPressed(){
        questItems.clear();
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void playSound(int a){
        if(player != null){
            player.reset();
            player.release();
            player = null;
        }
        player = MediaPlayer.create(this, a);
        player.start();
    }

    static public int getMistakesCount(){return mistakes;}
    static public int getCorrectCount() {return correct;}
    static public int getQstSize() {return size;}
    static public boolean getSound() {return storeSound;}
}

