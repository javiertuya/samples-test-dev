package com.example.loanmanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Loan(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,

    @SerializedName("client")
    val clientId: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("interestRate")
    val interestRate: Double,

    @SerializedName("totalAmount")
    val totalAmount: Double,

    @SerializedName("installments")
    val installments: Int,

    @SerializedName("installmentAmount")
    val installmentAmount: Double,

    @SerializedName("period")
    val period: String, // "daily" or "weekly"

    @SerializedName("issueDate")
    val issueDate: String,

    @SerializedName("status")
    val status: String // "active" or "paid"
)
