package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.examproject_v2.R;
import com.example.examproject_v2.Service.AccountService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransferActivity2 extends AppCompatActivity {
    private final String TAG = "TransferActivity2";
    EditText recipentEmail, transferAmount;
    Spinner accountSpinner;
    CheckBox checkBox;
    private String fuckdig;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String value, fromSpinnerText, recipientEmail;
    private int finalValue, finalTransferAmount;
    private AccountService accService = new AccountService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer2);

        init();
        spinnerSetup();

    }

    public void onClick(View view){
        int i = view.getId();

        if (checkBox.isChecked()) {

            if (!validateForm()) {
                checkBox.setChecked(false);
                return;
            }

            value = transferAmount.getText().toString();
            finalValue = Integer.parseInt(value);
            recipientEmail = recipentEmail.getText().toString();
            fromSpinnerText = accountSpinner.getSelectedItem().toString();

            if (accService.balanceChecker(finalValue, fromSpinnerText)){
                Log.d(TAG, "checked if amount can be transfered");
            }

        }

        if (i == R.id.transfer2NextButton){
            if (!validateForm()) {
                return;
            }
        }

        if (i == R.id.transfer2NextButton && checkBox.isChecked()){
            final Intent intent = new Intent(this, TransferActivity3.class);

            if (!validateForm()) {
                return;
            }

            value = transferAmount.getText().toString();
            finalValue = Integer.parseInt(value);
            recipientEmail = recipentEmail.getText().toString();
            fromSpinnerText = accountSpinner.getSelectedItem().toString();
            Log.d(TAG, "Trying to transfer: " + finalValue);

            CollectionReference usersRef = db.collection("Users");
            Query query = usersRef.whereEqualTo("email", recipientEmail);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            String user = documentSnapshot.getString("email");
                            if(user.equals(recipientEmail)){
                                if (accService.balanceChecker(finalValue, fromSpinnerText)){
                                    Log.d(TAG, "User Exists");
                                    intent.putExtra("amount", value);
                                    intent.putExtra("receiverEmail", recipientEmail);
                                    intent.putExtra("fromAccount", fromSpinnerText);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "Dont have amount to transfer");
                                    Toast.makeText(TransferActivity2.this,"You dont have " + finalValue + " to transfer", Toast.LENGTH_LONG).show();
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    }
                    if(task.getResult().size() == 0 ){
                        Log.d(TAG, "User not Exists");
                        Toast.makeText(TransferActivity2.this, "Username does not exists", Toast.LENGTH_SHORT).show();
                        //You can store new user information here
                        checkBox.setChecked(false);
                    }
                }
            });
        }
    }

    public void spinnerSetup(){

        final List<String> fbArray = new ArrayList<>();
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fbArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        accountSpinner.setAdapter(spinnerArrayAdapter);

        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").whereEqualTo("status", true)
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
    }

    public void init() {
        recipentEmail = findViewById(R.id.transfer2ReceiverText);
        accountSpinner = findViewById(R.id.transfer2Spinner);
        transferAmount = findViewById(R.id.transfer2Amount);
        checkBox = findViewById(R.id.checkBoxTransfer);
    }

    private boolean validateForm () {
        boolean valid = true;

        String recipient = recipentEmail.getText().toString();
        if (TextUtils.isEmpty(recipient)) {
            recipentEmail.setError("Required.");
            valid = false;
        } else {
            recipentEmail.setError(null);
        }

        if (!checkBox.isChecked()){
            checkBox.setError("Required");
            valid = false;
        } else {
            checkBox.setError(null);
        }

        String amount = transferAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            transferAmount.setError("Required.");
            valid = false;
        } else {
            transferAmount.setError(null);
        }

        return valid;
    }

}
