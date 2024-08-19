package com.register.app.dto

data class BulkPaymentWrapper(val message: String, val status: Boolean, val data: List<BulkPaymentModel>?)