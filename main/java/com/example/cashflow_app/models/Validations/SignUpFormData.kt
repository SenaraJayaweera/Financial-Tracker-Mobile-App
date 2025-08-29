package com.example.financial_tracker.models.Validations

import android.util.Patterns

class SignUpFormData (
     var name:String,
     var email:String,
     var password:String,
     var reenterPassword:String,
     var agree:Boolean,
){
    fun validateName():SignUpFormValidationResult{
        return if(name.isEmpty()){
            SignUpFormValidationResult.Empty("Name is Empty")
        }else{
            SignUpFormValidationResult.Valid
        }
    }

    fun validateEmail():SignUpFormValidationResult{
        return if(email.isEmpty()){
            SignUpFormValidationResult.Empty("Email is empty")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            SignUpFormValidationResult.Invalid("Invalid Email Format")
        }else{
            SignUpFormValidationResult.Valid
        }
    }


    fun validatePassword():SignUpFormValidationResult{
        return if(password.isEmpty()){
            SignUpFormValidationResult.Empty("Password is empty")
        }else if(password.length<8){
            SignUpFormValidationResult.Invalid("Min length is 8")
        }else{
            SignUpFormValidationResult.Valid
        }
    }

    fun validReEnterPassword():SignUpFormValidationResult{
        return if(reenterPassword.isEmpty()){
            SignUpFormValidationResult.Empty("Re-enter password is empty")
        }else if(reenterPassword!=password){
            SignUpFormValidationResult.Invalid("Password do not matched")
        }else{
            SignUpFormValidationResult.Valid
        }
    }

    fun validateAgreement():SignUpFormValidationResult{
        return if(!agree){
            SignUpFormValidationResult.Invalid("You must agree for the terms and conditions")
        }else{
            SignUpFormValidationResult.Valid
        }
    }
}