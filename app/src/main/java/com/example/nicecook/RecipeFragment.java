package com.example.nicecook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isButton1Clicked = true;
    private Recipe recipe;
    private TextView detailTitle;
    private TextView detailAuthor;
    private TextView detailTime;
    private TextView txtCount;
    private ImageView detailImage;
    private AppCompatButton btnProcedure;
    private AppCompatButton btnIngredients;
    private ListView listViewProcedure;
    private ImageView btnBack;
    private ImageView btnShare;
    private AlertDialog dialog;
    private ImageButton btnAddFavorite;
    private ImageButton btnRemoveFavorite;
    private DatabaseReference userReference;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
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

    @SuppressLint({"SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        detailTitle = view.findViewById(R.id.detailTitle);
        detailAuthor = view.findViewById(R.id.detailAuthor);
        detailTime = view.findViewById(R.id.detailTime);
        detailImage = view.findViewById(R.id.detailImage);
        btnProcedure = view.findViewById(R.id.btnProcedure);
        btnIngredients = view.findViewById(R.id.btnIngredients);
        btnBack = view.findViewById(R.id.btnBack);
        btnProcedure.setEnabled(false);
        listViewProcedure = view.findViewById(R.id.listViewProcedure);
        txtCount = view.findViewById(R.id.txtCount);
        btnShare = view.findViewById(R.id.btnShare);
        btnAddFavorite = view.findViewById(R.id.btnAddFavorite);
        btnRemoveFavorite = view.findViewById(R.id.btnRemoveFavorite);

        Bundle bundle = getArguments();
        if(bundle != null) {
            recipe = bundle.getParcelable("recipe");
            isRecipeFavorited(recipe.getRecipeID(), btnAddFavorite, btnRemoveFavorite);
            detailTitle.setText(recipe.getTitle());
            detailTime.setText(recipe.getTime() + " mins");
            txtCount.setText(recipe.getProcedure().size() + " Steps");
            userReference = FirebaseDatabase.getInstance().getReference("Users");
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.child("id").getValue(String.class);
                        if (userId != null && userId.equals(recipe.getAuthor())) {
                            String name = childSnapshot.child("name").getValue(String.class);
                            if (name != null) {
                                detailAuthor.setText(name);
                            }
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(view.getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                }
            });
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + recipe.getImageFile());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(view.getContext())
                            .load(uri)
                            .into(detailImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), "Failed to fetch some images", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        btnProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButtonStyles();
                isButton1Clicked = !isButton1Clicked;
                txtCount.setText(recipe.getProcedure().size() + " Steps");
                RecipeAdapter adapter = new RecipeAdapter(getContext(), recipe.getProcedure(), true);
                listViewProcedure.setAdapter(adapter);
                btnProcedure.setEnabled(false);
                btnIngredients.setEnabled(true);
            }
        });

        btnIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButtonStyles();
                isButton1Clicked = !isButton1Clicked;
                txtCount.setText(recipe.getIngredients().size() + " Ingredients");
                RecipeAdapter adapter = new RecipeAdapter(getContext(), recipe.getIngredients(), false);
                listViewProcedure.setAdapter(adapter);
                btnProcedure.setEnabled(true);
                btnIngredients.setEnabled(false);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_share_recipe, null);
                builder.setView(dialogView);
                dialog = builder.create();
                dialog.show();
                ImageView btnCloseDialog = dialog.findViewById(R.id.btnClose);
                AppCompatButton btnCopyLink = dialog.findViewById(R.id.btnCopyLink);
                TextView txtWeburl = dialog.findViewById(R.id.txtWeburl);

                btnCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnCopyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        String link = txtWeburl.getText().toString();
                        ClipData clipData = ClipData.newPlainText("URL", link);
                        clipboard.setPrimaryClip(clipData);
                        Toast.makeText(getContext(), "Link copied to your clipboard!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> favs = new ArrayList<>();
                btnRemoveFavorite.setVisibility(View.VISIBLE);
                btnAddFavorite.setVisibility(View.GONE);
                favs.add(recipe.getRecipeID());
                String userID = FirebaseAuth.getInstance().getUid();
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String userId = childSnapshot.child("id").getValue(String.class);
                            if (userId != null && userId.equals(userID)) {
                                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> favorites = childSnapshot.child("favorites").getValue(typeIndicator);
                                if (favorites != null) {
                                    favorites.addAll(favs);
                                } else {
                                    favorites = favs;
                                }

                                userReference.child(childSnapshot.getKey()).child("favorites").setValue(favorites);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFavorite.setVisibility(View.VISIBLE);
                btnRemoveFavorite.setVisibility(View.GONE);
                String recipeID = recipe.getRecipeID();
                String userID = FirebaseAuth.getInstance().getUid();
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String userId = childSnapshot.child("id").getValue(String.class);
                            if (userId != null && userId.equals(userID)) {
                                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> favorites = childSnapshot.child("favorites").getValue(typeIndicator);
                                if (favorites != null) {
                                    favorites.removeAll(Collections.singleton(recipeID));
                                    userReference.child(childSnapshot.getKey()).child("favorites").setValue(favorites);
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipe.getProcedure(), true);
        listViewProcedure.setAdapter(adapter);

        return view;
    }

    private void updateButtonStyles() {

        if (isButton1Clicked) {
            btnProcedure.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnProcedure.setTextAppearance(R.style.roundButtonLightBrown);
            }
            btnIngredients.setBackgroundResource(R.drawable.rectangle_bg_gray_900_radius_10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnIngredients.setTextAppearance(R.style.btnSolidRounded);
            }
        } else {
            btnIngredients.setBackgroundResource(R.drawable.rectangle_radius_8_5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnIngredients.setTextAppearance(R.style.roundButtonLightBrown);
            }
            btnProcedure.setBackgroundResource(R.drawable.rectangle_bg_gray_900_radius_10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnProcedure.setTextAppearance(R.style.btnSolidRounded);
            }
        }
    }

    private void isRecipeFavorited(String recipeID, ImageButton btnAddFav, ImageButton btnRemoveFav) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = FirebaseAuth.getInstance().getUid();

        if (userID != null) {
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {
                        };
                        ArrayList<String> favorites = childSnapshot.child("favorites").getValue(typeIndicator);
                        if (favorites != null) {
                            if (favorites.contains(recipeID)) {
                                btnAddFav.setVisibility(View.GONE);
                                btnRemoveFav.setVisibility(View.VISIBLE);
                            } else {
                                System.out.println("hello");
                                btnAddFav.setVisibility(View.VISIBLE);
                                btnRemoveFav.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }
    }
}