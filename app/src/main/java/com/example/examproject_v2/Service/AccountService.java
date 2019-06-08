package com.example.examproject_v2.Service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.examproject_v2.Activties.PaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private final String TAG = "AccountService";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts");
    public int fromVal, toVal; //fromVal is the value that a user tries to transfer. and a variable that updates the receiving users balance.
    public boolean amountChecker, companyChecker;

    public void transfer(final String fromAccount, final String toAccount, final String toEmail, final int transferAmount) {

        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            fromVal = documentSnapshot.getLong("balance").intValue();
                            Log.d(TAG, "transfer method() - printing fromVal: " + fromVal);

                            if (fromVal > transferAmount) {
                                db.collection("Users").document(toEmail).collection("Accounts").document(toAccount)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            toVal = documentSnapshot.getLong("balance").intValue();
                                            fromVal = fromVal - transferAmount;
                                            toVal = toVal + transferAmount;
                                            Log.d(TAG, "transfer method() - printing fromVal: " + fromVal);

                                            db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                                                    .update("balance", fromVal)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                            }
                                                        }
                                                    });
                                            db.collection("Users").document(toEmail).collection("Accounts").document(toAccount)
                                                    .update("balance", toVal)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                        }
                                                    });
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            } else {
                                Log.d(TAG, "ERROR: not enough money on the account you want to transfer from!!!");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void pay(final String fromAccount, final int kontoNummer, final int transferAmount) {

        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            fromVal = documentSnapshot.getLong("balance").intValue();
                            Log.d(TAG, "pay method() - printing fromVal: " + fromVal);

                            if (fromVal > transferAmount) {
                                db.collection("Companies").whereEqualTo("companyNumber", kontoNummer)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String company = document.getId();
                                                toVal = document.getLong("balance").intValue();
                                                fromVal = fromVal - transferAmount;
                                                toVal = toVal + transferAmount;
                                                Log.d(TAG, "Senders balance after payment: " + fromVal + ". " + company + " new balance: " + toVal);
                                                db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                                                        .update("balance", fromVal);
                                                db.collection("Companies").document(company)
                                                        .update("balance", toVal);
                                            }
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "ERROR: not enough money on the account you want to transfer from!!!");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void payAuto(final String fromAccount, final int kontoNummer, final int transferAmount, final String date, final String billName) {
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            fromVal = documentSnapshot.getLong("balance").intValue();
                            Log.d(TAG, "payAuto method() - printing fromVal: " + fromVal);

                            if (fromVal > transferAmount) {
                                db.collection("Companies").whereEqualTo("companyNumber", kontoNummer)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String company = document.getId();
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                                Date date2 = new Date();
                                                if (date.equals(dateFormat.format(date2))) {
                                                    toVal = document.getLong("balance").intValue();
                                                    fromVal = fromVal - transferAmount;
                                                    toVal = toVal + transferAmount;
                                                    Log.d(TAG, "Senders balance after payment: " + fromVal + ". " + company + " new balance: " + toVal);
                                                    db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                                                            .update("balance", fromVal);
                                                    db.collection("Companies").document(company)
                                                            .update("balance", toVal);
                                                }

                                                Map<String, Object> data = new HashMap<>();
                                                data.put("name", billName);
                                                data.put("nextDate", date);
                                                data.put("fromAccount", fromAccount);
                                                data.put("toKontoNummer", kontoNummer);
                                                data.put("amount", transferAmount);
                                                db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document("AutomaticPayments").collection("Bills").document("AUTOPAY: " + billName + "_" + kontoNummer).set(data);
                                                Log.d(TAG, "Adding a new auto bill: " + billName + "set for " + date);
                                            }
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "ERROR: not enough money on the account you want to transfer from!!!");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    // checks if the amount that user wants to transfer, is OK. -> TransferAct3 & PaymentAct
    public boolean balanceChecker(final int transferAmount, final String fromAccount) {
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document(fromAccount)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            fromVal = documentSnapshot.getLong("balance").intValue();
                            Log.d(TAG, "balanceChecker, checking if user can transfer: " + fromVal);

                            if (fromVal > transferAmount) {
                                amountChecker = true;
                                Log.d(TAG, "boolean have been set to true");
                            }
                        }
                    }

                });

        if (amountChecker) {
            Log.d(TAG,"amountChecker is true");
            return true;
        } else {
            Log.d(TAG,"amountChecker is false");
            return false;
        }

    }

   /* public void deleteBill(final String billName){
        usersRef.collection("AutomaticPayments").document("Bills").


    }*/

   /*public void accountActivity(final int amount, final String inOut, final String account, final String toAccount){
       if (inOut.equalsIgnoreCase("in")){
           Map<String, Object> data = new HashMap<>();
           data.put("name", amount);
           db.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Accounts").document("AutomaticPayments").collection("Bills").document("-" + amount + ", from: " + toAccount).set(data);

       } else {

       }
       usersRef.document(account).collection("activity").add()

   }*/

}

