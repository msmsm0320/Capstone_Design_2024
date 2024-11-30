package com.example.app_project

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class CaregiverSignupActivity : AppCompatActivity() {

    private lateinit var caregiverEmailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var relationshipEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var seniorIDEditText: EditText // 시니어 ID 입력 필드 추가
    private lateinit var signupButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        caregiverEmailEditText = findViewById(R.id.caregiverEmailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        relationshipEditText = findViewById(R.id.relationshipEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        seniorIDEditText = findViewById(R.id.seniorIDEditText) // 시니어 ID EditText 초기화
        signupButton = findViewById(R.id.caregiverSignupButton)

        signupButton.setOnClickListener {
            val caregiverEmail = caregiverEmailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val relationship = relationshipEditText.text.toString().trim()
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val seniorID = seniorIDEditText.text.toString().trim() // 시니어 ID 값 가져오기

            if (TextUtils.isEmpty(caregiverEmail) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(relationship) ||
                TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(seniorID)) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(caregiverEmail, password)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid
                        val caregiverInfo = mapOf(
                            "caregiverEmail" to caregiverEmail,
                            "name" to name,
                            "relationship" to relationship,
                            "phoneNumber" to phoneNumber,
                            "seniorID" to seniorID, // 시니어 ID 추가
                            "role" to "Caregiver" // 보호자 역할 추가
                        )

                        if (userId != null) {
                            val userRef = database.getReference("users").child(userId)
                            userRef.setValue(caregiverInfo).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Caregiver registered successfully", Toast.LENGTH_SHORT).show()
                                    getRoomIDAndSubscribe(seniorID)
                                    finish() // 회원가입 후 로그인 화면으로 돌아가기
                                } else {
                                    Toast.makeText(this, "Failed to register caregiver", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    // SeniorID로 roomID를 조회한 후 해당 roomID 주제에 구독
    private fun getRoomIDAndSubscribe(seniorID: String) {
        val patientRef = FirebaseDatabase.getInstance().getReference("seniors").child(seniorID)
        patientRef.child("roomID").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomID = snapshot.getValue(String::class.java)
                Log.d("room","Room number is $roomID")
                if (roomID != null) {
                    // roomID에 해당하는 주제를 구독
                    FirebaseMessaging.getInstance().subscribeToTopic(roomID)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("FCM", "$roomID 주제 구독 성공")
                                Toast.makeText(this@CaregiverSignupActivity, "Subscribed to Room $roomID topic", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.w("FCM", "주제 구독 실패: ${task.exception?.message}")
                                Toast.makeText(this@CaregiverSignupActivity, "Failed to subscribe to Room $roomID topic", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this@CaregiverSignupActivity, "Room ID not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CaregiverSignupActivity, "Error fetching room ID: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}