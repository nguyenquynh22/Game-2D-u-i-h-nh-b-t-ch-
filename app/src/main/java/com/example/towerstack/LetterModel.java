package com.example.towerstack;

public class LetterModel {
    private String text;
    private int originalIndex;

    public LetterModel(String text, int originalIndex) {
        this.text = text;
        this.originalIndex = originalIndex;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getOriginalIndex() { return originalIndex; }
    public void setOriginalIndex(int originalIndex) { this.originalIndex = originalIndex; }
}