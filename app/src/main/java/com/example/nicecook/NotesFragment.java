package com.example.nicecook;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment implements RecyclerViewInterface{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isButton1Clicked = true;
    private boolean isButton2Clicked = false;
    private boolean isButton3Clicked = false;
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<Note> list;
    Button all, encrypted, unencrypted;
    TextView whatnote;
    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerView = view.findViewById(R.id.recyclerListcaldereta);
        database = FirebaseDatabase.getInstance().getReference("Notes");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(),list,this);
        recyclerView.setAdapter(myAdapter);
        all = view.findViewById(R.id.btn_all);
        encrypted = view.findViewById(R.id.btn_encrypted);
        unencrypted = view.findViewById(R.id.btn_unencrypted);
        whatnote = view.findViewById(R.id.txtwhatnote);
        showNotes(true, 0);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isButton1Clicked = true;
                isButton2Clicked = false;
                isButton3Clicked = false;
                updateButtonStyles();
                showNotes(true, 0);
                whatnote.setText("All Notes");
                all.setEnabled(false);
                unencrypted.setEnabled(true);
                encrypted.setEnabled(true);
            }
        });

        encrypted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isButton1Clicked = false;
                isButton2Clicked = true;
                isButton3Clicked = false;
                updateButtonStyles();
                showNotes(false, 1);
                whatnote.setText("Encrypted Notes");
                all.setEnabled(true);
                unencrypted.setEnabled(true);
                encrypted.setEnabled(false);

            }
        });

        unencrypted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isButton1Clicked = false;
                isButton2Clicked = false;
                isButton3Clicked = true;
                updateButtonStyles();
                showNotes(false, 0);
                whatnote.setText("Unencrypted Notes");
                all.setEnabled(true);
                unencrypted.setEnabled(false);
                encrypted.setEnabled(true);
            }
        });
        // onclick all given
        // onclick sa encrypted showNotes(false, 1);
        // onclick sa unencypt showNotes(false, 0);

        return view;
    }

    private void showNotes(boolean isAll, int status) {
        list.clear();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    Note notes = data.getValue(Note.class);
                    if(isAll || notes != null && notes.getStatus() == status) {
                        list.add(notes);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateButtonStyles() {

        if (isButton1Clicked && !isButton2Clicked && !isButton3Clicked) {
            all.setBackgroundResource(R.drawable.rectangle_bg_gray_900_radius_10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                all.setTextAppearance(R.style.btntobrown);
            }
            encrypted.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                encrypted.setTextAppearance(R.style.btntowhite);
            }
            unencrypted.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unencrypted.setTextAppearance(R.style.btntowhite);
            }
        } else if (!isButton1Clicked && isButton2Clicked && !isButton3Clicked){
            all.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                all.setTextAppearance(R.style.btntowhite);
            }
            unencrypted.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unencrypted.setTextAppearance(R.style.btntowhite);
            }
            encrypted.setBackgroundResource(R.drawable.rectangle_bg_gray_900_radius_10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                encrypted.setTextAppearance(R.style.btntobrown);
            }
        } else if (!isButton1Clicked && !isButton2Clicked && isButton3Clicked){
            all.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                all.setTextAppearance(R.style.btntowhite);
            }
            unencrypted.setBackgroundResource(R.drawable.rectangle_bg_gray_900_radius_10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unencrypted.setTextAppearance(R.style.btntobrown);
            }
            encrypted.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                encrypted.setTextAppearance(R.style.btntowhite);
            }
        }
    }

    @Override
    public void onItemClick(int position) {

    }
}