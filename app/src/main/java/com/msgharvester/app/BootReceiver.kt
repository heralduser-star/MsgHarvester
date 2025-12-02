package com.msgharvester.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted - scheduling initial SMS read")
            val s = Intent(context, ReadAllSmsService::class.java)
            s.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startService(s)
        }
    }
}
