package com.example.app_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class Comment(
    val content: String = "",
    val userId: String = "",
    val role: String = "" // 역할 추가
)

class InquiryDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val comments = mutableListOf<Map<String, String>>()
    private lateinit var commentAdapter: SimpleAdapter
    private lateinit var commentsListView: ListView
    private lateinit var inquiryId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry_detail)

        // TextView 찾기
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val bodyTextView = findViewById<TextView>(R.id.bodyTextView)
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val seniorIdTextView = findViewById<TextView>(R.id.seniorIdTextView)

        // 댓글 입력 필드와 버튼
        val commentEditText = findViewById<EditText>(R.id.commentEditText)
        val commentButton = findViewById<Button>(R.id.commentButton)
        commentsListView = findViewById(R.id.commentsListView)

        // Intent로 전달된 데이터 가져오기
        inquiryId = intent.getStringExtra("inquiryId") ?: ""
        val title = intent.getStringExtra("title") ?: "제목 없음"
        val body = intent.getStringExtra("body") ?: "내용 없음"
        val name = intent.getStringExtra("name") ?: "이름 없음"
        val seniorId = intent.getStringExtra("seniorID") ?: "ID 없음"

        // 가져온 데이터를 UI에 표시
        titleTextView.text = title
        bodyTextView.text = body
        nameTextView.text = "작성자: $name"
        seniorIdTextView.text = "Senior ID: $seniorId"

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().getReference("comments")

        // 댓글 표시
        loadComments()

        // 댓글 작성 버튼 클릭 이벤트
        commentButton.setOnClickListener {
            val commentContent = commentEditText.text.toString()
            if (commentContent.isNotEmpty()) {
                addComment(commentContent)
                commentEditText.text.clear() // 입력 필드 초기화
            }
        }
    }

    // 댓글 데이터를 Firebase에서 불러오기
    private fun loadComments() {
        // inquiryId를 사용하여 특정 문의의 댓글을 가져옴
        database.child(inquiryId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                comments.clear()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        val commentMap = mapOf(
                            "content" to comment.content,
                            "role" to comment.role
                        )
                        comments.add(commentMap)
                    }
                }

                commentAdapter = SimpleAdapter(
                    this@InquiryDetailActivity,
                    comments,
                    android.R.layout.simple_list_item_2,
                    arrayOf("content", "role"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                commentsListView.adapter = commentAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }

    // 댓글을 Firebase에 저장
    private fun addComment(content: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 사용자 역할 가져오기
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.child("role").getValue(String::class.java) ?: "User"
                    val comment = Comment(content, userId, role)

                    // Firebase에 댓글 저장
                    // inquiryId를 사용하여 댓글을 해당 문의에 추가
                    database.child(inquiryId).push().setValue(comment)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                }
            })
    }
}
