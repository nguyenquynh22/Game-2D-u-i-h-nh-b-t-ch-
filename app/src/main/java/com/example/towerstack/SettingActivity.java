package com.example.towerstack;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.example.towerstack.Database.DatabaseHelper;
import com.example.towerstack.Model.UserModel;

public class SettingActivity extends AppCompatActivity {

    ImageButton btnBack, ibtnEdit;
    Switch swSound, swVibrate;
    DatabaseHelper databaseHelper;
    UserModel userModel;
    TextView edtPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.btnBack);
        swSound = findViewById(R.id.swSound);
        swVibrate = findViewById(R.id.swVibrate);
        ibtnEdit = findViewById(R.id.ibtnEdit);
        edtPlayerName = findViewById(R.id.edtPlayerName);

        databaseHelper = new DatabaseHelper(this);
        hienThiThongTinUser();
        ibtnEdit.setOnClickListener(v -> changePlayerName());

        if (userModel != null) {
            swSound.setChecked(userModel.getIsSound() == 1);
            swVibrate.setChecked(userModel.getIsVibrate() == 1);
        }

        btnBack.setOnClickListener(v -> finish());

        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int sound = isChecked ? 1 : 0;
            int vibrate = swVibrate.isChecked() ? 1 : 0;

            databaseHelper.updateSettings(sound, vibrate);
        });

        swVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int sound = swSound.isChecked() ? 1 : 0;
            int vibrate = isChecked ? 1 : 0;

            databaseHelper.updateSettings(sound, vibrate);
        });
    }
    private void hienThiThongTinUser() {
        userModel = databaseHelper.getUserInfo();
        if (userModel != null) {
            edtPlayerName.setText(userModel.getPlayerName());
            swSound.setChecked(userModel.getIsSound() == 1);
            swVibrate.setChecked(userModel.getIsVibrate() == 1);
        }
    }

    private void changePlayerName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi tên hiển thị");

        // Tạo một ô nhập liệu EditText bỏ vào Dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        // Gợi ý sẵn tên cũ để người chơi dễ sửa
        if (userModel != null) {
            input.setText(userModel.getPlayerName());
            input.setSelection(input.getText().length()); // Đưa con trỏ xuống cuối dòng
        }
        builder.setView(input);

        // Xử lý nút Xác nhận (Lưu)
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tenMoi = input.getText().toString().trim();
            if (tenMoi.isEmpty()) {
                Toast.makeText(SettingActivity.this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật vào SQLite
            databaseHelper.updatePlayerName(tenMoi);
            Toast.makeText(SettingActivity.this, "Đã cập nhật tên mới!", Toast.LENGTH_SHORT).show();

            // Đọc lại DB để làm mới giao diện hiển thị tên ngoài màn hình cài đặt
            hienThiThongTinUser();
        });

        // Xử lý nút Hủy bỏ
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}