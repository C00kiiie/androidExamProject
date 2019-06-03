package com.example.examproject_v2.Activties;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivtity";

    EditText emailText, passwordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth.getInstance().signOut();
        init();
        emailText.setText("test@test.dk");
        passwordText.setText("123456");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

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
                            updateUI(null);
                        }


                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

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

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.registerButton) {
            Log.d(TAG,"leaving for register");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (i == R.id.loginButton) {
            signIn(emailText.getText().toString(), passwordText.getText().toString());
        } else if (i == R.id.passResetButton) {
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
                                    Log.d(TAG, "Trying to reset password, for email: " + email);
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed on finding email, to send new password to", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(this, OverviewActivity.class);
        if(user != null) {
            startActivity(i);
        } else {
        }
    }

    public void init(){
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        mAuth = FirebaseAuth.getInstance();

    }
}
