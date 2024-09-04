package com.register.app.dto

data class SpecialLeviesWrapper(
    val message: String,
    val status: Boolean,
    val data: List<SpecialLevy>?
)
