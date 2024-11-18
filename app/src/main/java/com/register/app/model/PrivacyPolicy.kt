package com.register.app.model

data class PrivacyPolicyResponse(
    val message: String,
    val status: Boolean,
    val data: PrivacyPolicy?
)

data class PrivacyPolicy(
    val id: Int?,
    var content: String,
    val lastUpdated: String,
    val version: Int,
    val status: String
)

