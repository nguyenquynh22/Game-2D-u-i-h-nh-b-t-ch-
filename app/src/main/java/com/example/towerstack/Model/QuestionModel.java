package com.example.towerstack.Model;

public class QuestionModel {
    private int level;
    private String resourceImg;
    private String answer;


    public QuestionModel(int level, String resourceImg, String answer) {
        this.level = level;
        this.resourceImg = resourceImg;
        this.answer = answer.toUpperCase();
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getResourceImg() {
        return resourceImg;
    }

    public void setResourceImg(String resourceImg) {
        this.resourceImg = resourceImg;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
