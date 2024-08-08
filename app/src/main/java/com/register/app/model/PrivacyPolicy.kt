package com.register.app.model

data class PrivacyPolicy(
    val id: Int?,
    val content: String,
    val lastUpdated: String,
    val version: Int?,
    val status: String
)
