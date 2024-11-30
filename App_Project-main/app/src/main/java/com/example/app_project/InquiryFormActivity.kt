package com.example.app_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class InquiryFormActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_form) // 문의 작성 레이아웃

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        submitButton = findViewById(R.id.submitButton)

        // 제출 버튼 클릭 이벤트 처리
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                submitInquiry(title, content) // 문의사항 제출
            } else {
                Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitInquiry(title: String, content: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        // Firebase Database에서 name과 seniorID를 가져오기 (예시 경로: "users/$uid/")
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid!!)
        userRef.get().addOnSuccessListener { dataSnapshot ->
            val name = dataSnapshot.child("name").value.toString()
            val seniorID = dataSnapshot.child("seniorID").value.toString()
            val role = "caregiver" // 역할을 가져오는 방법에 따라 적절히 수정

            if (uid != null) {
                // Firebase Realtime Database에 문의 데이터 저장
                val inquiryRef = FirebaseDatabase.getInstance().getReference("inquiries").push()
                val inquiryData = mapOf(
                    "title" to title,
                    "content" to content,
                    "caregiverId" to uid, // caregiver의 uid 저장
                    "name" to name, // name 저장
                    "seniorID" to seniorID, // seniorID 저장
                    "role" to role // role 정보도 저장 (필요한 경우)
                )

                inquiryRef.setValue(inquiryData).addOnSuccessListener {
                    Toast.makeText(this, "문의가 제출되었습니다.", Toast.LENGTH_SHORT).show()
                    finish() // 문의 제출 후 현재 Activity 종료
                }.addOnFailureListener {
                    Toast.makeText(this, "문의 제출 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "사용자 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
