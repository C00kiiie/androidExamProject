package com.example.examproject_v2.Activties;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examproject_v2.R;
import com.example.examproject_v2.Service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "registerActivtity";

    EditText nameText, emailText, passwordText, adressText, zipText, secretQuestionAnswerText, ageInput;
    Spinner questionSpinner;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "Access Register");

        init();
        spinnerSetup();

    }

    private void createAccount (String email, String password){
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        String zip = zipText.getText().toString();
        int finalZip = Integer.parseInt(zip);
        String age = ageInput.getText().toString();
        int finalAge = Integer.parseInt(age);

        String createEmail = emailText.getText().toString();
        String spinnerText = questionSpinner.getSelectedItem().toString();

        //TODO: Account and user is created even if there was an error... the createNew is outside of the scope of the validating completeListener.

        UserService createNew = new UserService();

        createNew.createUser(nameText.getText().toString(),createEmail,adressText.getText().toString(),spinnerText, secretQuestionAnswerText.getText().toString(), createNew.citySelect(finalZip),finalZip, finalAge);
        createNew.createAccount("Savings", 0.0, false, createEmail, finalZip);
        createNew.createAccount("Default", 0.0, true, createEmail, finalZip);
        createNew.createAccount("Buisness", 0.0, false, createEmail, finalZip);
        createNew.createAccount("Budget", 0.0, true, createEmail, finalZip);
        if (finalAge >= 77){
            createNew.createAccount("Pension", 0.0, true, createEmail, finalZip);
        } else {
            createNew.createAccount("Pension", 0.0, false, createEmail, finalZip);
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
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

        String adress = adressText.getText().toString();
        if (TextUtils.isEmpty(adress)) {
            adressText.setError("Required.");
            valid = false;
        } else {
            adressText.setError(null);
        }

        String zip = zipText.getText().toString();
        if (TextUtils.isEmpty(zip)) {
            zipText.setError("Required.");
            valid = false;
        } else {
            zipText.setError(null);
        }

        String age = ageInput.getText().toString();
        if (TextUtils.isEmpty(age)) {
            ageInput.setError("Required.");
            valid = false;
        } else {
            ageInput.setError(null);
        }

        String secretAnswer = secretQuestionAnswerText.getText().toString();
        if (TextUtils.isEmpty(secretAnswer)) {
            secretQuestionAnswerText.setError("Required.");
            valid = false;
        } else {
            secretQuestionAnswerText.setError(null);
        }

        String name = nameText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameText.setError("Required.");
            valid = false;
        } else {
            nameText.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(this, MainActivity.class);
        if(user != null) {
            startActivity(i);
        } else {
        }
    }

    public void onClick(View view){
        int i = view.getId();
        if (i == R.id.registerRegisterButton) {
            createAccount(emailText.getText().toString(), passwordText.getText().toString());
        }
    }

    public void init(){
        nameText = findViewById(R.id.registerNameText);
        emailText = findViewById(R.id.registerEmailText);
        passwordText = findViewById(R.id.registerPasswordText);
        ageInput = findViewById(R.id.ageInput);
        adressText = findViewById(R.id.registerAdressText);
        zipText = findViewById(R.id.registerZipText);
        questionSpinner = findViewById(R.id.registerQuestionSpinner);
        secretQuestionAnswerText = findViewById(R.id.secretAnswerText);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void spinnerSetup(){

        List<String> mySpinnerArray = new ArrayList<>();

        mySpinnerArray.add("Name of your first pet");
        mySpinnerArray.add("Nickname in school");
        mySpinnerArray.add("Best friends name");
        mySpinnerArray.add("City your mother grew up in");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mySpinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        questionSpinner.setAdapter(spinnerArrayAdapter);


    }

}
