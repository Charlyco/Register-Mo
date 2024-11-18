package com.register.app.dto

import com.register.app.model.ContributionDto

data class SpecialLevy(
    val id: Long?,
    val levyTitle: String?,
    val levyDescription: String?,
    val dateCreated: String?,
    val eventCreator: String?,
    val groupId: Int?,
    val groupName: String?,
    val levyAmount: Double?,
    val payeeEmail: String?,
    val payeeMembershipId: String?,
    val pendingPaymentEvidence: List<Payment>?,
    val confirmedPayments: List<ContributionDto>?,
)
