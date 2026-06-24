package com.example.towerstack.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.towerstack.Model.LetterModel;
import com.example.towerstack.R;

import java.util.List;

public class ResultAdapter extends ArrayAdapter<LetterModel> {
    private Context myContext;
    private List<LetterModel> arr;

    public ResultAdapter(@NonNull Context context, int resource, @NonNull List<LetterModel> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.arr = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(myContext);
            convertView = inflater.inflate(R.layout.item_result, parent, false);
        }

        TextView tvResult = convertView.findViewById(R.id.tvResult);
        LetterModel item = this.arr.get(position);

        tvResult.setText(item.getText());

        if (item.getText().equals("")) {
            tvResult.setVisibility(View.INVISIBLE);
        } else {
            tvResult.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}