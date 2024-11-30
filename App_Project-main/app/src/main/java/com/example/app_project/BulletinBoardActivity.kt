package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BulletinBoardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var addNoticeButton: Button  // 공지사항 작성 버튼
    private lateinit var addInquiryButton: Button  // 문의사항 작성 버튼
    private lateinit var noticeListLayout: LinearLayout  // 공지사항 목록 레이아웃

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulletin_board)

        auth = FirebaseAuth.getInstance()
        addNoticeButton = findViewById(R.id.addNoticeButton)
        addInquiryButton = findViewById(R.id.addInquiryButton)  // 문의사항 버튼
        noticeListLayout = findViewById(R.id.noticeListLayout)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("role").getValue(String::class.java) ?: "Unknown"

                    // 관리자일 경우에만 공지사항 작성 버튼 보이기
                    if (userType == "Admin") {
                        addNoticeButton.visibility = View.VISIBLE
                    }

                    addNoticeButton.setOnClickListener {
                        val intent = Intent(this@BulletinBoardActivity, AddNoticeActivity::class.java)
                        startActivity(intent)
                    }

                    // 문의사항 버튼 클릭 이벤트 처리
                    addInquiryButton.setOnClickListener {
                        val intent = Intent(this@BulletinBoardActivity, InquiryActivity::class.java)
                        startActivity(intent)  // 문의사항 작성 페이지로 이동
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                }
            })
        }

        // Firebase에서 공지사항 리스트를 불러와 화면에 표시
        loadNotices()
    }

    private fun loadNotices() {
        val noticeRef = FirebaseDatabase.getInstance().getReference("notices")
        noticeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noticeSnapshot in snapshot.children) {
                    val title = noticeSnapshot.child("title").getValue(String::class.java)
                    val date = noticeSnapshot.child("date").getValue(String::class.java)
                    val noticeID = noticeSnapshot.key

                    // 공지사항 제목과 날짜를 표시하는 TextView 생성
                    val noticeItem = TextView(this@BulletinBoardActivity)
                    noticeItem.text = "$title - $date"
                    noticeItem.textSize = 18f
                    noticeItem.setPadding(16, 16, 16, 16)

                    // 클릭 이벤트를 추가하여 상세 화면으로 이동
                    noticeItem.setOnClickListener {
                        val intent = Intent(this@BulletinBoardActivity, NoticeDetailActivity::class.java)
                        intent.putExtra("noticeID", noticeID)
                        startActivity(intent)
                    }

                    // 공지사항을 레이아웃에 추가
                    noticeListLayout.addView(noticeItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }
}
