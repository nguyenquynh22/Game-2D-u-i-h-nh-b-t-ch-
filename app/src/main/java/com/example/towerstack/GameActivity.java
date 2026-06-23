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

    private void hienThiResult() {
        lnResult.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        lnResult.setOrientation(LinearLayout.VERTICAL);

        LinearLayout currentLine = new LinearLayout(this);
        currentLine.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        currentLine.setGravity(android.view.Gravity.CENTER);
        currentLine.setOrientation(LinearLayout.HORIZONTAL);
        lnResult.addView(currentLine);

        for (int i = 0; i < arrResult.size(); i++) {
            LetterModel letter = arrResult.get(i);

            if (letter.getText().equals("SPACE")) {
                // 🌟 Gặp khoảng trắng -> Tạo một dòng mới tinh ném vào lnResult
                currentLine = new LinearLayout(this);
                currentLine.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                currentLine.setGravity(android.view.Gravity.CENTER);
                currentLine.setOrientation(LinearLayout.HORIZONTAL);
                currentLine.setPadding(0, 10, 0, 0); // Khoảng cách giữa dòng trên và dòng dưới
                lnResult.addView(currentLine);
                continue; // Bỏ qua không vẽ ô chữ cho dấu cách
            }

            View itemView = inflater.inflate(R.layout.item_result, currentLine, false);
            TextView tvResult = itemView.findViewById(R.id.tvResult);
            tvResult.setText(letter.getText());

            final int index = i;
            itemView.setOnClickListener(v -> {
                LetterModel cellClicked = arrResult.get(index);
                if (!cellClicked.getText().equals("")) {
                    int targetIndex = cellClicked.getOriginalIndex();

                    arrSuggest.get(targetIndex).setText(cellClicked.getText());
                    suggestAdapter.notifyDataSetChanged();
                    cellClicked.setText("");
                    cellClicked.setOriginalIndex(-1);

                    hienThiResult(); // Vẽ lại giao diện sau khi thu hồi chữ
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
            char c = dapan.charAt(i);
            if(c == ' '){
                arrResult.add(new LetterModel("SPACE", -2));
            } else{
                arrResult.add(new LetterModel("", -1));
            }
        }
    }

    private void checkResult() {
        // Kiểm tra xem người chơi điền đủ hết các ô chưa
        for (int i = 0; i < arrResult.size(); i++) {
            if (arrResult.get(i).getText().equals("")) {
                return; // Vẫn còn ô trống chưa điền
            }
        }

        StringBuilder s = new StringBuilder();
        for (LetterModel letter : arrResult) {
            if (letter.getText().equals("SPACE")) {
                s.append(" "); // Trả lại khoảng trắng
            } else {
                s.append(letter.getText());
            }
        }

        // So sánh (Xóa bỏ khoảng trắng thừa nếu có và so khớp không phân biệt hoa thường)
        if (s.toString().equalsIgnoreCase(dapan.replace(" ", ""))) {
            Toast.makeText(this, "You're right! Bạn giỏi quá 🎉", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi, chúc bạn may mắn lần sau!", Toast.LENGTH_SHORT).show();
        }
    }
}