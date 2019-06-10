package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examproject_v2.Model.User;
import com.example.examproject_v2.R;
import com.example.examproject_v2.Service.AccountService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

// This activity handles an account overview, and also checks if the user has any pending autobills.

public class OverviewActivity extends AppCompatActivity {
    private final String TAG = "OverviewActivity";

    TextView buisnessText, savingsText, pensionText, defaultText, budgetText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    AccountService accService = new AccountService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        init();

        // When onCreate is called, then try to run method
        try {
            checkAutoPay();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getAccountAmounts();
        hideAccounts();
    }

    // There are no clickable views/widgets in layout.
    public void onClick(View view){
        int i = view.getId();

    }

    // Gets current users account balances from Firebase.
    public void getAccountAmounts(){
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").whereEqualTo("status", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int balance = document.getLong("balance").intValue();
                                Log.d(TAG, "printing found accounts: " + document.getId() + " has " + document.getLong("balance").intValue() + " money on it!");
                                if (document.getId().equalsIgnoreCase("Default")){
                                    defaultText.setText("Default:\n" + balance + " kr.");
                                    //defaultAmount.setText(balance + " kr.");
                                } else if (document.getId().equalsIgnoreCase("Savings")){
                                    savingsText.setText("Savings:\n" + balance + " kr.");
                                    //savingsAmount.setText(balance + " kr.");
                                } else if (document.getId().equalsIgnoreCase("Budget")){
                                    System.out.println(balance);
                                    budgetText.setText("Budget:\n" + balance + " kr.");
                                    //budgetAmount.setText(balance + " kr.");
                                } else if (document.getId().equalsIgnoreCase("Buisness")){
                                    buisnessText.setText("Business:\n" + balance + " kr.");
                                    //buisnessAmount.setText(balance + " kr.");
                                } else if (document.getId().equalsIgnoreCase("Pension")){
                                    pensionText.setText("Pension:\n" + balance + " kr.");
                                    //pensionAmount.setText(balance + " kr.");
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "getAccounts:failure", task.getException());
                            Toast.makeText(OverviewActivity.this, "Could find accounts",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure, could get accounts" + e.getMessage());
            }
        });

    }

    // Hides textViews that holds account info, that the current user doesnt have access to.
    public void hideAccounts(){
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").whereEqualTo("status", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "printing hidden accounts: " + document.getId());
                                if (document.getId().equalsIgnoreCase("Savings")){
                                    savingsText.setVisibility(View.GONE);

                                } else if (document.getId().equalsIgnoreCase("Buisness")){
                                    buisnessText.setVisibility(View.GONE);

                                } else if (document.getId().equalsIgnoreCase("Pension")){
                                    pensionText.setVisibility(View.GONE);

                                }
                            }
                        }
                    }
                });
    }

    // Checks if the current user has any pending bills that needs to be paid
    public void checkAutoPay() throws ParseException {
        Log.d(TAG, "Calling checkAutoPay Method()");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        Log.d(TAG, "current date: " + dateFormat.format(date));

        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document("AutomaticPayments").collection("Bills").whereEqualTo("nextDate", dateFormat.format(date))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        String docName = doc.getId();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                        Date newDate = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newDate);
                        cal.add(Calendar.MONTH, 1);
                        newDate = cal.getTime();
                        int amount = doc.getLong("amount").intValue();
                        String fromAccount = doc.getString("fromAccount");
                        int kontoNummer = doc.getLong("toKontoNummer").intValue();
                        String billName = doc.getString("name");

                        Log.d(TAG, "printing found id's: " + doc.getId() + "with pay date set for: " + dateFormat.format(newDate));
                        accService.payAuto(fromAccount,kontoNummer,amount,dateFormat.format(newDate),billName);
                        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(docName).delete();

                        Log.w(TAG, "secretAnswer:failure", task.getException());
                        Toast.makeText(OverviewActivity.this, "An autopayment was made:" + billName, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Log.d(TAG, "onComplete: checking for autopayments, something went wrong: " + task.getException());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure, could get doc's" + e.getMessage());

            }
        });
    }

    // validates fields in activity
    public void init(){
        budgetText = findViewById(R.id.budgetTextView);
        defaultText = findViewById(R.id.defaultTextView);
        savingsText = findViewById(R.id.savingsTextView);
        buisnessText = findViewById(R.id.buisnessTextView);
        pensionText = findViewById(R.id.pensionTextView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

}
