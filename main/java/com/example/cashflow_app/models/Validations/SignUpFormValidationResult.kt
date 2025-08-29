package com.example.financial_tracker.models.Validations

import androidx.core.app.NotificationCompat.MessagingStyle.Message

sealed class SignUpFormValidationResult {
    data class Empty (val errorMessage: String):SignUpFormValidationResult()
    data class Invalid (val errorMessage: String):SignUpFormValidationResult()
    object Valid:SignUpFormValidationResult()
}