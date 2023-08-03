package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {

    TextView switchToRegister, switchToForgotPassword; //TextView variables for widgets in activity_login.xml
    EditText loginEmail, loginPassword; //EditText variables for widgets in activity_login.xml
    Button loginButton; //Button variable for widget in activity_login.xml
    ProgressBar loginProgressBar; //ProgressBar variable for widget in activity_login.xml
    BroadcastReceiver broadcastReceiver = null;

    FirebaseAuth auth; //FirebaseAuth variable for application to connect Firebase's Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing Variables.
        auth = FirebaseAuth.getInstance(); //Initializing FirebaseAuth variable.
        loginEmail = findViewById(R.id.registerName); //Initializing EditText loginEmail variable.
        loginPassword = findViewById(R.id.loginPassword); //Initializing EditText loginPassword variable.
        loginButton = findViewById(R.id.loginButton); //Initializing Button loginButton variable.
        loginProgressBar = findViewById(R.id.loginProgressBar); // //Initializing ProgressBar loginProgressBar variable.
        switchToRegister = findViewById(R.id.linkRegister); //Initializing TextView switchToRegister variable.
        switchToForgotPassword = findViewById(R.id.linkForgotPassword); //Initializing TextView switchToForgotPassword variable.

        /* When user clicks the switchToRegister(TextView) widget, the application will direct user
         * to the Register Page by using the startActivity() function as an explicit intent. */
        switchToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister.setTextColor(Color.BLUE);
                startActivity(new Intent(loginActivity.this, RegisterActivity.class));
            }
        });


        /* When user clicks the switchToForgotPassword(TextView) widget, the application will direct user
         * to the Forgot Password Page by using the startActivity() function as an explicit intent. */
        switchToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToForgotPassword.setTextColor(Color.BLUE);
                startActivity(new Intent(loginActivity.this, ForgotPasswordActivity.class));
            }
        });

        /* When user clicks the loginButton(Button) widget, it will call the loginUser() function. */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    /* The loginUser() function is a private and void function. The loginUser() function will check the
     * the email and password entered by the user, if the details matched with the data that is stored in
     * Firebase's Realtime Database, then the application will let the user log in. */
    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        //Check the email format of the user.
        if(email.isEmpty()){
            loginEmail.setError("Email is required to login.");
            loginEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please enter a valid email to login.");
            loginEmail.requestFocus();
            return;
        }
        //Check the password format of the user.
        if(password.isEmpty()){
            loginPassword.setError("Password is required to login");
            loginPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            loginPassword.setError("Password must be 6 characters or above.");
            loginPassword.requestFocus();
            return;
        }

        //Setting the progress bar to visible to inform the user to wait.
        loginProgressBar.setVisibility(View.VISIBLE);

        //Checks if the user's account exists in the Firebase Realtime Database.
         auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /* If the user's account DOES exist in the database, then set make a pop up message
                   telling the user that the login was successful and direct them to the Home Page.
                   Also, the progress bar's visibility will be set to gone too to indicate that the
                   login process is done.
                 */
                if(task.isSuccessful()){
                    Toast.makeText(loginActivity.this, "Login Successful!",Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.GONE);
                    startActivity(new Intent(loginActivity.this, MainActivity.class));
                }
                /* If the user's account DOES NOT exist in the database, then set make a pop up message
                   telling the user that the login was unsuccessful and ask them to try logging in again.
                   Also, the progress bar's visibility will be set to gone too to indicate that the
                   login process is done.
                 */
                else{
                    Toast.makeText(loginActivity.this, "Login Unsuccessful, Please Try Again.",Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}