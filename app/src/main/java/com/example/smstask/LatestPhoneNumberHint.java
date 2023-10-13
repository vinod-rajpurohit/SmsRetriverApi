package com.example.smstask;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.common.api.ApiException;

public class LatestPhoneNumberHint extends AppCompatActivity {

    private static final int RESOLVE_HINT = 1;
    private SignInClient signInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_phone_number_hint);
        signInClient = Identity.getSignInClient(this);

        getMobileNumbers();

        requestHint();

    }

    private void getMobileNumbers() {


        SignInClient signInClient = Identity.getSignInClient(LatestPhoneNumberHint.this);

        // Request a hint for the phone number for the first SIM card.
        GetPhoneNumberHintIntentRequest request1 = GetPhoneNumberHintIntentRequest.builder().build();
        signInClient.getPhoneNumberHintIntent(request1).addOnSuccessListener(result -> {
            try {
                IntentSender intentSender1 = result.getIntentSender();
                hintResult.launch(new IntentSenderRequest.Builder(intentSender1).build());
            } catch (Exception e) {
                Log.i("Error launching", "error occurred in launching Activity result");
            }
        }).addOnFailureListener(e -> Log.i("Failure occurred", "Failure getting phone number"));

        // Request a hint for the phone number for the second SIM card.
        GetPhoneNumberHintIntentRequest request2 = GetPhoneNumberHintIntentRequest.builder().build();
        signInClient.getPhoneNumberHintIntent(request2).addOnSuccessListener(result -> {
            try {
                IntentSender intentSender2 = result.getIntentSender();
                hintResult.launch(new IntentSenderRequest.Builder(intentSender2).build());
            } catch (Exception e) {
                Log.i("Error launching", "error occurred in launching Activity result");
            }
        }).addOnFailureListener(e -> Log.i("Failure occurred", "Failure getting phone number"));

    }

    private void requestHint() {
        GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();
        Identity.getSignInClient(this)
                .getPhoneNumberHintIntent(request)
                .addOnSuccessListener(result -> {
                    try {
                        IntentSender intentSender = result.getIntentSender();
                        hintResult.launch(new IntentSenderRequest.Builder(intentSender).build());
                    } catch (Exception e) {
                        Log.i("Error launching", "error occurred in launching Activity result");
                    }
                })
                .addOnFailureListener(e -> Log.i("Failure occurred", "Failure getting phone number"));
    }

    ActivityResultLauncher<IntentSenderRequest> hintResult = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if(result!=null){
                    try {
                        String phoneNum = Identity.getSignInClient(getApplicationContext()).getPhoneNumberFromIntent(result.getData());
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
            });
}