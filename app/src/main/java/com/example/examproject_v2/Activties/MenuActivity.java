package com.example.examproject_v2.Activties;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.examproject_v2.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    final static String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void onClick(View view){
        int i = view.getId();

        if (i == R.id.accountButton){
            Log.d(TAG, "onClick: Pressed accountButton");
            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
        } else if (i == R.id.transferButton){
            Intent intent2 = new Intent(this, TransferActivity2.class);
            startActivity(intent2);
        } else if (i == R.id.payButton){
            Intent intent3 = new Intent(this, PaymentActivity.class);
            startActivity(intent3);
        } else if (i == R.id.checkPayButton){
            Intent intent4 = new Intent(this, OverheadPay.class);
            startActivity(intent4);
        } else if(i == R.id.signOutButton){
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG, "onClick: signOut, user is being signed out");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    @Override //
    public void onBackPressed() {
        Log.d(TAG, "Cannot go back to login screen, without pressing 'signout' button");
        return;
    }

}
