package com.example.smstask;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmsRetrivalinConsetApi extends AppCompatActivity implements SMSVerificationReceiver.SMSVerificationListener {

    private static final int CREDENTIAL_PICKER_REQUEST = 1;

    private SMSVerificationReceiver smsVerificationReceiver;

    private static final int RESOLVE_HINT = 1;

    private static final int SMS_CONSENT_REQUEST = 2;

    String realotp;

    EditText cosentapiotp,Received_Number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_retrivalin_conset_api);

        cosentapiotp = findViewById(R.id.received_otp3);
        Received_Number = findViewById(R.id.received_number_4);

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        }



        startSMSListener2();

    }

    private void startSMSListener2() {


        smsVerificationReceiver = new SMSVerificationReceiver();

        smsVerificationReceiver.setListener(this);


        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);


        registerReceiver(smsVerificationReceiver, intentFilter);

        // Start listening for SMS User Consent broadcasts
        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(null);



        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void requestHint()  throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string

                    Received_Number.setText(credential.getId().substring(3));
                }
/*
                else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
                {
                    Toast.makeText(SmsRetrivalinConsetApi.this, "No phone numbers found", Toast.LENGTH_LONG).show();
                }




                break;


        }
    }

 */

    @Override
    public void onSmsRetrieverSuccess(Intent consent,int CONSENT_REQUEST) {
        startActivityForResult(consent, SMS_CONSENT_REQUEST);

        }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // ...
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {

                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);

                    String oneTimeCode = parseOneTimeCode(message);
                    cosentapiotp.setText(oneTimeCode);


                } else {

                }
                break;
        }
    }

 */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    Received_Number.setText(credential.getId().substring(3));
                }
                break;

            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    String oneTimeCode = parseOneTimeCode(message);
                    cosentapiotp.setText(oneTimeCode);
                }
                break;
        }
    }

    private String parseOneTimeCode(String message) {

        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(message);
        String otp = null;
        if (matcher.find()) {
            otp = matcher.group(1);
        }
        else {
            otp= "Invalid Otp";
        }

     return otp;
    }

    @Override
    public void onSmsRetrieverTimeout() {
        Toast.makeText(SmsRetrivalinConsetApi.this,"Timeout",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOTPReceivedError(String apiNotConnected) {
        Toast.makeText(SmsRetrivalinConsetApi.this,"OTP Received Error",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNullValue(String nullValueReceived) {
        Toast.makeText(SmsRetrivalinConsetApi.this,"Null OTP Received",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsVerificationReceiver != null) {
            unregisterReceiver(smsVerificationReceiver);
        }
    }
}