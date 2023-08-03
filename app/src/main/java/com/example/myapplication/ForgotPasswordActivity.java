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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotPasswordEmail; //EditText variables for widgets in activity_forgot_password.xml
    private Button forgotPasswordButton; //Button variables for widgets in activity_forgot_password.xml
    private ProgressBar progressBar; //ProgressBar variables for widgets in activity_forgot_password.xml
    private TextView switchToLogin; //TextView variables for widgets in activity_forgot_password.xml

    FirebaseAuth auth; //FirebaseAuth variables for application to connect Firebase's Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Initializing Variables.
        forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        progressBar = findViewById(R.id.forgotPasswordProgressBar);
        switchToLogin = findViewById(R.id.linkLogin);
        auth = FirebaseAuth.getInstance();

        /* When user clicks the forgotPasswordButton(TextView) widget, it will call the
         * resetPassword() function. */
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        /* When user clicks the switchToLogin(TextView) widget, the application will direct user
         * to the Login Page by using the startActivity() function as an explicit intent. */
        switchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin.setTextColor(Color.BLUE);
                startActivity(new Intent(ForgotPasswordActivity.this, loginActivity.class));
            }
        });


    }

    /* The resetPassword() function is a private and void function. The resetPassword() function will check the
     * information that the user keyed in (email). If all the details are
     * correct, then the application will send a link to the email to that user's email account for them to
     * reset their password.
     */
    private void resetPassword(){
        String email = forgotPasswordEmail.getText().toString().trim();

        //Check the email format of the user.
        if(email.isEmpty()){
            forgotPasswordEmail.setError("Email must not be empty.");
            forgotPasswordEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            forgotPasswordEmail.setError("Email must be in valid format.");
            forgotPasswordEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); //Setting the progress bar to visible to inform the user to wait.

        //Send a link to the user's email account for them to reset their password.
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                /* Send a reset password link to the user's email account. If the process is successful, the application will
                 * pop up a message telling the user the process is successful. Also, the progress bar's visibility will be set to gone too
                 * to indicate that the forgot password process is done.
                 */
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(ForgotPasswordActivity.this, loginActivity.class));
                }

                /* If the process failed, the application will pop up a message telling the user that the process is unsuccessful
                 * and ask them to try again. Also, the progress bar's visibility will be set to gone too
                 * to indicate that the forgot password process is done.
                 */
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Reset password failed. Please try again.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}