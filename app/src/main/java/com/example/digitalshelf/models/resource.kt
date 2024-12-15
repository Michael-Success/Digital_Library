package com.example.digitalshelf.models




data class Resource(
    val name: String,
    val description: String,
    val fileType: String,
    val fileUrl: String,
    val previewUrl: String? = null,
    val drawableResIds: List<Int> = emptyList() // List of drawable resource IDs
)

