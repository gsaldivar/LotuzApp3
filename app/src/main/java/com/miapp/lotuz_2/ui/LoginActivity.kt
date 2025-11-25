package com.miapp.lotuz_2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.miapp.lotuz_2.BuildConfig
import com.miapp.lotuz_2.databinding.ActivityLoginBinding
import com.miapp.lotuz_2.network.RetrofitClient
import com.miapp.lotuz_2.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        if (session.isLoggedIn()) {
            navigateBasedOnRole(session.getUserRole())
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (validEmail && pass.isNotEmpty()) performLogin(email, pass)
            else Toast.makeText(this, "Completa los campos con datos válidos", Toast.LENGTH_SHORT).show()
        }

        binding.btnLoginAdmin.setOnClickListener {
            if (!BuildConfig.DEMO_AUTO_LOGIN) {
                Toast.makeText(this, "Auto-login desactivado en producción", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(BuildConfig.DEMO_ADMIN_USER, BuildConfig.DEMO_ADMIN_PASS)
            }
        }

        binding.btnLoginCliente.setOnClickListener {
            if (!BuildConfig.DEMO_AUTO_LOGIN) {
                Toast.makeText(this, "Auto-login desactivado en producción", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(BuildConfig.DEMO_CLIENT_USER, BuildConfig.DEMO_CLIENT_PASS)
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun performLogin(email: String, pass: String) {
        val session = SessionManager(this)

        if (!session.canAttemptLogin()) {
            val remaining = session.getLockoutRemainingMillis() / 1000
            Toast.makeText(this, "Intentos bloqueados. Espera ${remaining} s", Toast.LENGTH_SHORT).show()
            return
        }

        setButtonsEnabled(false)
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@LoginActivity)
                var loginRes = api.login(mapOf("email" to email, "username" to email, "password" to pass))

                if (loginRes.code() == 404) {
                    loginRes = api.loginPlain(mapOf("email" to email, "username" to email, "password" to pass))
                }

                if (loginRes.isSuccessful && loginRes.body() != null) {
                    val token = loginRes.body()!!.authToken
                    if (token.isNullOrBlank()) {
                        session.recordFailedLogin()
                        Toast.makeText(this@LoginActivity, "Respuesta de login inválida", Toast.LENGTH_SHORT).show()
                        setButtonsEnabled(true)
                        return@launch
                    }
                    session.saveAuthToken(token)
                    val meRes = api.getMe()

                    if (meRes.isSuccessful && meRes.body() != null) {
                        val role = meRes.body()!!.role
                        session.saveUserRole(role)
                        session.resetLoginAttempts()
                        val msg = if (role == "admin") "Bienvenido Administrador" else "Bienvenido Cliente"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                        navigateBasedOnRole(role)
                    } else {
                        session.recordFailedLogin()
                        val code = meRes.code()
                        val err = meRes.errorBody()?.string()?.take(200) ?: ""
                        Toast.makeText(this@LoginActivity, "Error perfil ${code}: ${err}", Toast.LENGTH_SHORT).show()
                    }
                } else if (loginRes.code() == 401) {
                    session.recordFailedLogin()
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas (401)", Toast.LENGTH_SHORT).show()
                } else if (loginRes.code() == 404) {
                    val role = com.miapp.lotuz_2.utils.AuthUtils.offlineRole(email, pass)
                    if (role != null) {
                        session.saveAuthToken("DEMO_TOKEN")
                        session.saveUserRole(role)
                        session.resetLoginAttempts()
                        Toast.makeText(this@LoginActivity, com.miapp.lotuz_2.utils.MessageUtils.loginFallbackMessage(), Toast.LENGTH_SHORT).show()
                        navigateBasedOnRole(role)
                    } else {
                        session.recordFailedLogin()
                        Toast.makeText(this@LoginActivity, "Ruta de login no encontrada (404)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    session.recordFailedLogin()
                    val code = loginRes.code()
                    val err = loginRes.errorBody()?.string()?.take(200) ?: ""
                    Toast.makeText(this@LoginActivity, "Login ${code}: ${err}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val role = com.miapp.lotuz_2.utils.AuthUtils.offlineRole(email, pass)
                if (role != null) {
                    session.saveAuthToken("DEMO_TOKEN")
                    session.saveUserRole(role)
                    session.resetLoginAttempts()
                    Toast.makeText(this@LoginActivity, com.miapp.lotuz_2.utils.MessageUtils.loginFallbackMessage(), Toast.LENGTH_SHORT).show()
                    navigateBasedOnRole(role)
                } else {
                    session.recordFailedLogin()
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            setButtonsEnabled(true)
        }
    }

    private fun navigateBasedOnRole(role: String) {
        val intent = if (role == "admin") Intent(this, AdminHomeActivity::class.java)
        else Intent(this, ClientHomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.btnLoginAdmin.isEnabled = enabled
        binding.btnLoginCliente.isEnabled = enabled
    }

    private fun showForgotPasswordDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Email"
        AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val res = RetrofitClient.getInstance(this@LoginActivity)
                                .forgotPassword(mapOf("email" to email))
                            if (res.isSuccessful) {
                                Toast.makeText(this@LoginActivity, "Email enviado si existe", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "No se pudo enviar", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
