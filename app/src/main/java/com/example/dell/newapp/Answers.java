package com.example.dell.newapp;

/**
 * Created by Dell on 9/4/2017.
 */

public class Answers {
    private String name,date_time,image,answer,question_key;

    public Answers() {
    }

    public Answers(String name, String date_time, String image, String answer,String question_key) {
        this.name = name;
        this.date_time = date_time;
        this.image = image;
        this.answer = answer;
        this.question_key = question_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setQuestion_key(String question_key) {
        this.question_key = question_key;
    }

    public String getQuestion_key() {

        return question_key;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
