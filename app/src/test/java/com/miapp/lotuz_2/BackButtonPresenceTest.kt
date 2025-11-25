package com.miapp.lotuz_2

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BackButtonPresenceTest {
    private fun fileHasBtnBack(path: String): Boolean {
        val f = File(path)
        if (!f.exists()) return false
        val content = f.readText()
        return content.contains("@+id/btnBack")
    }

    @Test
    fun cartHasBackButton() {
        assertTrue(fileHasBtnBack("app/src/main/res/layout/activity_cart.xml"))
    }

    @Test
    fun adminAddProductHasBackButton() {
        assertTrue(fileHasBtnBack("app/src/main/res/layout/activity_admin_add_product.xml"))
    }

    @Test
    fun adminOrdersHasBackButton() {
        assertTrue(fileHasBtnBack("app/src/main/res/layout/activity_admin_orders.xml"))
    }
}

