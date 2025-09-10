package com.example.loanmanagement.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.loanmanagement.data.model.Client
import com.example.loanmanagement.data.model.Loan
import com.example.loanmanagement.data.model.LoanWithPaymentsAndClient
import com.example.loanmanagement.data.model.Payment

@Dao
interface LoanDao {

    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<Client>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<Loan>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>)


    // --- Query Operations ---

    @Query("SELECT * FROM clients ORDER BY name ASC")
    suspend fun getAllClients(): List<Client>

    @Transaction
    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun getLoanWithPaymentsAndClient(loanId: String): LoanWithPaymentsAndClient

    @Transaction
    @Query("SELECT * FROM loans ORDER BY issueDate DESC")
    suspend fun getAllLoansWithClient(): List<LoanWithPaymentsAndClient>

    // --- Clear Operations ---

    @Query("DELETE FROM clients")
    suspend fun clearClients()

    @Query("DELETE FROM loans")
    suspend fun clearLoans()

    @Query("DELETE FROM payments")
    suspend fun clearPayments()
}
