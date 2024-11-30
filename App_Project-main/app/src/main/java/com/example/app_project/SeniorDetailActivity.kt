package com.example.app_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SeniorDetailActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var roomIDTextView: TextView
    private lateinit var admissionDateTextView: TextView
    private lateinit var birthDateTextView: TextView
    private lateinit var conditionsTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var updateConditionsEditText: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_senior_detail)

        // View 연결
        nameTextView = findViewById(R.id.seniorNameTextView)
        roomIDTextView = findViewById(R.id.seniorRoomIDTextView)
        admissionDateTextView = findViewById(R.id.seniorAdmissionDateTextView)
        birthDateTextView = findViewById(R.id.seniorBirthDateTextView)
        conditionsTextView = findViewById(R.id.seniorConditionsTextView)
        genderTextView = findViewById(R.id.seniorGenderTextView)
        updateConditionsEditText = findViewById(R.id.updateConditionsEditText)
        updateButton = findViewById(R.id.updateButton)

        // Intent로 전달된 seniorID 받기
        val seniorID = intent.getStringExtra("seniorID") ?: return

        loadSeniorDetails(seniorID)

        // 상태 업데이트 버튼 클릭 시
        updateButton.setOnClickListener {
            val newCondition = updateConditionsEditText.text.toString().trim()
            if (newCondition.isNotEmpty()) {
                updateSeniorConditions(seniorID, newCondition)
            } else {
                Toast.makeText(this, "Condition cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase에서 시니어의 상세 정보를 로드
    private fun loadSeniorDetails(seniorID: String) {
        val seniorRef = FirebaseDatabase.getInstance().getReference("seniors").child(seniorID)
        seniorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val roomID = snapshot.child("roomID").getValue(String::class.java) ?: "Unknown"
                    val admissionDate = snapshot.child("admissionDate").getValue(String::class.java) ?: "Unknown"
                    val birthDate = snapshot.child("birthDate").getValue(String::class.java) ?: "Unknown"
                    val conditions = snapshot.child("existingConditions").getValue(String::class.java) ?: "Unknown"
                    val gender = snapshot.child("gender").getValue(String::class.java) ?: "Unknown"

                    // 상세 정보 표시
                    nameTextView.text = "이름: $name"
                    roomIDTextView.text = "호실: $roomID"
                    admissionDateTextView.text = "등록날짜: $admissionDate"
                    birthDateTextView.text = "생년월일: $birthDate"
                    conditionsTextView.text = "특이사항: $conditions"
                    genderTextView.text = "성별: $gender"
                } else {
                    Toast.makeText(this@SeniorDetailActivity, "Senior not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SeniorDetailActivity, "Failed to load senior details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 상태 업데이트
    private fun updateSeniorConditions(seniorID: String, newCondition: String) {
        val seniorRef = FirebaseDatabase.getInstance().getReference("seniors").child(seniorID)
        seniorRef.child("existingConditions").setValue(newCondition).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Condition updated successfully", Toast.LENGTH_SHORT).show()
                conditionsTextView.text = "Conditions: $newCondition"
            } else {
                Toast.makeText(this, "Failed to update condition", Toast.LENGTH_SHORT).show()
            }
        }
    }
}