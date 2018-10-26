package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText signUpEmail;
    private EditText signUpPassword;
    private EditText signUpPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // get all the EditText in SignUp Activity
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpPassConfirm = findViewById(R.id.confirmPass);

        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Signing Up", Toast.LENGTH_SHORT).show();
                // Get user inputs
                String signUpEmailText = signUpEmail.getText().toString();
                String signUpPasswordText = signUpPassword.getText().toString();
                String signUpPassConfirmText = signUpPassConfirm.getText().toString();

                // Test if passwords match
                // Sent account data to Firebase and check if it completes successfully
                if(signUpPasswordText.equals(signUpPassConfirmText)) {
                    // Create new user with email and password. Give error if not successful.
                    if (mAuth.createUserWithEmailAndPassword(signUpEmailText, signUpPassConfirmText).isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(SignUpActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    // Send password error
                    Toast.makeText(SignUpActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
}