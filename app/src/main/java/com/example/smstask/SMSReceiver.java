package com.example.smstask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private OTPReceiveListener otpListener;

    private static final int SMS_CONSENT_REQUEST = 2;
    public void setOTPListener(OTPReceiveListener otpListener) {
        this.otpListener = otpListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:

                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    Pattern pattern = Pattern.compile("(\\d{6})");
                    Matcher matcher = pattern.matcher(message);
                    String otp = null;
                    if (matcher.find()) {
                        otp = matcher.group(1);
                    }
                    if (otpListener != null) {
                        otpListener.onOTPReceived(otp);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:

                    if (otpListener != null) {
                        otpListener.onOTPTimeOut();
                    }
                    break;

                case CommonStatusCodes.API_NOT_CONNECTED:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("API NOT CONNECTED");
                    }

                    break;

                case CommonStatusCodes.NETWORK_ERROR:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("NETWORK ERROR");
                    }

                    break;

                case CommonStatusCodes.ERROR:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("SOME THING WENT WRONG");
                    }

                    break;

            }
        }
    }
    public interface OTPReceiveListener {

        void onOTPReceived(String otp);

        void onOTPTimeOut();

        void onOTPReceivedError(String error);
    }

}
