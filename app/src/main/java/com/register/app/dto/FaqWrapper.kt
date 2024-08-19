package com.register.app.dto

import com.register.app.model.Faq

data class FaqWrapper(
    val message: String,
    val status: Boolean,
    val data: List<Faq>?
)
