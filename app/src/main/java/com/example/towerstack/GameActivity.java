package com.example.towerstack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.towerstack.Adapter.ResultAdapter;
import com.example.towerstack.Model.LetterModel;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    String dapan = "quạt quậy";
    ArrayList<LetterModel> arrResult;
    ArrayList<LetterModel> arrSuggest;
    LinearLayout lnResult;
    GridView gvSuggest;
    ResultAdapter suggestAdapter;
    ImageButton ibtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        init();
        anhXa();
        hashData();
        hienThiResult();
        hienThiSuggest();

        ibtnBack.setOnClickListener(v -> {
            finish();
        });

        gvSuggest.setOnItemClickListener((parent, view, position, id) -> {
            LetterModel selectedLetter = arrSuggest.get(position);
            if (selectedLetter.getText().isEmpty()) return;
            for (int i = 0; i < arrResult.size(); i++) {
                if (arrResult.get(i).getText().equals("")) {
                    arrResult.get(i).setText(selectedLetter.getText());
                    arrResult.get(i).setOriginalIndex(selectedLetter.getOriginalIndex());
                    selectedLetter.setText("");
                    suggestAdapter.notifyDataSetChanged();
                    break;
                }
            }
            hienThiResult();
            checkResult();
        });
    }

    private void anhXa() {
        lnResult = findViewById(R.id.lnResult);
        gvSuggest = findViewById(R.id.gvSuggest);
        ibtnBack = findViewById(R.id.ibtnBack);
    }

    private void init() {
        arrResult = new ArrayList<>();
        arrSuggest = new ArrayList<>();

        int tongSoO = 14;
        java.util.Random random = new java.util.Random();

        ArrayList<String> tempLetters = new ArrayList<>();

        String dapanNoSpace = dapan.replace(" ", "").toUpperCase();

        for (int i = 0; i < tongSoO; i++) {
            if (i < dapanNoSpace.length()) {
                tempLetters.add(String.valueOf(dapanNoSpace.charAt(i)));
            } else {
                int asciiCode = random.nextInt(90 - 65 + 1) + 65; // A-Z
                tempLetters.add(String.valueOf((char) asciiCode));
            }
        }

        java.util.Collections.shuffle(arrSuggest);

        for (int i = 0; i < tempLetters.size(); i++) {
            arrSuggest.add(new LetterModel(tempLetters.get(i), i));
        }
    }

    private void hashData() {
        arrResult.clear();
        String dapanNoSpace = dapan.replace(" ", "");
        for (int i = 0; i < dapanNoSpace.length(); i++) {
            arrResult.add(new LetterModel("", -1));
        }
    }

    private void hienThiResult() {
        lnResult.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        lnResult.setOrientation(LinearLayout.VERTICAL);

        String[] words = dapan.toUpperCase().split(" ");
        int globalIndex = 0;
        for (int w = 0; w < words.length; w++) {
            String currentWord = words[w];
            LinearLayout wordLine = new LinearLayout(this);
            wordLine.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            wordLine.setGravity(android.view.Gravity.CENTER);
            wordLine.setOrientation(LinearLayout.HORIZONTAL);
            if (w > 0) {
                wordLine.setPadding(0, 16, 0, 0);
            }
            lnResult.addView(wordLine);

            for (int i = 0; i < currentWord.length(); i++) {
                View itemView = inflater.inflate(R.layout.item_result, wordLine, false);
                TextView tvResult = itemView.findViewById(R.id.tvResult);

                tvResult.setText(arrResult.get(globalIndex).getText());

                final int targetIndex = globalIndex;
                itemView.setOnClickListener(v -> {
                    LetterModel cellClicked = arrResult.get(targetIndex);
                    if (!cellClicked.getText().equals("")) {
                        int originalSuggestIndex = cellClicked.getOriginalIndex();

                        arrSuggest.get(originalSuggestIndex).setText(cellClicked.getText());
                        suggestAdapter.notifyDataSetChanged();
                        cellClicked.setText("");
                        cellClicked.setOriginalIndex(-1);

                        hienThiResult();
                    }
                });
                wordLine.addView(itemView);
                globalIndex++;
            }
        }
    }

    private void hienThiSuggest() {
        gvSuggest.setNumColumns(7);
        int density = (int) getResources().getDisplayMetrics().density;
        gvSuggest.setColumnWidth(42 * density);
        suggestAdapter = new ResultAdapter(this, 0, arrSuggest);
        gvSuggest.setAdapter(suggestAdapter);
    }


    private void checkResult() {
        // Kiểm tra xem người chơi điền đủ hết các ô chưa
        for (int i = 0; i < arrResult.size(); i++) {
            if (arrResult.get(i).getText().equals("")) {
                return;
            }
        }

        StringBuilder s = new StringBuilder();
        for (LetterModel letter : arrResult) {
            s.append(letter.getText());
        }
        String cleanDapan = dapan.replace(" ", "").toUpperCase();

        if (s.toString().equalsIgnoreCase(cleanDapan)) {
            Toast.makeText(this, "You're right! Bạn giỏi quá 🎉", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi, chúc bạn may mắn lần sau!", Toast.LENGTH_SHORT).show();
        }
    }
}