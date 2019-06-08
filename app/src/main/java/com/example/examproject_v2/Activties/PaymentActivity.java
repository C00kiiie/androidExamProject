package com.example.examproject_v2.Activties;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examproject_v2.Model.PaymentParcelable;
import com.example.examproject_v2.R;
import com.example.examproject_v2.Service.AccountService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private final String TAG = "PaymentActivity";

    TextView paymentDate;
    EditText kontoNummer, paymentAmount, billName;
    Spinner accountSpinner;
    CheckBox checkBox;
    Switch dateSwitch;
    AccountService accService = new AccountService();
    private int companyNumber, amount;
    private String fromSpinnerText, bil, dateText;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PaymentParcelable parcelable = new PaymentParcelable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();
        dateSwitch.setChecked(true);
        spinnerSetup();
        switchSetup();

        paymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PaymentActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "-" + day + "-" + year);
                // If month is above 9 DONT add a '0' infront, this is to get date from firebase
                if (month <= 9 && day <= 9){
                    String date = "0" + month + "-0" + day + "-" + year;
                    paymentDate.setText(date);
                } else if (month <= 9 && day > 9){
                    String date = "0" + month + "-" + day + "-" + year;
                    paymentDate.setText(date);
                } else if (month > 9 && day <= 9){
                    String date = month + "-0" + day + "-" + year;
                    paymentDate.setText(date);
                } else {
                    String date = month + "-" + day + "-" + year;
                    paymentDate.setText(date);
                }

            }
        };

    }

    public void onClick(View view) {
        int i = view.getId();

        if (checkBox.isChecked()) {

            if (!validateForm()) {
                checkBox.setChecked(false);
                return;
            }

            amount = Integer.parseInt(paymentAmount.getText().toString());
            String fromSpinnerText = accountSpinner.getSelectedItem().toString();
            companyNumber = Integer.parseInt(kontoNummer.getText().toString());

            if (accService.balanceChecker(amount, fromSpinnerText)){
                    Log.d(TAG, "User Exists");
                    Log.d(TAG, "checked if amount can be transfered");
            }

        }

        if (i == R.id.paymentConfirmButton){
            if (!validateForm()) {
                return;
            }
        }

        if (i == R.id.paymentConfirmButton && checkBox.isChecked()){

            final Intent intent = new Intent(this, NemIdActivity.class);

            if (!validateForm()) {
                return;
            }

            companyNumber = Integer.parseInt(kontoNummer.getText().toString());
            amount = Integer.parseInt(paymentAmount.getText().toString());
            bil = billName.getText().toString();
            dateText = paymentDate.getText().toString();
            fromSpinnerText = accountSpinner.getSelectedItem().toString();

            CollectionReference companyRef = db.collection("Companies");
            Query query = companyRef.whereEqualTo("companyNumber", companyNumber);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            int company = documentSnapshot.getLong("companyNumber").intValue();
                            if(company == companyNumber){
                                if (accService.balanceChecker(amount, fromSpinnerText)){

                                    if (dateSwitch.isChecked()){
                                        parcelable.setBillName(bil);
                                        parcelable.setDateText(dateText);
                                        intent.putExtra("bill", parcelable);
                                        Log.d(TAG, "Trying to set up autobilling. senting to NemId, to finish");

                                    } else if (!dateSwitch.isChecked()){
                                        Log.d(TAG, "Trying to pay bill. senting to NemId, to finish");
                                    }
                                    Log.d(TAG, "transfer amount is good. Senting to NemId");
                                    parcelable.setAmount(amount);
                                    parcelable.setAccountSpinner(fromSpinnerText);
                                    parcelable.setKontoNummer(companyNumber);
                                    intent.putExtra("bill", parcelable);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "Dont have amount to transfer");
                                    Toast.makeText(PaymentActivity.this,"You dont have " + amount + " to transfer", Toast.LENGTH_LONG).show();
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    } if(task.getResult().size() == 0 ){
                        Log.d(TAG, "Company not Exists");
                        Toast.makeText(PaymentActivity.this, "Company does not exists", Toast.LENGTH_SHORT).show();
                        //You can store new user information here
                        checkBox.setChecked(false);
                    }
                }
            });
        }
    }

    public void spinnerSetup() {

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
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "spinnerSetup:failure", task.getException());
                            Toast.makeText(PaymentActivity.this, "Could't find accounts for dropdown",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure, fetching accounts for spinnerSetup" + e.getMessage());
            }
        });
    }

    public void switchSetup() {
        dateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    paymentDate.setVisibility(View.VISIBLE);
                    billName.setVisibility(View.VISIBLE);
                } else {
                    paymentDate.setVisibility(View.GONE);
                    billName.setVisibility(View.GONE);
                }
            }
        });


    }

    public void init(){
        kontoNummer = findViewById(R.id.paymentKontoNummer);
        accountSpinner = findViewById(R.id.paymentSpinner);
        paymentAmount = findViewById(R.id.paymentAmount);
        dateSwitch = findViewById(R.id.paymentSwitch);
        billName = findViewById(R.id.paymentBillName);
        paymentDate = findViewById(R.id.paymentDate);
        checkBox = findViewById(R.id.checkBoxPayment);
    }

    private boolean validateForm () {
        boolean valid = true;

        String kontonummer = kontoNummer.getText().toString();
        if (TextUtils.isEmpty(kontonummer)) {
            kontoNummer.setError("Required.");
            valid = false;
        } else {
            kontoNummer.setError(null);
        }

        String amount = paymentAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            paymentAmount.setError("Required.");
            valid = false;
        } else {
            paymentAmount.setError(null);
        }

        if (!checkBox.isChecked()){
            checkBox.setError("Required");
            valid = false;
        } else {
            checkBox.setError(null);
        }

        if (dateSwitch.isChecked()){
            String bill = billName.getText().toString();
            if (TextUtils.isEmpty(bill)) {
                billName.setError("Required.");
                valid = false;
            } else if (bill.length() > 8){
                billName.setError("To long name");
                valid = false;
            } else {
                billName.setError(null);
            }

            String date = paymentDate.getText().toString();
            if (TextUtils.isEmpty(date)) {
                paymentDate.setError("Required.");
                valid = false;
            } else {
                paymentDate.setError(null);
            }
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PaymentActivity.this, "Canceling payment",Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}
