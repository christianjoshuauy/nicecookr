package com.example.nicecook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BrowseActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    TextView emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        emailAddress = view.findViewById(R.id.emailAddress);
        if(user == null) {
            Intent intent = new Intent(BrowseActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
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

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);

        LinearLayout profileLayout = dialog.findViewById(R.id.layoutProfile);
        LinearLayout TBDlayout = dialog.findViewById(R.id.layoutTBD);
        LinearLayout signOutLayout = dialog.findViewById(R.id.layoutSignOut);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(BrowseActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        TBDlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(BrowseActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(BrowseActivity.this,"Go live is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}