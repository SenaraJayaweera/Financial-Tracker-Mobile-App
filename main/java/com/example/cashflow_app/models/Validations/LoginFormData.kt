package com.example.financial_tracker.models.Validations

import android.util.Patterns


class LoginFormData (
    private var email:String,
    private var password:String,
)
{
    fun validateEmail():LoginFormValidationResult{
        return if(email.isEmpty()){
            LoginFormValidationResult.Empty("Email is Empty")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            LoginFormValidationResult.Invalid("Invalid email format")
        }else{
            LoginFormValidationResult.Valid
        }
    }

    fun validatePassword():LoginFormValidationResult{
        return if(password.isEmpty()){
            LoginFormValidationResult.Empty("Password is Empty")
        }else if(password.length<8){
            LoginFormValidationResult.Invalid("Min length is 8?")
        }else{
            LoginFormValidationResult.Valid
        }
    }
}