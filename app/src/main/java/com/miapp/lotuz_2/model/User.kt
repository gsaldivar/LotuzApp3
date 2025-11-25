package com.miapp.lotuz_2.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName(value = "role", alternate = ["user_role"]) val role: String
)
