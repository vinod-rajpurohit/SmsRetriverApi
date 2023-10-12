package com.example.smstask;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;


public class SmsRetrivalinConsetApi extends AppCompatActivity implements SMSVerificationReceiver.SMSVerificationListener {

    private static final int CREDENTIAL_PICKER_REQUEST = 1;

    private SMSVerificationReceiver smsVerificationReceiver;

    private static final int RESOLVE_HINT = 1;

    private static final int SMS_CONSENT_REQUEST = 2;

    String realotp;

    EditText cosentapiotp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_retrivalin_conset_api);

        cosentapiotp = findViewById(R.id.received_otp3);

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        }
        // Create an instance of the broadcast receiver class.
        smsVerificationReceiver = new SMSVerificationReceiver();

        // Set a listener for the broadcast receiver.
        smsVerificationReceiver.setListener(this);

        // Register the broadcast receiver.
        registerReceiver(smsVerificationReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));

    }

    private void requestHint()  throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                }
                break;
        }
    }


    @Override
    public void onSmsRetrieverSuccess(Intent otp) {
         realotp = otp.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
        cosentapiotp.setText(realotp);

        startActivityForResult(otp, SMS_CONSENT_REQUEST);
    }

    @Override
    public void onSmsRetrieverTimeout() {
        Toast.makeText(SmsRetrivalinConsetApi.this,"Timeout",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsVerificationReceiver != null) {
            unregisterReceiver(smsVerificationReceiver);
        }
    }
}