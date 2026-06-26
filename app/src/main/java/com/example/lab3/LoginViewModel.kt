package com.example.lab3

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
        validate()
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        validate()
    }

    private fun validate() {
        errorMessage = when {
            email.isEmpty() || password.isEmpty() -> "Pola nie mogą być puste"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Niepoprawny format adresu e-mail"
            password.length < 6 -> "Hasło musi mieć co najmniej 6 znaków"
            else -> null
        }
    }

    fun login() {
        validate()
        if (errorMessage == null) {
            successMessage = "Zalogowano pomyślnie"
        }
    }
}
