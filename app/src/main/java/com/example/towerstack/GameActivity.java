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
import android.media.MediaPlayer;

import com.example.towerstack.Database.DatabaseHelper;
import com.example.towerstack.Model.UserModel;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

public class GameActivity extends AppCompatActivity {
    String dapan = "quạt quậy";
    ArrayList<LetterModel> arrResult;
    ArrayList<LetterModel> arrSuggest;
    LinearLayout lnResult;
    GridView gvSuggest;
    ResultAdapter suggestAdapter;
    ImageButton ibtnBack;
    DatabaseHelper databaseHelper;
    UserModel userModel;

    MediaPlayer musicPlayer;
    MediaPlayer coinPlayer;
    MediaPlayer failPlayer;

    boolean isSoundOn = true;
    boolean isVibrateOn = true;
    Vibrator vibrator;

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
            phatAmThanh(coinPlayer);
            rung(120);
            Toast.makeText(this, "You're right! Bạn giỏi quá 🎉", Toast.LENGTH_SHORT).show();
        } else {
            rung(400);
            phatAmThanh(failPlayer);
            Toast.makeText(this, "Sai rồi, chúc bạn may mắn lần sau!", Toast.LENGTH_SHORT).show();
        }
    }

    private void taoAmThanh() {
        musicPlayer = MediaPlayer.create(this, R.raw.music);
        coinPlayer = MediaPlayer.create(this, R.raw.coin);
        failPlayer = MediaPlayer.create(this, R.raw.fail);

        if (musicPlayer != null) {
            musicPlayer.setLooping(true);
            musicPlayer.setVolume(0.5f, 0.5f);
        }
    }

    private void kiemTraCaiDatAmThanh() {
        userModel = databaseHelper.getUserInfo();

        if (userModel != null) {
            isSoundOn = userModel.getIsSound() == 1;
        } else {
            isSoundOn = true;
        }

        if (isSoundOn) {
            batNhacNen();
        } else {
            tatNhacNen();
        }
    }

    private void batNhacNen() {
        if (musicPlayer != null && !musicPlayer.isPlaying()) {
            musicPlayer.start();
        }
    }

    private void tatNhacNen() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
            musicPlayer.seekTo(0);
        }
    }

    private void phatAmThanh(MediaPlayer mediaPlayer) {
        if (!isSoundOn) return;
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

        mediaPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        kiemTraCaiDatAmThanh();
        kiemTraCaiDatRung();
        userModel = databaseHelper.getUserInfo();

        if (userModel != null && userModel.getIsSound() == 1) {
            if (musicPlayer != null && !musicPlayer.isPlaying()) {
                musicPlayer.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }

        if (coinPlayer != null) {
            coinPlayer.release();
            coinPlayer = null;
        }

        if (failPlayer != null) {
            failPlayer.release();
            failPlayer = null;
        }
    }
    private void taoRung() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager =
                    (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);

            if (vibratorManager != null) {
                vibrator = vibratorManager.getDefaultVibrator();
            }
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    private void kiemTraCaiDatRung() {
        userModel = databaseHelper.getUserInfo();

        if (userModel != null) {
            isVibrateOn = userModel.getIsVibrate() == 1;
        } else {
            isVibrateOn = true;
        }
    }

    private void rung(long thoiGian) {
        if (!isVibrateOn) {
            Toast.makeText(this, "Rung đang OFF trong cài đặt", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đã gọi lệnh rung", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                    VibrationEffect.createOneShot(
                            thoiGian,
                            VibrationEffect.DEFAULT_AMPLITUDE
                    )
            );
        } else {
            vibrator.vibrate(thoiGian);
        }
    }
}