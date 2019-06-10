package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.examproject_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// OverheadPay handles an overview of a users bills
// At the moment this activity does not provide any real functionality
// other than displaying upcoming bills

public class OverheadPay extends AppCompatActivity {
    private final String TAG = "OverheadActivity";

    Spinner overheadSpinner;
    private String subject;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overhead_pay);
        init();
        spinnerSetup();
    }

    // Handles code, when button is pressed.
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.overheadDelete) {
            // this button does not have a function at the moment.
            Toast.makeText(this, "Delete function is temporarely disabled", Toast.LENGTH_SHORT).show();

        }
    }

    // validates fields in activity
    public void init() {
        overheadSpinner = findViewById(R.id.overheadSpinner);
    }

    // spinner is set up to load in all current AUTO payments from Firebase
    public void spinnerSetup() {


        final List<String> fbArray = new ArrayList<>();
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fbArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        overheadSpinner.setAdapter(spinnerArrayAdapter);

        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document("AutomaticPayments").collection("Bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subject = document.getString("name");
                                String date = document.getString("nextDate");
                                int amount = document.getLong("amount").intValue();
                                fbArray.add("Note: " + subject + " // Due: " + date + " // Amount: " + amount);
                            }
                            spinnerArrayAdapter.notifyDataSetChanged();
                        } if(task.getResult().size() == 0 ){
                            fbArray.add("You dont have any bills");
                            Log.d(TAG, "no bills Exists");
                            Toast.makeText(OverheadPay.this, "You dont have any bills", Toast.LENGTH_SHORT).show();
                            //You can store new user information here
                            spinnerArrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "onComplete: an error accoured when setting up spinner. " + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: failure on establishing connection to firebase: " + e.getMessage());
            }
        });
    }
}
