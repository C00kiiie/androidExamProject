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

    //Handles pressed items on screen
    public void onClick(View view){
        int i = view.getId();

        //set up new intent
        final Intent menuIntent = new Intent(this, MenuActivity.class);

        //receiving info from a parcelable, from PaymentActivity
        Intent intent = getIntent();

        // create an instance of a parcelable
        PaymentParcelable paymentParcelableAuto;
        paymentParcelableAuto = intent.getParcelableExtra("bill"); //Parcelable key = 'bill'
        final String nameBill = paymentParcelableAuto.getBillName();
        final String nameDate = paymentParcelableAuto.getDateText();
        final String accountSpinner = paymentParcelableAuto.getAccountSpinner();
        final int amount = paymentParcelableAuto.getAmount();
        final int companyNumber = paymentParcelableAuto.getKontoNummer();

        // If match between answer and question, pay the bill after pressing nemIdButton
        if(i == R.id.nemIdButton){
            Log.d(TAG, "onClick nemIdButton pressed");

            db.collection("Users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String answer = documentSnapshot.getString("secretAnswer");
                            String answerInput = nemIdInput.getText().toString();

                            // If answer matches question
                            if(answer.equalsIgnoreCase(answerInput)){
                                if (nameBill == null){
                                    accService.pay(accountSpinner,companyNumber,amount);
                                    Toast.makeText(NemIdActivity.this, "Paying bill", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Paid bill. Paying: " + amount);
                                } else if (nameBill != null){
                                    accService.payAuto(accountSpinner,companyNumber,amount,nameDate,nameBill);
                                    Toast.makeText(NemIdActivity.this, "Setting up autobilling", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Autobilling. Setting up payment due: " + nameDate);
                                }

                                startActivity(menuIntent);
                                Log.d(TAG, "Intent after nemIdButton initilized");

                            } else {
                                // If check fails, display a message to the user.
                                Log.w(TAG, "secretAnswer:failure", task.getException());
                                Toast.makeText(NemIdActivity.this, "Secret answer is wrong",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                });
        }

    }

    // validates fields in activity
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
