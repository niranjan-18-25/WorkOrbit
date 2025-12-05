package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _employees = MutableStateFlow<List<User>>(emptyList())
    val employees: StateFlow<List<User>> = _employees

    private val _employeeCount = MutableStateFlow(0)
    val employeeCount: StateFlow<Int> = _employeeCount

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            userDao.getAllEmployees().collect { employeeList ->
                _employees.value = employeeList
                _employeeCount.value = employeeList.size
            }
        }
    }

    fun addEmployee(user: User) {
        viewModelScope.launch {
            userDao.insertUser(user)
        }
    }

    fun updateEmployee(user: User) {
        viewModelScope.launch {
            userDao.updateUser(user)
        }
    }

    fun deleteEmployee(user: User) {
        viewModelScope.launch {
            userDao.deleteUser(user)
        }
    }

    suspend fun getEmployeeById(id: Int): User? {
        return userDao.getUserById(id)
    }
}