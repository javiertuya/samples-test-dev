package com.example.loanmanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("documentPhoto")
    val documentPhoto: String,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("createdAt")
    val createdAt: String
)
