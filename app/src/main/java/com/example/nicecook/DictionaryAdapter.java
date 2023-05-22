package com.example.nicecook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.DictionaryHolder>{
    Context context;
    ArrayList<DictionaryEntry> list;

    public DictionaryAdapter(Context context, ArrayList<DictionaryEntry> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DictionaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dictionary_card, parent, false);
        return new DictionaryAdapter.DictionaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionaryHolder holder, int position) {
        DictionaryEntry dictionaryEntry = list.get(position);
        holder.txtWord.setText(dictionaryEntry.getWord());
        holder.txtDefinition.setText(dictionaryEntry.getDefinition());

        if(position == getItemCount() - 1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = 200;
            holder.itemView.setLayoutParams(params);
        } else {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = (int) (0 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DictionaryHolder extends RecyclerView.ViewHolder {
        TextView txtWord, txtDefinition;

        public DictionaryHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.txtWord);
            txtDefinition = itemView.findViewById(R.id.txtDefinition);
        }
    }
}
