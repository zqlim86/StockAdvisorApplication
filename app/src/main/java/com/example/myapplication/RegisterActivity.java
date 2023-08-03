package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextView switchToLogin; //TextView variables for widgets in activity_register.xml
    EditText inputName, inputEmail, inputPassword, inputConfirmPassword; //EditText variables for widgets in activity_register.xml
    Button registerButton; //Button variables for widgets in activity_register.xml
    ProgressBar progressBar; //ProgressBar variables for widgets in activity_register.xml


    private FirebaseAuth auth; //FirebaseAuth variables for application to connect Firebase's Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing Variables.
        auth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.buttonRegister);
        switchToLogin = findViewById(R.id.linkLogin);
        inputName = findViewById(R.id.registerName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.loginPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        progressBar = findViewById(R.id.registerProgressBar);


        /* When user clicks the switchToLogin(TextView) widget, the application will direct user
         * to the Login Page by using the startActivity() function as an explicit intent. */
        switchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin.setTextColor(Color.BLUE);
                startActivity(new Intent(RegisterActivity.this, loginActivity.class));
            }
        });

        /* When user clicks the registerButton(Button) widget, it will call the registerUser() function. */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    /* The registerUser() function is a private and void function. The registerUser() function will check the
     * information that the user keyed in (name, email, password, confirm password). If all the details are
     * correct, then the application will register the account into the Firebase Database and direct the user
     * to the Login Page for them to log in using their account.   */
    private void registerUser(){
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        //Check the name format of the user.
        if(name.isEmpty()){
            inputName.setError("Name is required");
            inputName.requestFocus();
            return;
        }
        //Check the email format of the user.
        if(email.isEmpty()){
            inputEmail.setError("Email is required");
            inputEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Please provide a valid email address.");
            inputEmail.requestFocus();
            return;
        }
        //Check the password format of the user.
        if(password.isEmpty()){
            inputPassword.setError("Password is required");
            inputPassword.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty()){
            inputConfirmPassword.setError("Password is required");
            inputConfirmPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            inputPassword.setError("Password must be minimum 6 characters or above.");
            inputPassword.requestFocus();
            return;
        }
        //Check if the password and the confirm password contains the same characters.
        if(!password.equals(confirmPassword)){
            inputPassword.setError("Password doesn't match, please try again.");
            inputPassword.requestFocus();
            return;
        }
        //Setting the progress bar to visible to inform the user to wait.
        progressBar.setVisibility(View.VISIBLE);

        //Create the user's account in the Firebase Database.
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name,email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(RegisterActivity.this, loginActivity.class));
                                            }else{
                                                Toast.makeText(RegisterActivity.this, "Failed to register. Try Again.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                        }else{
                            Toast.makeText(RegisterActivity.this, "2 Failed to register. Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

    }
}