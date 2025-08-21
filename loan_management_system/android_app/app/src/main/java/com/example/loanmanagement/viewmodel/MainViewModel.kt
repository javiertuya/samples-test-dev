package com.example.loanmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loanmanagement.data.LoanRepository
import com.example.loanmanagement.data.model.LoanWithPaymentsAndClient
import kotlinx.coroutines.launch

class MainViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _loans = MutableLiveData<List<LoanWithPaymentsAndClient>>()
    val loans: LiveData<List<LoanWithPaymentsAndClient>> get() = _loans

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAllLoans() {
        viewModelScope.launch {
            try {
                // First, try to refresh data from the network
                repository.getLoans()
                // Then, load the (potentially updated) data from the local DB
                _loans.postValue(repository.getAllLoansFromDb())
            } catch (e: Exception) {
                _error.postValue("Failed to fetch loans: ${e.message}")
                // In case of network error, still try to load from DB
                _loans.postValue(repository.getAllLoansFromDb())
            }
        }
    }
}
