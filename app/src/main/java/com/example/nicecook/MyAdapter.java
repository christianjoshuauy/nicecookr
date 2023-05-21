package com.example.nicecook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    Context context;
    ArrayList<Note> list;

    public MyAdapter(Context context, ArrayList<Note> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notes_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Note notes = list.get(position);
        holder.notesName.setText(notes.getNotesName());
        holder.description.setText(notes.getDescription());
        holder.time.setText(notes.getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView notesName,description,time;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            notesName= itemView.findViewById(R.id.txtCaldereta);
            description = itemView.findViewById(R.id.txtLabelOne);
            time = itemView.findViewById(R.id.txtTime);
        }
    }
}
