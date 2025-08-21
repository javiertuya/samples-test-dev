package com.example.loanmanagement.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.loanmanagement.data.model.Client
import com.example.loanmanagement.data.model.Loan
import com.example.loanmanagement.data.model.Payment

@Database(entities = [Client::class, Loan::class, Payment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun loanDao(): LoanDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loan_management_database"
                )
                .fallbackToDestructiveMigration() // Not for production apps! For dev only.
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
