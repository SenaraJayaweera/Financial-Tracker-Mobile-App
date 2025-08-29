package com.example.financial_tracker.models.Validations

import androidx.core.app.NotificationCompat.MessagingStyle.Message

sealed class LoginFormValidationResult {
    data class Empty(val errorMessage: String):LoginFormValidationResult()
    data class Invalid(val errorMessage: String):LoginFormValidationResult()
    object Valid:LoginFormValidationResult()

}