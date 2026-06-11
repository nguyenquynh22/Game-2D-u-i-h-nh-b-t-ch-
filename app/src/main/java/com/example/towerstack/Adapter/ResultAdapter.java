package com.example.towerstack.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.towerstack.R;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends ArrayAdapter<String> {
    private Context myContext;
    private ArrayList<String> arr;
    public ResultAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.arr = new ArrayList<>(objects);
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_result, parent, false);
        }
        TextView tvResult = convertView.findViewById(R.id.tvResult);
        tvResult.setText(this.arr.get(position));
        return convertView;
    }
}
