package com.example.loanmanagement.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.loanmanagement.data.LoanRepository
import com.example.loanmanagement.data.api.RetrofitInstance
import com.example.loanmanagement.data.db.AppDatabase
import com.example.loanmanagement.databinding.ActivityMainBinding
import com.example.loanmanagement.viewmodel.MainViewModel
import com.example.loanmanagement.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Dependency Injection (manual) ---
        // In a real app, this would be handled by a library like Hilt or Koin
        val database = AppDatabase.getDatabase(application)
        val apiService = RetrofitInstance.api
        val repository = LoanRepository(apiService, database.loanDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        // --- UI Setup ---
        // TODO: Set up RecyclerView adapter
        // val adapter = LoanAdapter()
        // binding.recyclerView.adapter = adapter

        // --- Observing LiveData ---
        viewModel.loans.observe(this) { loans ->
            // TODO: Submit list to the adapter
            // adapter.submitList(loans)
        }

        viewModel.error.observe(this) { error ->
            // TODO: Show a Toast or Snackbar with the error message
        }

        // --- Initial Data Load ---
        viewModel.fetchAllLoans()
    }
}
