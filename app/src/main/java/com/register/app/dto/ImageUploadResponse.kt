package com.register.app.dto

data class ImageUploadResponse(
    val message: String,
    val status: Boolean,
    val data: ImageData?
)
