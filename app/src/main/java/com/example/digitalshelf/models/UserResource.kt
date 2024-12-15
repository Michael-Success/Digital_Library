package com.example.digitalshelf.models


data class UserResource(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val storagePath: String? = null // Ensure this property exists
)
