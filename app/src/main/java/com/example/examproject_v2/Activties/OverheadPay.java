package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.examproject_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OverheadPay extends AppCompatActivity {

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

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.overheadDelete) {
            String spinnerText = overheadSpinner.getSelectedItem().toString();



            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
        }
    }

    public void init() {
        overheadSpinner = findViewById(R.id.overheadSpinner);
    }

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
                        }
                    }
                });
    }
}
