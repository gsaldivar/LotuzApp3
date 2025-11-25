package com.miapp.lotuz_2.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName(value = "authToken", alternate = ["token", "auth_token"]) val authToken: String?
)

