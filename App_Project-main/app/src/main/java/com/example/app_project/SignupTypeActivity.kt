package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignupTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_type)

        val adminSignupButton = findViewById<Button>(R.id.adminSignupButton)
        val caregiverSignupButton = findViewById<Button>(R.id.caregiverSignupButton)

        adminSignupButton.setOnClickListener {
            // 관리자 회원가입 화면으로 이동
            val intent = Intent(this, AdminSignupActivity::class.java)
            startActivity(intent)
        }

        caregiverSignupButton.setOnClickListener {
            // 보호자 회원가입 화면으로 이동
            val intent = Intent(this, CaregiverSignupActivity::class.java)
            startActivity(intent)
        }
    }
}