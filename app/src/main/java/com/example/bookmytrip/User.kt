package com.example.bookmytrip

data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val location: String,
    val profileImagePath: String? = null
)
