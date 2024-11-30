package com.example.app_project

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging // FCM import 추가

class AdminSignupActivity : AppCompatActivity() {

    private lateinit var managerEmailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var positionEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        managerEmailEditText = findViewById(R.id.managerEmailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        positionEditText = findViewById(R.id.positionEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        signupButton = findViewById(R.id.adminSignupButton)

        signupButton.setOnClickListener {
            val managerEmail = managerEmailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val position = positionEditText.text.toString().trim()
            val phoneNumber = phoneNumberEditText.text.toString().trim()

            if (TextUtils.isEmpty(managerEmail) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(position) ||
                TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 관리자 이메일 중복 체크 (Firebase Authentication에서 처리됨)
            auth.createUserWithEmailAndPassword(managerEmail, password)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid
                        val managerInfo = mapOf(
                            "managerEmail" to managerEmail,
                            "name" to name,
                            "position" to position,
                            "phoneNumber" to phoneNumber,
                            "role" to "Admin" // 관리자 역할 추가
                        )

                        if (userId != null) {
                            val userRef = database.getReference("users").child(userId)
                            userRef.setValue(managerInfo).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // 관리자 가입 성공 후 FCM 토픽에 구독
                                    FirebaseMessaging.getInstance().subscribeToTopic("admin")
                                        .addOnCompleteListener { topicTask ->
                                            if (topicTask.isSuccessful) {
                                                Toast.makeText(this, "Manager registered and subscribed to adminTopic", Toast.LENGTH_SHORT).show()
                                                finish() // 회원가입 후 로그인 화면으로 돌아가기
                                            } else {
                                                Toast.makeText(this, "Failed to subscribe to admin topic", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(this, "Failed to register manager", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}