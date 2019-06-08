package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examproject_v2.Model.PaymentParcelable;
import com.example.examproject_v2.R;

import com.example.examproject_v2.Service.AccountService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//NemIdActivity handles and verifies the users secret answer.
//A user will need to verify themselves to be able to set up a autobill or pay bills.

public class NemIdActivity extends AppCompatActivity {
    private static final String TAG = "NemIdActivtity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AccountService accService = new AccountService();
    boolean check = false; // a boolean to use to validate answer -> checkSecretAnswer()
    TextView nemIdText, nemIdCode;
    EditText nemIdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nem_id);
        init();
        getSecretQuestion();

    }

    //Fetches the users secret quetion from Firebase
    public void getSecretQuestion(){
        Log.d(TAG, "getSecretQuestion method called, and returned");
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String title = documentSnapshot.getString("secretQuestion");
                        nemIdCode.setText(title);
                    }
                });

    }

    //Validates the users answer agains their actual answer
    public void checkSecretAnswer(){
        Log.d(TAG, "checkSecretAnswer method called");
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String answer = documentSnapshot.getString("secretAnswer");
                            String answerInput = nemIdInput.getText().toString();
                            if(answer.equalsIgnoreCase(answerInput)){
                                check = true;
                                Log.d(TAG, "check have been set to true");
                                Toast.makeText(NemIdActivity.this, "Answer match! Press confirm again, to finalize payment", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "secretAnswer:failure", task.getException());
                            Toast.makeText(NemIdActivity.this, "Secret answer is wrong",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //Handles pressed items on screen
    public void onClick(View view){
        int i = view.getId();

        //Below 'getExtra' is values sent from PaymentActivity. Used for the actual payment, with an instance of AccountService.
        Intent intent = getIntent();

        PaymentParcelable paymentParcelableAuto;

        paymentParcelableAuto = intent.getParcelableExtra("bill");

        String nameBill = paymentParcelableAuto.getBillName();
        String nameDate = paymentParcelableAuto.getDateText();
        String accountSpinner = paymentParcelableAuto.getAccountSpinner();
        int amount = paymentParcelableAuto.getAmount();
        int companyNumber = paymentParcelableAuto.getKontoNummer();



        if(i == R.id.nemIdButton){
            Log.d(TAG, "onClick nemIdButton pressed");

            checkSecretAnswer(); // Calls the method to validate input.

            if (check){

                if (nameBill == null){
                    accService.pay(accountSpinner,companyNumber,amount);
                    Toast.makeText(this, "Paying bill", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Paid bill. Paying: " + amount);
                } else if (nameBill != null){
                    accService.payAuto(accountSpinner,companyNumber,amount,nameDate,nameBill);
                    Toast.makeText(this, "Setting up autobilling", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Autobilling. Setting up payment due: " + nameDate);
                    }

                Log.d(TAG, "Intent after nemIdButton initilized");
                Intent intent2 = new Intent(this, MenuActivity.class);
                startActivity(intent2);
                check = false;

            } else {
                Toast.makeText(this, "Answer does not match. Try again!", Toast.LENGTH_SHORT).show();
                // [ENDS nemIdButton]
            }
        }

    }

    public void init(){
        nemIdCode = findViewById(R.id.nemIdCode);
        nemIdText = findViewById(R.id.nemIdText);
        nemIdInput = findViewById(R.id.nemIdInput);

    }

    @Override // disables the back button
    public void onBackPressed() {
        Log.d(TAG, "Cannot go back to login screen, without pressing 'signout' button");
        return;
    }

}
