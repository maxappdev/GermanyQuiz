package com.quiz.admin.myquiz;

import android.graphics.drawable.Drawable;

public class QuestItem {
    String topic;
    Drawable image;
    String answer;
    String opt1;
    String opt2;
    String opt3;

    QuestItem(String topic, Drawable image, String answer, String opt1, String opt2, String opt3){
        this.topic = topic;
        this.image = image;
        this.answer = answer;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
    }

    public String getTopic(){return topic;}
    public Drawable getImage(){return image;}
    public String getAnswer(){return answer;}
    public String getOpt1(){return opt1;}
    public String getOpt2(){return opt2;}
    public String getOpt3(){return opt3; }
}
