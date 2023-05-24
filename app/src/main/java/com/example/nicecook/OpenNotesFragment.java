package com.example.nicecook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenNotesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Note note;
    private TextView noteName,description;
    private EditText txtNoteName,txtDescription;
    private AppCompatButton editNote,update;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OpenNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenNotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenNotesFragment newInstance(String param1, String param2) {
        OpenNotesFragment fragment = new OpenNotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_open_notes, container, false);
        noteName = view.findViewById(R.id.txtnotename);
        description = view.findViewById(R.id.txtdescription);
        txtNoteName = view.findViewById(R.id.txtnotename);
        txtDescription = view.findViewById(R.id.txtdescription);
        editNote = view.findViewById(R.id.btn_editnote);
        update = view.findViewById(R.id.btn_updatenote);
        Bundle bundle = getArguments();
        if(bundle != null) {
            note = bundle.getParcelable("note");
            noteName.setText(note.getNotesName());
            description.setText(note.getDescription());
        }
        txtDescription.setEnabled(false);
        txtNoteName.setEnabled(false);
        update.setEnabled(false);

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDescription.setEnabled(true);
                txtNoteName.setEnabled(true);
                update.setEnabled(true);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Notes");
                String noteId = FirebaseAuth.getInstance().getUid();
                // Get the updated note content from your input field
                String updatedNoteName = txtNoteName.getText().toString();
                String updatedDescription = txtDescription.getText().toString();

                // Get the reference to your Firebase database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference notesRef = database.getReference("Notes");

                // Update the note in Firebase
                notesRef.child(noteId).child("noteName").setValue(updatedNoteName)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Note updated successfully
                                Toast.makeText(getActivity(), "Note updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // An error occurred while updating the note
                                Toast.makeText(getActivity(), "Failed to update note", Toast.LENGTH_SHORT).show();
                            }
                        });
                notesRef.child(noteId).child("description").setValue(updatedDescription)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Note updated successfully
                                Toast.makeText(getActivity(), "Note updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // An error occurred while updating the note
                                Toast.makeText(getActivity(), "Failed to update note", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
//            update.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("")
//                }
//            });
        return view;
    }


}