package com.example.app_project

import android.widget.SimpleAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Inquiry 데이터 클래스
data class Inquiry(
    val title: String = "",
    val content: String = "",  // 본문 내용을 content로 변경
    val time: String = "",
    val caregiverId: String = "",
    val name: String = "",  // 작성자 이름 필드 추가
    val seniorID: String = ""  // seniorID 필드 추가
)

class InquiryActivity : AppCompatActivity() {

    private lateinit var inquiryButton: Button // 문의 작성 버튼
    private lateinit var inquiryListView: ListView // 문의사항 리스트 뷰
    private lateinit var database: DatabaseReference // Firebase 데이터베이스 참조
    private val inquiries = mutableListOf<Map<String, String>>() // 문의사항 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry) // 문의사항 레이아웃

        inquiryButton = findViewById(R.id.inquiryButton) // 문의 작성 버튼
        inquiryButton.visibility = View.GONE // 기본적으로 버튼 숨기기

        inquiryListView = findViewById(R.id.inquiryListView) // 문의사항 리스트 뷰

        // Firebase 데이터베이스 참조 초기화
        database = FirebaseDatabase.getInstance().getReference("users")

        // 현재 로그인한 사용자 ID 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // userId가 null이 아니면 데이터베이스에서 사용자 정보 조회
        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.child("role").getValue(String::class.java)
                    if (role == "Caregiver") {
                        // role이 Caregiver일 때만 버튼 보이기
                        inquiryButton.visibility = View.VISIBLE
                    }
                    // 문의사항 불러오기
                    loadInquiries(role)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                }
            })
        }

        // 문의 작성 버튼 클릭 이벤트 처리
        inquiryButton.setOnClickListener {
            val intent = Intent(this, InquiryFormActivity::class.java)
            startActivity(intent) // 문의 작성 페이지로 이동
        }

        // 리스트 아이템 클릭 이벤트
        inquiryListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedInquiry = inquiries[position]
                val intent = Intent(this, InquiryDetailActivity::class.java)
                intent.putExtra("inquiryId", selectedInquiry["inquiryId"]) // 문의 ID 전달
                intent.putExtra("title", selectedInquiry["title"]) // 제목 전달
                intent.putExtra("body", selectedInquiry["body"])   // 본문 내용 전달
                intent.putExtra("name", selectedInquiry["name"])   // 작성자 이름 전달
                intent.putExtra("seniorID", selectedInquiry["seniorID"]) // 작성자의 seniorID 전달
                startActivity(intent)
            }
    }

    private fun loadInquiries(role: String?) {
        val inquiriesRef = FirebaseDatabase.getInstance().getReference("inquiries")

        inquiriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                inquiries.clear()
                for (inquirySnapshot in snapshot.children) {
                    val inquiry = inquirySnapshot.getValue(Inquiry::class.java)
                    if (inquiry != null && (role == "Admin" || inquiry.caregiverId == FirebaseAuth.getInstance().currentUser?.uid)) {
                        val inquiryMap = mapOf(
                            "inquiryId" to inquirySnapshot.key!!, // 문의 ID 추가
                            "title" to inquiry.title,
                            "time" to inquiry.time,
                            "body" to inquiry.content,  // 본문 내용을 content로 설정
                            "name" to inquiry.name, // 작성자 이름 추가
                            "seniorID" to inquiry.seniorID // seniorID 추가
                        ).mapValues { it.value ?: "" } // Nullable 값을 처리하여 빈 문자열로 대체

                        inquiries.add(inquiryMap)
                    }
                }

                // 문의사항을 리스트뷰에 표시하는 어댑터
                val adapter = SimpleAdapter(
                    this@InquiryActivity,
                    inquiries,
                    android.R.layout.simple_list_item_2,
                    arrayOf("title", "time"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                inquiryListView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }
}
