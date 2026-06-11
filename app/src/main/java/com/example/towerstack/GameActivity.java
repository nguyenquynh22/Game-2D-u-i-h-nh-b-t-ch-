package com.example.towerstack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.towerstack.Adapter.ResultAdapter;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    String dapan = "oanquan";
    ArrayList<String> arrResult;
    ArrayList<String> arrSuggest;
    LinearLayout lnResult;
    GridView gvSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        init();
        anhXa();
        hashData();
        hienThi();
        hienThiGoiY();

        // Thêm đoạn này vào cuối hàm onCreate của em:
        gvSuggest.setOnItemClickListener((parent, view, position, id) -> {
            // 1. Lấy chữ cái vừa được bấm ở bảng gợi ý
            String chuCaiDuocChon = arrSuggest.get(position);

            // 2. Tìm ô trống đầu tiên trong arrResult để điền vào
            for (int i = 0; i < arrResult.size(); i++) {
                if (arrResult.get(i).equals("")) { // Nếu ô này đang trống
                    arrResult.set(i, chuCaiDuocChon); // Điền chữ vào mảng dữ liệu
                    break; // Điền xong 1 ô thì dừng vòng lặp ngay
                }
            }

            // 3. Ép giao diện hàng đáp án vẽ lại để cập nhật chữ mới vừa điền
            hienThi();
        });
    }

    private void anhXa (){
        lnResult = findViewById(R.id.lnResult);
        gvSuggest = findViewById(R.id.gvSuggest);
    }

    private void init(){
        arrResult = new ArrayList<>();
        arrSuggest = new ArrayList<>();

        for (int i = 0; i < dapan.length(); i++) {
            arrSuggest.add(String.valueOf(dapan.charAt(i)).toUpperCase());
        }

        java.util.Random random = new java.util.Random();
        while (arrSuggest.size() < 14){
            int asciiCode = random.nextInt(90 - 65 +1) +65;
            String randomLetter = String.valueOf((char) asciiCode);
            arrSuggest.add(randomLetter);
        }
        java.util.Collections.shuffle(arrSuggest);
    }

    private void hienThi (){
        lnResult.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < arrResult.size(); i++) {
            View itemView = inflater.inflate(R.layout.item_result, lnResult, false);
            TextView tvResult = itemView.findViewById(R.id.tvResult);
            tvResult.setText(arrResult.get(i));

            // --- THÊM ĐOẠN NÀY: Click vào ô đáp án để xóa chữ ---
            final int index = i; // Khai báo biến final để dùng bên trong sự kiện click
            itemView.setOnClickListener(v -> {
                if (!arrResult.get(index).equals("")) { // Nếu ô này đang có chữ
                    arrResult.set(index, ""); // Đặt lại thành ô trống
                    hienThi(); // Vẽ lại hàng đáp án
                }
            });
            lnResult.addView(itemView);
        }
    }
    private void hienThiGoiY(){
        gvSuggest.setNumColumns(7);
        int density = (int) getResources().getDisplayMetrics().density;
        // Tăng lên một chút để vừa khít với kích thước ô chữ mới
        gvSuggest.setColumnWidth(42 * density);
        gvSuggest.setAdapter(new ResultAdapter(this, 0, arrSuggest));
    }
    private void hashData () {
        arrResult.clear();
        for(int i=0; i<dapan.length(); i++){
            arrResult.add("");
        }

    }

}