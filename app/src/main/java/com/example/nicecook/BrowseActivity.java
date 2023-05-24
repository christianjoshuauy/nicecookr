package com.example.nicecook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class BrowseActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    TextView userName;
    TextView emailAddress;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imageUri;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userName = view.findViewById(R.id.userName);
        emailAddress = view.findViewById(R.id.emailAddress);
        if(user == null) {
            Intent intent = new Intent(BrowseActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.child("id").getValue(String.class);
                        if (userId != null && userId.equals(user.getUid())) {
                            String name = childSnapshot.child("name").getValue(String.class);
                            if (name != null) {
                                userName.setText(name);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BrowseActivity.this, "Failed to fetch user name", Toast.LENGTH_SHORT).show();
                }
            });
            emailAddress.setText(user.getEmail());
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.bringToFront();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.lbl, R.string.lbl);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    navigationView.setCheckedItem(R.id.nav_home);
                    break;
                case R.id.favorites:
                    replaceFragment(new FavoritesFragment());
                    navigationView.setCheckedItem(R.id.nav_favorites);
                    break;
                case R.id.notes:
                    replaceFragment(new NotesFragment());
                    navigationView.setCheckedItem(R.id.nav_notes);
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    navigationView.setCheckedItem(R.id.nav_profile);
                    break;
            }

            return true;
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        replaceFragment(new HomeFragment());
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        break;
                    case R.id.nav_favorites:
                        replaceFragment(new FavoritesFragment());
                        bottomNavigationView.setSelectedItemId(R.id.favorites);
                        break;
                    case R.id.nav_notes:
                        replaceFragment(new NotesFragment());
                        bottomNavigationView.setSelectedItemId(R.id.notes);
                        break;
                    case R.id.nav_dictionary:
                        replaceFragment(new DictionaryFragment());
                        bottomNavigationView.setSelectedItemId(0);
                        break;
                    case R.id.nav_translate:
                        replaceFragment(new TranslateFragment());
                        bottomNavigationView.setSelectedItemId(0);
                        break;
                    case R.id.nav_profile:
                        replaceFragment(new ProfileFragment());
                        bottomNavigationView.setSelectedItemId(R.id.profile);
                        break;
                    case R.id.nav_logout:
                        signOut();
                        Toast.makeText(BrowseActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(BrowseActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    private String uploadImage() {
        final CountDownLatch latch = new CountDownLatch(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = format.format(now) + ".jpg";
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ImageView imageView = dialog.findViewById(R.id.recipeImage);
                imageView.setImageURI(null);
                Toast.makeText(BrowseActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                latch.countDown();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BrowseActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView imageView = dialog.findViewById(R.id.recipeImage);
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    private void showBottomDialog() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ArrayList<Fragment> fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
        boolean isShop = false;
        for(Fragment fragment : fragments) {
            if(fragment.isVisible() && fragment instanceof ShoppingListFragment) {
                isShop = true;
                break;
            }
        }
        if(isShop) {
            dialog.setContentView(R.layout.bottomsheet_shop);
            TextView itemName = dialog.findViewById(R.id.itemName);
            TextView itemQuantity = dialog.findViewById(R.id.itemQuantity);
            TextView itemPrice = dialog.findViewById(R.id.itemPrice);
            LinearLayout btnAddItem = dialog.findViewById(R.id.btnAddItem);

            btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userID = user.getUid();
                    UUID randomUUID = UUID.randomUUID();
                    String itemID = randomUUID.toString();
                    String name = itemName.getText().toString();
                    String quantity = itemQuantity.getText().toString();
                    double price = 0;
                    try {
                        price = Double.parseDouble(itemPrice.getText().toString());
                        if(price <= 0) {
                            Toast.makeText(BrowseActivity.this, "Price should be more than 0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        Toast.makeText(BrowseActivity.this, "Enter a valid number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ShoppingItem shoppingItem = new ShoppingItem(userID, itemID, name, quantity, price);
                    databaseReference = FirebaseDatabase.getInstance().getReference("ShoppingList");
                    databaseReference.push().setValue(shoppingItem);
                    Toast.makeText(BrowseActivity.this, "Added Shopping Item", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            dialog.setContentView(R.layout.bottomsheet);
            TextView recipeName = dialog.findViewById(R.id.recipeName);
            TextView recipeIngredients = dialog.findViewById(R.id.recipeIngredients);
            TextView recipeProcedure = dialog.findViewById(R.id.recipeProcedure);
            TextView recipeDuration = dialog.findViewById(R.id.recipeDuration);
            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
            LinearLayout btnConfirm = dialog.findViewById(R.id.btnConfirm);
            LinearLayout btnUpload = dialog.findViewById(R.id.btnUpload);
            AppCompatButton btnAddIngredients = dialog.findViewById(R.id.btnAddIngredients);
            AppCompatButton btnAddProcedure = dialog.findViewById(R.id.btnAddProcedure);
            ChipGroup chipGroupIng = dialog.findViewById(R.id.chipGroupIng);
            ChipGroup chipGroupProc = dialog.findViewById(R.id.chipGroupProc);

            btnAddIngredients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ingredient = recipeIngredients.getText().toString();
                    recipeIngredients.setText("");
                    addChip(chipGroupIng, ingredient);
                }
            });

            btnAddProcedure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String step = recipeProcedure.getText().toString();
                    recipeProcedure.setText("");
                    addChip(chipGroupProc, step);
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String fileName = uploadImage();
                        String title = recipeName.getText().toString();
                        String author = user.getUid();
                        UUID randomUUID = UUID.randomUUID();
                        String recipeID = randomUUID.toString();
                        ArrayList<String> ingredients = new ArrayList<>();
                        for(int i = 0; i < chipGroupIng.getChildCount(); i++) {
                            Chip chip = (Chip) chipGroupIng.getChildAt(i);
                            ingredients.add(chip.getText().toString());
                        }
                        ArrayList<String>  procedure = new ArrayList<>();
                        for(int i = 0; i < chipGroupProc.getChildCount(); i++) {
                            Chip chip = (Chip) chipGroupProc.getChildAt(i);
                            procedure.add(chip.getText().toString());
                        }
                        int duration = Integer.parseInt(recipeDuration.getText().toString());
                        Recipe recipe = new Recipe(recipeID, title, author, duration, ingredients, procedure, fileName);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
                        databaseReference.push().setValue(recipe);
                        Toast.makeText(BrowseActivity.this, "Added Recipe", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(BrowseActivity.this, "Adding Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addChip(ChipGroup chipGroup, String text) {
        if (!text.isEmpty()) {
            Chip chip = new Chip(this);
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
    }
}