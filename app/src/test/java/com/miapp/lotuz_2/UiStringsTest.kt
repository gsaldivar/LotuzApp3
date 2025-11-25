package com.miapp.lotuz_2

import com.miapp.lotuz_2.utils.MessageUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStringsTest {
    @Test
    fun loginFallbackIsProfessional() {
        assertEquals("Sesión iniciada", MessageUtils.loginFallbackMessage())
    }

    @Test
    fun checkoutSuccessMessageIsSet() {
        assertEquals("Compra realizada. Orden pendiente", MessageUtils.checkoutSuccessMessage())
    }
}

