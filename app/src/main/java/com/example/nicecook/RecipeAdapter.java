package com.example.nicecook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RecipeAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> procedureList;
    private boolean isProcedure;

    public RecipeAdapter(Context context, ArrayList<String> procedureList, boolean isProcedure) {
        super(context, 0, procedureList);
        this.context = context;
        this.procedureList = procedureList;
        this.isProcedure = isProcedure;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_procedure, parent, false);
        }

        String procedure = procedureList.get(position);

        TextView tvStepNumber = convertView.findViewById(R.id.tvStepNumber);
        TextView tvProcedure = convertView.findViewById(R.id.tvProcedure);
        tvStepNumber.setText((isProcedure ? "Step " : "Ingredient ") + (position + 1));
        tvProcedure.setText(procedure);

        return convertView;
    }
}

