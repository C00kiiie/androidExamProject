package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examproject_v2.R;
import com.example.examproject_v2.Service.AccountService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferActivity3 extends AppCompatActivity {
    private final String TAG = "TransferActivity3";
    TextView recipientEmail, amount, fromAccount;
    Spinner recipientAccounts;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    AccountService accService = new AccountService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer3);
        Intent intent = getIntent();
        init();

        recipientEmail.setText("You are transfering money to: " + intent.getStringExtra("receiverEmail"));
        fromAccount.setText("You are transfering from: " + intent.getStringExtra("fromAccount"));
        amount.setText("You are transfering: " + intent.getStringExtra("amount") + " kr.");

        spinnerSetup();

    }

    public void onClick(View view){
        int i = view.getId();
        Intent intent1 = getIntent();
        String toAccount = recipientAccounts.getSelectedItem().toString();
        String value = intent1.getStringExtra("amount");
        int finalValue = Integer.parseInt(value);
        String fromAccount = intent1.getStringExtra("fromAccount");
        String email = intent1.getStringExtra("receiverEmail");
        if (i == R.id.tranfer3ConfirmButton) {
            if (email.equalsIgnoreCase(mAuth.getCurrentUser().getEmail()) && (! toAccount.equalsIgnoreCase("Pension"))) {
                accService.transfer(fromAccount, toAccount, recipientEmail.getText().toString(), finalValue);
                Intent intent = new Intent(this, OverviewActivity.class);
                startActivity(intent);
            } else {
                accService.transfer(fromAccount, toAccount, email, finalValue);
                Intent intent = new Intent(this, NemIdActivity.class);
                startActivity(intent);
            }
        } else if (i == R.id.cancelTransfer){
            Toast.makeText(this, "Cancelling transfer", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
        }
    }

    public void spinnerSetup(){
        Intent intent = getIntent();
        String email = intent.getStringExtra("receiverEmail");
        final List<String> fbArray = new ArrayList<>();
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fbArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        recipientAccounts.setAdapter(spinnerArrayAdapter);

        db.collection("Users").document(email).collection("Accounts").whereEqualTo("status", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String subject = document.getString("acountType");
                                fbArray.add(subject);
                            }
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
        db.collection("Users").document(email).collection("Accounts").whereEqualTo("acountType", "Pension")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String subject = document.getString("acountType");
                                if (Arrays.asList(fbArray).contains("Pension")){
                                }else {
                                    fbArray.add(subject);
                                }
                            }
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void init(){
        recipientEmail = findViewById(R.id.transfer3RecipientEmailTextView);
        recipientAccounts = findViewById(R.id.transfer3ReceiverSpinner);
        amount = findViewById(R.id.transfer3Amount2);
        fromAccount = findViewById(R.id.transfer3FromAccount);
    }

}
