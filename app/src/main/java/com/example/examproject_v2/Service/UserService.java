package com.example.examproject_v2.Service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.examproject_v2.Model.Account;
import com.example.examproject_v2.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserService {
    private final String TAG = "UserService";
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void createUser(String name, String email, String adress,String secretQuestion, String secretAnswer,String department, int zip,int age){

        User user = new User(name, email, adress,secretQuestion, secretAnswer, department, zip, age);

        db.collection("Users").document(email)
                .set(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void createAccount(String type, double balance, boolean status, String email, int zip){

        Account account = new Account(type, balance, status);

        db.collection("Users").document(email).collection("Accounts").document(type)
                .set(account)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public String citySelect(int zip){
        String city = "";

        if (zip < 5000) {
            city = "Copenhagen";
            return city;
        } else {
            city = "Odense";
            return city;
        }
    }

    }
