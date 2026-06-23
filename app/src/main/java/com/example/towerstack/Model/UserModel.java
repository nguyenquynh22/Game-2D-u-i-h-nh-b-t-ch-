package com.example.towerstack.Model;

public class UserModel {
    private int userId;
    private String playerName;
    private int currentLevel;
    private int totalCoin;
    private int isSound;
    private int isVibrate;

    public UserModel() {

    }

    public UserModel(int userId, String playerName, int currentLevel, int totalCoin, int isSound, int isVibrate) {
        this.userId = userId;
        this.playerName = playerName;
        this.currentLevel = currentLevel;
        this.totalCoin = totalCoin;
        this.isSound = isSound;
        this.isVibrate = isVibrate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(int totalCoin) {
        this.totalCoin = totalCoin;
    }

    public int getIsSound() {
        return isSound;
    }

    public void setIsSound(int isSound) {
        this.isSound = isSound;
    }

    public int getIsVibrate() {
        return isVibrate;
    }

    public void setIsVibrate(int isVibrate) {
        this.isVibrate = isVibrate;
    }
}
