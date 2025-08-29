package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthenticationScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnNavigate1: Button =findViewById(R.id.buttonsigninAuthentication)
        btnNavigate1.setOnClickListener{
            val intent= Intent(this,SignInScreen::class.java)
            startActivity(intent)
        }

        val btnNavigate2: Button =findViewById(R.id.buttonsignupAuthentication)
        btnNavigate2.setOnClickListener{
            val intent= Intent(this,SignUpScreen::class.java)
            startActivity(intent)
        }
    }
}