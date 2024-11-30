package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NoticeDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)

        titleTextView = findViewById(R.id.titleTextView)
        contentTextView = findViewById(R.id.contentTextView)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        val noticeID = intent.getStringExtra("noticeID")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (noticeID != null && currentUserId != null) {
            checkUserRole(currentUserId) // 사용자 역할 확인
            loadNoticeDetail(noticeID)
        }

        // 수정 버튼 클릭 리스너
        editButton.setOnClickListener {
            val intent = Intent(this, EditNoticeActivity::class.java)
            intent.putExtra("noticeID", noticeID)
            startActivity(intent)
        }

        // 삭제 버튼 클릭 리스너
        deleteButton.setOnClickListener {
            if (noticeID != null) {
                deleteNotice(noticeID)
            }
        }
    }

    // 사용자 역할을 확인하는 함수
    private fun checkUserRole(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.child("role").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userRole = snapshot.getValue(String::class.java)
                if (userRole == "Admin") {
                    editButton.visibility = Button.VISIBLE
                    deleteButton.visibility = Button.VISIBLE
                } else {
                    editButton.visibility = Button.GONE
                    deleteButton.visibility = Button.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NoticeDetailActivity, "역할 확인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 공지사항 로딩
    private fun loadNoticeDetail(noticeID: String) {
        val noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeID)
        noticeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val title = snapshot.child("title").getValue(String::class.java)
                val content = snapshot.child("content").getValue(String::class.java)

                titleTextView.text = title
                contentTextView.text = content
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }

    // 공지사항 삭제 함수
    private fun deleteNotice(noticeID: String) {
        val noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeID)
        noticeRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "공지사항이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            finish() // 삭제 후 화면 종료
        }.addOnFailureListener {
            Toast.makeText(this, "삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
