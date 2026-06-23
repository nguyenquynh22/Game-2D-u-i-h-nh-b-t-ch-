package com.example.towerstack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
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

        gvSuggest.setOnItemClickListener((parent, view, position, id) -> {
            LetterModel selectedLetter = arrSuggest.get(position);
            if (selectedLetter.equals("")) return;
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
    }

    private void init() {
        arrResult = new ArrayList<>();
        arrSuggest = new ArrayList<>();

        int tongSoO = 14;
        java.util.Random random = new java.util.Random();

        ArrayList<String> tempLetters = new ArrayList<>();

        for (int i = 0; i < tongSoO; i++) {
            if (i < dapan.length()) {
                tempLetters.add(String.valueOf(dapan.charAt(i)).toUpperCase());
            } else {
                int asciiCode = random.nextInt(90 - 65 + 1) + 65;
                tempLetters.add(String.valueOf((char) asciiCode));
            }
        }

        java.util.Collections.shuffle(arrSuggest);

        for (int i = 0; i < tempLetters.size(); i++) {
            arrSuggest.add(new LetterModel(tempLetters.get(i), i));
        }
    }

    private void hienThiResult() {
        lnResult.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < arrResult.size(); i++) {
            View itemView = inflater.inflate(R.layout.item_result, lnResult, false);
            TextView tvResult = itemView.findViewById(R.id.tvResult);
            tvResult.setText(arrResult.get(i).getText());

            final int index = i;
            itemView.setOnClickListener(v -> {
                LetterModel cellClicked = arrResult.get(index);
                if (!cellClicked.getText().equals("")) {
                    int targetIndex = cellClicked.getOriginalIndex();

                    arrSuggest.get(targetIndex).setText(cellClicked.getText());
                    suggestAdapter.notifyDataSetChanged();
                    cellClicked.setText("");
                    cellClicked.setOriginalIndex(-1);

                    hienThiResult();
                }
            });
            lnResult.addView(itemView);
        }
    }

    private void hienThiSuggest() {
        gvSuggest.setNumColumns(7);
        int density = (int) getResources().getDisplayMetrics().density;
        gvSuggest.setColumnWidth(42 * density);
        suggestAdapter = new ResultAdapter(this, 0, arrSuggest);
        gvSuggest.setAdapter(suggestAdapter);
    }

    private void hashData() {
        arrResult.clear();
        for (int i = 0; i < dapan.length(); i++) {
            arrResult.add(new LetterModel("", -1));
        }
    }

    private void checkResult() {
        for (int i = 0; i < arrResult.size(); i++) {
            if (arrResult.get(i).getText().equals("")) {
                return;
            }
        }
        StringBuilder s = new StringBuilder();
        for (LetterModel letter : arrResult) {
            s.append(letter.getText());
        }
        if (s.toString().equalsIgnoreCase(dapan)) {
            Toast.makeText(this, "You're right! Bạn giỏi quá 🎉", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi, chúc bạn may mắn lần sau!", Toast.LENGTH_SHORT).show();
        }
    }
}