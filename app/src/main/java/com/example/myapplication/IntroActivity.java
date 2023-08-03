package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// This class is responsible for displaying the introductory screen of the app
// It contains two buttons to navigate to the login or sign up activity
// When the login button is clicked, it starts the login activity
// When the sign up button is clicked, it starts the register activity
public class IntroActivity extends AppCompatActivity {

    private Button landingLoginButton, landingSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Find the login and sign up buttons from the view
        landingLoginButton = findViewById(R.id.landingLoginButton);
        landingSignUpButton = findViewById(R.id.landingSignUpButton);

        // Set a click listener for the login button
        landingLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, loginActivity.class));
            }
        });

        // Set a click listener for the sign up button
        landingSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, RegisterActivity.class));
            }
        });
    }
}