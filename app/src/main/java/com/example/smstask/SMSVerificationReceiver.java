package com.example.smstask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SMSVerificationReceiver extends BroadcastReceiver {

    private SMSVerificationListener listener;

    private static final String ONE_TIME_CODE_PATTERN = "\\d+[a-zA-Z0-9]{3,9}";

    private static final int SMS_CONSENT_REQUEST = 2;


    int CONSENT_REQUEST;

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

                    Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);

             //      String message = consentIntent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);

                   listener.onSmsRetrieverSuccess(consentIntent,SMS_CONSENT_REQUEST);

/*
                Pattern pattern = Pattern.compile(ONE_TIME_CODE_PATTERN);
                Matcher matcher = pattern.matcher(message);
                String otp = null;
                if (matcher.find()) {
                    otp = matcher.group(1);
                }
                    if (listener != null) {

                    }

 */
                break;

/*
                    if (message != null && message.length() > 0) {
                        Pattern pattern = Pattern.compile("(\\d{6})");
                        Matcher matcher = pattern.matcher(message);
                        String otp = null;
                        if (matcher.find()) {
                            otp = matcher.group(1);
                        }
                        if (listener != null) {
                            listener.onSmsRetrieverSuccess(consentIntent,otp);
                        }
                    }
                    else {
                        listener.onNullValue("Null Value Received");
                    }
                    break;

 */
                case CommonStatusCodes.TIMEOUT:
                    listener.onSmsRetrieverTimeout();
                    break;

                case CommonStatusCodes.API_NOT_CONNECTED:

                    if (listener != null) {
                        listener.onOTPReceivedError("API NOT CONNECTED");
                    }

                    break;

                case CommonStatusCodes.NETWORK_ERROR:

                    if (listener != null) {
                        listener.onOTPReceivedError("NETWORK ERROR");
                    }

                    break;

                case CommonStatusCodes.ERROR:

                    if (listener != null) {
                        listener.onOTPReceivedError("SOME THING WENT WRONG");
                    }

                    break;
            }
        }

    }
    public interface SMSVerificationListener {

        void onSmsRetrieverSuccess(Intent consent,int CONSENT_REQUEST);

        void onSmsRetrieverTimeout();

        void onOTPReceivedError(String apiNotConnected);

        void onNullValue(String nullValueReceived);
    }
}
