package com.example.nicecook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Note> list;

    public MyAdapter(Context context, ArrayList<Note> list,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notes_card,parent,false);
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Note notes = list.get(position);
        holder.notesName.setText(notes.getNotesName());
        holder.description.setText(notes.getDescription());
        holder.time.setText(notes.getTime());

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, OpenNotesFragment.class);
            intent.putExtra("title", notes.getNotesName());
            intent.putExtra("content", notes.getDescription());
            String docId = this.Snapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView notesName,description,time;
        LinearLayout linearRowcaldereta;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface){
            super(itemView);
            notesName= itemView.findViewById(R.id.txtCaldereta);
            description = itemView.findViewById(R.id.txtLabelOne);
            time = itemView.findViewById(R.id.txtTime);

            linearRowcaldereta = itemView.findViewById(R.id.linearRowcaldereta);

            linearRowcaldereta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("note", new Note(notesName.getText().toString(), description.getText().toString(), time.getText().toString()));
                    OpenNotesFragment openNotesFragment = new OpenNotesFragment();
                    openNotesFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, openNotesFragment).addToBackStack(null ).commit();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }

    }
}
