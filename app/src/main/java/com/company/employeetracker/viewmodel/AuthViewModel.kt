package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val user = userDao.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _loginError.value = null
                    onSuccess()
                } else {
                    _loginError.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun clearError() {
        _loginError.value = null
    }
}