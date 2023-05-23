package com.example.nicecook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.Collections;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<Recipe> list;
    private StorageReference storageReference;
    private String userID;
    private OnItemClickListener listener;
    private boolean isVertical;

    public CustomAdapter(Context context, ArrayList<Recipe> list, OnItemClickListener listener, boolean isVertical) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.isVertical = isVertical;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_card, parent, false);
        return new CustomViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Recipe recipe = list.get(position);
        holder.title.setText(recipe.getTitle());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userID = FirebaseAuth.getInstance().getUid();
        isRecipeFavorited(recipe.getRecipeID(), holder);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String userId = childSnapshot.child("id").getValue(String.class);
                    if (userId != null && userId.equals(recipe.getAuthor())) {
                        String name = childSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            holder.author.setText("By " + name);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
            }
        });
        holder.time.setText(recipe.getTime() + " mins");
        storageReference = FirebaseStorage.getInstance().getReference("images/" + recipe.getImageFile());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .into(holder.cardImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(holder.itemView.getContext(), "Failed to fetch some images", Toast.LENGTH_SHORT).show();
            }
        });

        if(isVertical) {
            if(position == getItemCount() - 1) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                params.bottomMargin = 200;
                holder.itemView.setLayoutParams(params);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                params.bottomMargin = (int) (15 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
                holder.itemView.setLayoutParams(params);
            }
        } else {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.rightMargin = (int) (15 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.itemView.setLayoutParams(params);
        }

        holder.cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("recipe", recipe);
                RecipeFragment recipeFragment = new RecipeFragment();
                recipeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, recipeFragment).addToBackStack(null).commit();
            }
        });

        holder.btnAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnAddFav.setVisibility(View.GONE);
                holder.btnRemoveFav.setVisibility(View.VISIBLE);
                ArrayList<String> favs = new ArrayList<>();
                favs.add(recipe.getRecipeID());
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
                        Toast.makeText(holder.itemView.getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.btnRemoveFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnAddFav.setVisibility(View.VISIBLE);
                holder.btnRemoveFav.setVisibility(View.GONE);
                listener.onItemClick(recipe);
                String recipeID = recipe.getRecipeID();
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String userId = childSnapshot.child("id").getValue(String.class);
                            if (userId != null && userId.equals(userID)) {
                                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> favorites = childSnapshot.child("favorites").getValue(typeIndicator);
                                if (favorites != null) {
                                    favorites.remove(recipeID);
                                    userReference.child(childSnapshot.getKey()).child("favorites").setValue(favorites);
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(holder.itemView.getContext(), "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, time;
        ImageButton cardImage, btnAddFav, btnRemoveFav;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            author = itemView.findViewById(R.id.txtAuthor);
            time = itemView.findViewById(R.id.txtTime);
            cardImage = itemView.findViewById(R.id.cardImage);
            btnAddFav = itemView.findViewById(R.id.btnAddFav);
            btnRemoveFav = itemView.findViewById(R.id.btnRemoveFav);
        }
    }

    private void isRecipeFavorited(String recipeID, CustomViewHolder holder) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = FirebaseAuth.getInstance().getUid();

        if (userID != null) {
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if(userID.equals(childSnapshot.child("id").getValue())) {
                            GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {
                            };
                            ArrayList<String> favorites = childSnapshot.child("favorites").getValue(typeIndicator);
                            if (favorites != null) {
                                if (favorites.contains(recipeID)) {
                                    holder.btnAddFav.setVisibility(View.GONE);
                                    holder.btnRemoveFav.setVisibility(View.VISIBLE);
                                } else {
                                    System.out.println("hello");
                                    holder.btnAddFav.setVisibility(View.VISIBLE);
                                    holder.btnRemoveFav.setVisibility(View.GONE);
                                }
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

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }
}
