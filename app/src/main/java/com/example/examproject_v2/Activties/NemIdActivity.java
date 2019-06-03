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

import com.example.examproject_v2.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NemIdActivity extends AppCompatActivity {
    private static final String TAG = "NemIdActivtity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean check = false;
    TextView nemIdText, nemIdCode;
    EditText nemIdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nem_id);
        init();
        getSecretQuestion();
    }

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

    public void onClick(View view){
        int i = view.getId();
        if(i == R.id.nemIdButton){
            Log.d(TAG, "onClick nemIdButton pressed");
            checkSecretAnswer();
            if (check){
                Log.d(TAG, "Intent after nemIdButton initilized");
                Intent intent = new Intent(this, OverviewActivity.class);
                startActivity(intent);
                check = false;
            }
        }

    }

    public void init(){
        nemIdCode = findViewById(R.id.nemIdCode);
        nemIdText = findViewById(R.id.nemIdText);
        nemIdInput = findViewById(R.id.nemIdInput);
      //  database = FirebaseDatabase.getInstance();
    }

}
