package com.miapp.lotuz_2.utils

object AuthUtils {
    fun offlineRole(email: String, pass: String): String? {
        val e = email.trim().lowercase()
        val p = pass.trim()
        if (p != "123456") return null
        return when (e) {
            "admin@lotuz.cl", "admin@lotuz.com" -> "admin"
            "cliente@lotuz.cl", "cliente@lotuz.com" -> "client"
            else -> null
        }
    }
}

