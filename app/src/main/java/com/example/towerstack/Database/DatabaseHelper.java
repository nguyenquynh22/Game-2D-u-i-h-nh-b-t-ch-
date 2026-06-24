package com.example.towerstack.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.towerstack.Model.QuestionModel;
import com.example.towerstack.Model.UserModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String Database_Name = "pic2word.db";
    private static final int Database_Version = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTabelQuestion = "CREATE TABLE tbl_question (" +
                "level INTEGER PRIMARY KEY, "+
                "resource_img TEXT, "+
                "answer TEXT, "+
                "reward_coin INTEGER)";
        db.execSQL(createTabelQuestion);

        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (1, 'oanquan', 'o an quan', 10)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (2, 'coloa', 'co loa', 10)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (3, 'traicay', 'trai cay', 15)"); // Cấp độ 3 của em đây nhé!
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (4, 'caokien', 'cao kien', 15)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (5, 'tieunhan', 'tieu nhan', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (6, 'tangca', 'tang ca', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (7, 'tinhtruong', 'tinh truong', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (8, 'bahoa', 'ba hoa', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (9, 'cobap', 'co bap', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (10, 'cautha', 'cau tha', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (11, 'noigian', 'noi gian', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (12, 'khaucung', 'khau cung', 20)");
        db.execSQL("INSERT INTO tbl_question (level, resource_img, answer, reward_coin) VALUES (13, 'bongbay', 'bong bay', 20)");

        String createTabelUser = "CREATE TABLE tbl_user (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "player_name TEXT, "+
                "current_level INTEGER, "+
                "total_coin INTEGER, " +
                "is_sound INTEGER DEFAULT 1, " +
                "is_vibrate INTEGER DEFAULT 1)";
        db.execSQL(createTabelUser);

        db.execSQL("INSERT INTO tbl_user (player_name, current_level, total_coin, is_sound, is_vibrate) VALUES ('Người chơi ẩn danh #1024', 1, 100, 1, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_question");
        db.execSQL("DROP TABLE IF EXISTS tbl_user");
        onCreate(db);
    }
    public void addQuestion(QuestionModel question, int rewardCoin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("level", question.getLevel());
        values.put("resource_img", question.getResourceImg());
        values.put("answer", question.getAnswer());
        values.put("reward_coin", rewardCoin);
        db.insertWithOnConflict("tbl_question", null, values, SQLiteDatabase.CONFLICT_REPLACE );
        db.close();
    }

    @SuppressLint("Range")
    public QuestionModel getQuestionByLevel(int level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tbl_question", null, "level = ?", new String[]{String.valueOf(level)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String img = cursor.getString(cursor.getColumnIndex("resource_img"));
            String ans = cursor.getString(cursor.getColumnIndex("answer"));

            int coin = cursor.getInt(cursor.getColumnIndex("reward_coin"));

            cursor.close();
            return new QuestionModel(level, img, ans, coin);
        }
        if (cursor != null) cursor.close();
        return null;
    }

    @SuppressLint("Range")
    public UserModel getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from tbl_user Limit 1", null);
        if(cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex("user_id"));
            String name = cursor.getString(cursor.getColumnIndex("player_name"));
            int level = cursor.getInt(cursor.getColumnIndex("current_level"));
            int coin = cursor.getInt(cursor.getColumnIndex("total_coin"));
            int sound = cursor.getInt(cursor.getColumnIndex("is_sound"));
            int vibrate = cursor.getInt(cursor.getColumnIndex("is_vibrate"));

            cursor.close();
            return new UserModel(id, name, level, coin, sound, vibrate);
        }
        if(cursor != null) cursor.close();
        return null;
    }

    public void updateScoreAndLevel(int level, int coin){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update tbl_user Set current_level = ?, total_coin = total_coin + ?", new Object[]{level, coin});
        db.close();
    }
    public void updatePlayerName(String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("player_name", newName);
        db.update("tbl_user", values, null, null);
        db.close();
    }
    public void updateSettings(int isSound, int isVibrate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_sound", isSound);
        values.put("is_vibrate", isVibrate);
        db.update("tbl_user", values, null, null);
        db.close();
    }

}
