package com.miapp.lotuz_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miapp.lotuz_2.ui.AdminHomeActivity
import com.miapp.lotuz_2.ui.ClientHomeActivity
import com.miapp.lotuz_2.ui.LoginActivity
import com.miapp.lotuz_2.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate de que este layout exista

        val session = SessionManager(this)

        if (session.isLoggedIn()) {
            // SI hay usuario guardado, verificamos rol y redirigimos
            val role = session.getUserRole()
            if (role == "admin") {
                startActivity(Intent(this, AdminHomeActivity::class.java))
            } else {
                startActivity(Intent(this, ClientHomeActivity::class.java))
            }
        } else {
            // NO hay usuario, mandamos al Login
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Importante: finish() mata esta actividad para que el usuario
        // no pueda volver a ella presionando "Atrás".
        finish()
    }
}