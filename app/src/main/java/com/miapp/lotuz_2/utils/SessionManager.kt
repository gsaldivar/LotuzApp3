package com.miapp.lotuz_2.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(private val context: Context) {

    private val PREF_NAME = "LotuzSecurePrefs"
    private val KEY_TOKEN = "auth_token"
    private val KEY_ROLE = "user_role"
    private val KEY_FAILED_ATTEMPTS = "failed_attempts"
    private val KEY_LOCKOUT_UNTIL = "lockout_until"

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val editor = prefs.edit()

    fun saveAuthToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUserRole(role: String) {
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getUserRole(): String = prefs.getString(KEY_ROLE, "client") ?: "client"

    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun canAttemptLogin(): Boolean {
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        return System.currentTimeMillis() >= lockoutUntil
    }

    fun recordFailedLogin() {
        val attempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        editor.putInt(KEY_FAILED_ATTEMPTS, attempts)
        if (attempts >= 5) {
            val lockoutMillis = 30 * 1000L
            editor.putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + lockoutMillis)
            editor.putInt(KEY_FAILED_ATTEMPTS, 0)
        }
        editor.apply()
    }

    fun resetLoginAttempts() {
        editor.putInt(KEY_FAILED_ATTEMPTS, 0)
        editor.putLong(KEY_LOCKOUT_UNTIL, 0L)
        editor.apply()
    }

    fun getLockoutRemainingMillis(): Long {
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        return (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0L)
    }
}
