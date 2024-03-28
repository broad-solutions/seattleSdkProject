package com.example.webviewdemo

data class Datautils(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String,
    val jti: String
)
