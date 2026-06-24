package com.example.towerstack.Model;

public class QuestionModel {
    private int level;
    private String resourceImg;
    private String answer;
    private int rewardCoin;

    public QuestionModel(int level, String resourceImg, String answer, int rewardCoin) {
        this.level = level;
        this.resourceImg = resourceImg;
        this.answer = (answer != null) ? answer.toUpperCase() : "";
        this.rewardCoin = rewardCoin;
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

    public int getRewardCoin() {
        return rewardCoin;
    }

    public void setRewardCoin(int rewardCoin) {
        this.rewardCoin = rewardCoin;
    }
}