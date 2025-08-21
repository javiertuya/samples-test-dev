package com.example.loanmanagement.data.api

import com.example.loanmanagement.data.model.Client
import com.example.loanmanagement.data.model.Loan
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Loan Endpoints ---

    @GET("api/loans")
    suspend fun getLoans(): Response<List<Loan>>

    @GET("api/loans/{id}")
    suspend fun getLoanById(@Path("id") loanId: String): Response<Loan> // Assuming this endpoint returns a single loan object

    @POST("api/loans")
    suspend fun createLoan(@Body loan: Loan): Response<Loan>


    // --- Client Endpoints ---

    @Multipart
    @POST("api/clients")
    suspend fun createClient(
        @Part("name") name: RequestBody,
        @Part("documentId") documentId: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part documentPhoto: MultipartBody.Part
    ): Response<Client>
}
