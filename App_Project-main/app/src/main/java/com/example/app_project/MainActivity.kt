package com.example.app_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // 현재 사용자 확인
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 사용자가 로그인되어 있는 경우 DashboardActivity로 이동
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else {
            // 사용자가 로그인되어 있지 않은 경우 LoginActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // MainActivity 종료
        finish()
    }
}

