package com.example.towerstack;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.towerstack.Adapter.ResultAdapter;
import com.example.towerstack.Database.DatabaseHelper;
import com.example.towerstack.Model.LetterModel;
import com.example.towerstack.Model.QuestionModel;
import com.example.towerstack.Model.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    String dapan = "";
    String hinhanh = "";
    int currentLevel = 1;
    int rewardCoin = 10;
    int hintCount = 0;

    ArrayList<LetterModel> arrResult;
    ArrayList<LetterModel> arrSuggest;
    LinearLayout lnResult;
    GridView gvSuggest;
    ResultAdapter suggestAdapter;
    ImageButton ibtnBack;

    ImageView imgQuestion;
    TextView tvLevel;
    Button btnScore;
    Button btnGoiY, btnBoQua;

    DatabaseHelper databaseHelper;
    UserModel userModel;

    MediaPlayer musicPlayer;
    MediaPlayer coinPlayer;
    MediaPlayer failPlayer;

    boolean isSoundOn = true;
    boolean isVibrateOn = true;
    Vibrator vibrator;
    boolean isTransitioning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        databaseHelper = new DatabaseHelper(this);

        taoAmThanh();
        kiemTraCaiDatAmThanh();
        taoRung();
        kiemTraCaiDatRung();

        anhXa();
        loadGameData();

        ibtnBack.setOnClickListener(v -> finish());

        gvSuggest.setOnItemClickListener((parent, view, position, id) -> {
            if (isTransitioning) return;

            LetterModel selectedLetter = arrSuggest.get(position);
            if (selectedLetter.getText().isEmpty()) return;

            boolean daDienPhanTu = false;
            for (int i = 0; i < arrResult.size(); i++) {
                if (arrResult.get(i).getText().isEmpty()) {
                    arrResult.get(i).setText(selectedLetter.getText());
                    arrResult.get(i).setOriginalIndex(selectedLetter.getOriginalIndex());
                    selectedLetter.setText("");
                    daDienPhanTu = true;
                    break;
                }
            }

            if (daDienPhanTu) {
                suggestAdapter.notifyDataSetChanged();
                hienThiResult();
                checkResult();
            }
        });

        btnGoiY.setOnClickListener(v -> xuLyGoiY());
        btnBoQua.setOnClickListener(v -> xuLyBoQua());
    }

    private void anhXa() {
        lnResult = findViewById(R.id.lnResult);
        gvSuggest = findViewById(R.id.gvSuggest);
        ibtnBack = findViewById(R.id.ibtnBack);
        imgQuestion = findViewById(R.id.imgQuestion);
        tvLevel = findViewById(R.id.tvLevel);
        btnScore = findViewById(R.id.btnScore);
        btnGoiY = findViewById(R.id.btnGoiY);
        btnBoQua = findViewById(R.id.btnBoQua);
    }

    private void loadGameData() {
        userModel = databaseHelper.getUserInfo();
        if (userModel == null) return;

        currentLevel = userModel.getCurrentLevel();
        btnScore.setText(String.valueOf(userModel.getTotalCoin()));
        tvLevel.setText(String.valueOf(currentLevel));
        hintCount = 0;
        btnGoiY.setEnabled(true);
        isTransitioning = false;

        QuestionModel question = databaseHelper.getQuestionByLevel(currentLevel);
        if (question != null) {
            dapan = question.getAnswer().toLowerCase().trim();
            hinhanh = question.getResourceImg();
            rewardCoin = question.getRewardCoin();

            int resId = getResources().getIdentifier(hinhanh, "drawable", getPackageName());
            if (resId != 0) {
                imgQuestion.setImageResource(resId);
            } else {
                imgQuestion.setImageResource(R.drawable.oanquan);
            }

            init();
            hashData();
            hienThiResult();
            hienThiSuggest();
        } else {
            Toast.makeText(this, "Bạn đã chơi hết level hiện có. Chờ NSX cập nhật thêm! 🎉", Toast.LENGTH_LONG).show();
            btnGoiY.setEnabled(false);
            btnBoQua.setEnabled(false);
            gvSuggest.setVisibility(View.GONE);
        }
    }

    private void init() {
        arrResult = new ArrayList<>();
        arrSuggest = new ArrayList<>();

        int tongSoO = 14;
        Random random = new Random();
        ArrayList<String> tempLetters = new ArrayList<>();

        String dapanNoSpace = dapan.replace(" ", "").toUpperCase();

        for (int i = 0; i < dapanNoSpace.length(); i++) {
            tempLetters.add(String.valueOf(dapanNoSpace.charAt(i)));
        }

        while (tempLetters.size() < tongSoO) {
            int asciiCode = random.nextInt(90 - 65 + 1) + 65; // A-Z
            String chuNgauNhien = String.valueOf((char) asciiCode);
            tempLetters.add(chuNgauNhien);
        }

        Collections.shuffle(tempLetters);

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

        String[] words = dapan.toUpperCase().split(" ");
        int globalIndex = 0;

        for (int w = 0; w < words.length; w++) {
            String currentWord = words[w];
            if (currentWord.isEmpty()) continue;

            LinearLayout wordLine = new LinearLayout(this);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (w > 0) {
                lineParams.setMargins(0, 16, 0, 0); // Khoảng cách giữa các hàng chữ
            }
            wordLine.setLayoutParams(lineParams);
            wordLine.setGravity(android.view.Gravity.CENTER);
            wordLine.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < currentWord.length(); i++) {
                View itemView = inflater.inflate(R.layout.item_result, wordLine, false);
                TextView tvResult = itemView.findViewById(R.id.tvResult);

                if (globalIndex < arrResult.size()) {
                    String textInCell = arrResult.get(globalIndex).getText();
                    tvResult.setText(textInCell);
                }

                final int targetIndex = globalIndex;
                itemView.setOnClickListener(v -> {
                    if (isTransitioning) return;

                    LetterModel cellClicked = arrResult.get(targetIndex);
                    if (!cellClicked.getText().isEmpty()) {
                        int originalSuggestIndex = cellClicked.getOriginalIndex();

                        // Trả chữ về lại danh sách gợi ý bên dưới nếu bấm hủy
                        if (originalSuggestIndex >= 0 && originalSuggestIndex < arrSuggest.size()) {
                            arrSuggest.get(originalSuggestIndex).setText(cellClicked.getText());
                            suggestAdapter.notifyDataSetChanged();
                        }
                        cellClicked.setText("");
                        cellClicked.setOriginalIndex(-1);

                        hienThiResult();
                    }
                });
                wordLine.addView(itemView);
                globalIndex++;
            }
            lnResult.addView(wordLine);
        }
        lnResult.requestLayout();
        lnResult.invalidate();
    }

    private void hienThiSuggest() {
        gvSuggest.setNumColumns(7);
        int density = (int) getResources().getDisplayMetrics().density;
        gvSuggest.setColumnWidth(42 * density);
        suggestAdapter = new ResultAdapter(this, 0, arrSuggest);
        gvSuggest.setAdapter(suggestAdapter);
    }

    private void checkResult() {
        for (int i = 0; i < arrResult.size(); i++) {
            if (arrResult.get(i).getText().isEmpty()) {
                return;
            }
        }

        StringBuilder s = new StringBuilder();
        for (LetterModel letter : arrResult) {
            s.append(letter.getText());
        }
        String cleanDapan = dapan.replace(" ", "").toUpperCase();

        if (s.toString().equalsIgnoreCase(cleanDapan)) {
            isTransitioning = true;
            phatAmThanh(coinPlayer);
            rung(120);
            Toast.makeText(this, "You're right! Bạn giỏi quá 🎉", Toast.LENGTH_SHORT).show();
            databaseHelper.updateScoreAndLevel(currentLevel + 1, rewardCoin);
            new Handler().postDelayed(() -> loadGameData(), 2000);
        } else {
            rung(400);
            phatAmThanh(failPlayer);
            Toast.makeText(this, "Sai rồi, hãy kiểm tra lại các ký tự!", Toast.LENGTH_SHORT).show();
        }
    }

    private void xuLyGoiY() {
        if (isTransitioning) return;
        userModel = databaseHelper.getUserInfo();
        if (userModel == null) return;

        if (userModel.getTotalCoin() < 30) {
            Toast.makeText(this, "Không đủ 30 xu để nhận gợi ý!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dapanNoSpace = dapan.replace(" ", "").toUpperCase();
        int targetIndex = -1;
        for (int i = 0; i < arrResult.size(); i++) {
            if (arrResult.get(i).getText().isEmpty()) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            Toast.makeText(this, "Vui lòng thu hồi bớt ô chữ để lấy chỗ điền gợi ý!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.updateScoreAndLevel(currentLevel, -30);

        userModel = databaseHelper.getUserInfo();
        btnScore.setText(String.valueOf(userModel.getTotalCoin()));

        String chuCaiDung = String.valueOf(dapanNoSpace.charAt(targetIndex));
        arrResult.get(targetIndex).setText(chuCaiDung);
        arrResult.get(targetIndex).setOriginalIndex(-2);

        for (int j = 0; j < arrSuggest.size(); j++) {
            if (arrSuggest.get(j).getText().equalsIgnoreCase(chuCaiDung)) {
                arrSuggest.get(j).setText("");
                break;
            }
        }

        suggestAdapter.notifyDataSetChanged();
        hienThiResult();

        hintCount++;
        if (hintCount >= 2) {
            btnGoiY.setEnabled(false);
            Toast.makeText(this, "Đã hết lượt gợi ý cho màn này!", Toast.LENGTH_SHORT).show();
        }

        checkResult();
    }

    private void xuLyBoQua() {
        if (isTransitioning) return;
        userModel = databaseHelper.getUserInfo();
        if (userModel == null) return;

        if (userModel.getTotalCoin() < 50) {
            Toast.makeText(this, "Không đủ 50 xu để bỏ qua màn chơi!", Toast.LENGTH_SHORT).show();
            return;
        }

        isTransitioning = true;
        databaseHelper.updateScoreAndLevel(currentLevel + 1, -50);
        Toast.makeText(this, "Đã bỏ qua màn chơi này!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> loadGameData(), 1000);
    }

    // Các hàm phụ trợ âm thanh/rung giữ nguyên...
    private void taoAmThanh() {
        musicPlayer = MediaPlayer.create(this, R.raw.music);
        coinPlayer = MediaPlayer.create(this, R.raw.coin);
        failPlayer = MediaPlayer.create(this, R.raw.fail);
        if (musicPlayer != null) { musicPlayer.setLooping(true); musicPlayer.setVolume(0.5f, 0.5f); }
    }
    private void kiemTraCaiDatAmThanh() {
        userModel = databaseHelper.getUserInfo();
        isSoundOn = userModel == null || userModel.getIsSound() == 1;
        if (isSoundOn) batNhacNen(); else tatNhacNen();
    }
    private void batNhacNen() { if (musicPlayer != null && !musicPlayer.isPlaying()) musicPlayer.start(); }
    private void tatNhacNen() { if (musicPlayer != null && musicPlayer.isPlaying()) { musicPlayer.pause(); musicPlayer.seekTo(0); } }
    private void phatAmThanh(MediaPlayer mediaPlayer) {
        if (!isSoundOn || mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) { mediaPlayer.pause(); mediaPlayer.seekTo(0); }
        mediaPlayer.start();
    }
    @Override protected void onResume() { super.onResume(); kiemTraCaiDatAmThanh(); kiemTraCaiDatRung(); }
    @Override protected void onPause() { super.onPause(); if (musicPlayer != null && musicPlayer.isPlaying()) musicPlayer.pause(); }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) { musicPlayer.release(); musicPlayer = null; }
        if (coinPlayer != null) { coinPlayer.release(); coinPlayer = null; }
        if (failPlayer != null) { failPlayer.release(); failPlayer = null; }
    }
    private void taoRung() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (vm != null) vibrator = vm.getDefaultVibrator();
        } else { vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); }
    }
    private void kiemTraCaiDatRung() { userModel = databaseHelper.getUserInfo(); isVibrateOn = userModel == null || userModel.getIsVibrate() == 1; }
    private void rung(long t) {
        if (!isVibrateOn || vibrator == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(t, VibrationEffect.DEFAULT_AMPLITUDE));
        else vibrator.vibrate(t);
    }
}