package com.example.examproject_v2.Activties;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.examproject_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//MainActivity handles the login screen.
//A user will be able to register and create an account.
//A user will be able to login if said user has an account.

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivtity";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // enables use of FirebaseAuth. This lets the app register new users to Firebase project.
    EditText emailText, passwordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().signOut();
        init();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        emailText.setText("test@test.dk");
        passwordText.setText("123456");
    }

    // SignIn tries to sign in user, if email and password matches Firebase account.
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed. Error with user input",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            updateUI(null);
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    //Validates if user has filles out the required fields.
    private boolean validateForm () {
        boolean valid = true;

        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required.");
            valid = false;
        } else {
            emailText.setError(null);
        }

        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("Required.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    //Handles a users press on the screen. Specificly the buttons.
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.registerButton) {
            Log.d(TAG, "onClick: registerButton clicked. intet to RegisterActivtiy");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (i == R.id.loginButton) {
            Log.d(TAG, "onClick: loginButton clicked. checking signIn() to see if user exists");
            progressDialog.show();
            signIn(emailText.getText().toString(), passwordText.getText().toString());
        } else if (i == R.id.passResetButton) {
            Log.d(TAG, "onClick: passResetButton clicked. trying to send email to input email.");
            final String email = emailText.getText().toString();
            if (TextUtils.isEmpty(email)) {
                emailText.setError("Required");
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // do something when mail was sent successfully.
                                    Log.d(TAG, "reset email send to: " + email);
                                    Toast.makeText(MainActivity.this, "Check your email inbox", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed on finding email, to send new password to", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
    }

    //Starts an Intent, that leads the user to the OverviewActivity
    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(this, MenuActivity.class);
        if(user != null) {
            // If user exists, startActivity
            progressDialog.dismiss();
            startActivity(i);
        } else {
        }
    }

    //Initializes multiple fields
    public void init(){
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
    }

}
