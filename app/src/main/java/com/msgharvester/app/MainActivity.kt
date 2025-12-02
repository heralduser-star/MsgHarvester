package com.msgharvester.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnShowLastOtp: Button
    private var lastOtp: String? = null

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val receiveGranted = perms[Manifest.permission.RECEIVE_SMS] == true
            val readGranted = perms[Manifest.permission.READ_SMS] == true
            tvStatus.text = if (receiveGranted) "SMS permission granted" else "SMS permission denied"
            // Start service to read existing SMS if permissions granted
            if (receiveGranted) {
                startService(Intent(this, ReadAllSmsService::class.java))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnShowLastOtp = findViewById(R.id.btnShowLastOtp)

        btnShowLastOtp.setOnClickListener {
            lastOtp = OTPStore.lastOtp
            if (!lastOtp.isNullOrEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Latest OTP")
                    .setMessage(lastOtp)
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("No OTP")
                    .setMessage("No OTP captured yet. Wait for an incoming OTP message.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        checkPermissions()

        // If opened via intent from receiver when OTP captured
        intent?.let {
            val otp = it.getStringExtra("otp")
            val full = it.getStringExtra("full_sms")
            if (!otp.isNullOrEmpty()) {
                showOtpDialog(otp, full)
            }
        }
    }

    private fun showOtpDialog(otp: String, fullSms: String?) {
        AlertDialog.Builder(this)
            .setTitle("OTP detected: $otp")
            .setMessage("Full SMS:\n${'$'}{fullSms ?: "(not available)"}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun checkPermissions() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.READ_SMS)
        }
        if (needed.isNotEmpty()) {
            requestPermissions.launch(needed.toTypedArray())
        } else {
            tvStatus.text = "SMS permissions already granted"
            startService(Intent(this, ReadAllSmsService::class.java))
        }
    }
}
