package com.example.smstask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;


public class SMSVerificationReceiver extends BroadcastReceiver {

    private SMSVerificationListener listener;

    public void setListener(SMSVerificationListener listener) {
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (smsRetrieverStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get consent intent
                    Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);

                    // Start activity to show consent dialog to user
                    // activity must be started in 5 minutes, otherwise you'll receive another TIMEOUT intent
                    listener.onSmsRetrieverSuccess(consentIntent);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Time out occurred, handle the error.
                    listener.onSmsRetrieverTimeout();
                    break;
            }
        }



    }
    public interface SMSVerificationListener {

        void onSmsRetrieverSuccess(Intent otp);

        void onSmsRetrieverTimeout();

    }
}
