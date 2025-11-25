package com.miapp.lotuz_2

import com.miapp.lotuz_2.utils.AuthUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthUtilsTest {
    @Test
    fun adminOfflineRoleWorks() {
        val role = AuthUtils.offlineRole("admin@lotuz.cl", "123456")
        assertEquals("admin", role)
    }

    @Test
    fun clientOfflineRoleWorks() {
        val role = AuthUtils.offlineRole("cliente@lotuz.com", "123456")
        assertEquals("client", role)
    }

    @Test
    fun invalidCredentialsReturnNull() {
        val role = AuthUtils.offlineRole("foo@bar.com", "badpass")
        assertNull(role)
    }
}

