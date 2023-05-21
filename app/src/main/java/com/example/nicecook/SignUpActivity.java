package com.example.nicecook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtName;
    TextView txtEmail;
    TextView txtPassword;
    TextView txtConfirmPassword;
    TextView txtSignIn;
    LinearLayout btnSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView txtLabelFour;
    ImageView imageArrowright;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(SignUpActivity.this, BrowseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        txtSignIn = findViewById(R.id.txtSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        txtLabelFour = findViewById(R.id.txtLabelFour);
        imageArrowright = findViewById(R.id.imageArrowright);

        txtSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.btnSignUp) {
              String name = txtName.getText().toString();
              String email = txtEmail.getText().toString();
              String password = txtPassword.getText().toString();
              String confirmPassword = txtConfirmPassword.getText().toString();

              if(TextUtils.isEmpty(name)) {
                  Toast.makeText(SignUpActivity.this, "Please fill out your name", Toast.LENGTH_SHORT).show();
                  return;
              }
              if(TextUtils.isEmpty(email)) {
                  Toast.makeText(SignUpActivity.this, "Please fill out your email", Toast.LENGTH_SHORT).show();
                  return;
              }
              if(TextUtils.isEmpty(password)) {
                  Toast.makeText(SignUpActivity.this, "Please fill out your password", Toast.LENGTH_SHORT).show();
                  return;
              }
              if(TextUtils.isEmpty(confirmPassword)) {
                  Toast.makeText(SignUpActivity.this, "Please fill out your confirm password", Toast.LENGTH_SHORT).show();
                  return;
              }
              if(password.length() < 8) {
                  Toast.makeText(SignUpActivity.this, "Password has to be atleast 8 characters", Toast.LENGTH_SHORT).show();
                  return;
              }
              if(!password.equals(confirmPassword)) {
                  Toast.makeText(SignUpActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                  return;
              }
            progressBar.setVisibility(View.VISIBLE);
            txtLabelFour.setVisibility(View.GONE);
            imageArrowright.setVisibility(View.GONE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                String id = task.getResult().getUser().getUid();
                                User user = new User(id, name, new ArrayList<>(), new ArrayList<>());
                                databaseReference.push().setValue(user);
                                Toast.makeText(SignUpActivity.this, "Registered Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, BrowseActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Signing Up Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                            txtLabelFour.setVisibility(View.VISIBLE);
                            imageArrowright.setVisibility(View.VISIBLE);
                        }
                    });
        }
        if (v.getId() == R.id.txtSignIn) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}