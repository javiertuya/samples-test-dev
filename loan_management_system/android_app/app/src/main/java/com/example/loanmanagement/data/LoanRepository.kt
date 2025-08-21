package com.example.loanmanagement.data

import com.example.loanmanagement.data.api.ApiService
import com.example.loanmanagement.data.db.LoanDao
import com.example.loanmanagement.data.model.Loan
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LoanRepository(
    private val apiService: ApiService,
    private val loanDao: LoanDao
) {

    // Example of a function that fetches from network and caches locally
    suspend fun getLoans() {
        try {
            val response = apiService.getLoans()
            if (response.isSuccessful) {
                response.body()?.let { loans ->
                    // Clear old data
                    loanDao.clearLoans()
                    loanDao.clearPayments() // Assuming we get payments with loans or need to clear them
                    // Insert new data
                    loanDao.insertLoans(loans)
                }
            }
        } catch (e: Exception) {
            // Handle network error, maybe log it
            // The app will rely on the cached data in the DAO
        }
    }

    suspend fun createClient(
        name: RequestBody,
        documentId: RequestBody,
        phone: RequestBody?,
        email: RequestBody?,
        address: RequestBody?,
        photo: MultipartBody.Part
    ) = apiService.createClient(name, documentId, phone, email, address, photo)


    suspend fun createLoan(loan: Loan) = apiService.createLoan(loan)

    // Provide access to the DAO's methods for the ViewModel to observe
    fun getAllLoansFromDb() = loanDao.getAllLoansWithClient()
}
