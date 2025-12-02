package com.msgharvester.app

object OTPExtractor {
    private val otpRegex = Regex("\\b(\\d{4,8})\\b")
    fun extractOTP(text: String): String? {
        val match = otpRegex.find(text)
        return match?.value
    }
}
