package com.example.dell.newapp;

/**
 * Created by Dell on 9/2/2017.
 */

public class questions {

    private String question, image, name, date_time;

    public questions() {
    }

    public questions(String question, String image, String name, String date_time) {
        this.question = question;
        this.image = image;
        this.name = name;
        this.date_time = date_time;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}