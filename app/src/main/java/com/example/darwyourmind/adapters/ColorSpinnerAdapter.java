package com.example.darwyourmind.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.darwyourmind.R;

import java.util.List;

public class ColorSpinnerAdapter extends ArrayAdapter<String> {

    private final List<String> colors;
    private final int[] colorValues; // 배열에 실제 색상 값을 저장

    public ColorSpinnerAdapter(Context context, List<String> colors, int[] colorValues) {
        super(context, 0, colors);
        this.colors = colors;
        this.colorValues = colorValues;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        View colorPreview = convertView.findViewById(R.id.colorPreview);
        TextView colorName = convertView.findViewById(R.id.colorName);

        // 색상 이름 설정
        colorName.setText(colors.get(position));
        // 색상 미리보기 설정
        colorPreview.setBackgroundTintList(ColorStateList.valueOf(colorValues[position]));

        return convertView;
    }
}

