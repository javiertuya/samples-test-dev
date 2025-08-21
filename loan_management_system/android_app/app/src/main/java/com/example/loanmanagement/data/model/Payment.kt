package com.example.loanmanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Loan::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Payment(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,

    @SerializedName("loan")
    val loanId: String,

    @SerializedName("dueDate")
    val dueDate: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("status")
    val status: String, // "pending" or "paid"

    @SerializedName("paymentDate")
    val paymentDate: String?
)
