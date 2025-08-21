package com.example.loanmanagement.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class LoanWithPaymentsAndClient(
    @Embedded
    val loan: Loan,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client,

    @Relation(
        parentColumn = "id",
        entityColumn = "loanId"
    )
    val payments: List<Payment>
)
