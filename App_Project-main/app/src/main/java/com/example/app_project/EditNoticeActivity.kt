package com.example.app_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditNoticeActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_notice)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)

        val noticeID = intent.getStringExtra("noticeID")
        if (noticeID != null) {
            loadNoticeDetail(noticeID)
        }

        saveButton.setOnClickListener {
            if (noticeID != null) {
                saveNoticeDetail(noticeID)
            }
        }
    }

    private fun loadNoticeDetail(noticeID: String) {
        val noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeID)
        noticeRef.get().addOnSuccessListener { snapshot ->
            val title = snapshot.child("title").getValue(String::class.java)
            val content = snapshot.child("content").getValue(String::class.java)

            titleEditText.setText(title)
            contentEditText.setText(content)
        }.addOnFailureListener {
            Toast.makeText(this, "공지사항을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNoticeDetail(noticeID: String) {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val noticeRef = FirebaseDatabase.getInstance().getReference("notices").child(noticeID)
        val noticeUpdates = mapOf<String, Any>(
            "title" to title,
            "content" to content
        )

        noticeRef.updateChildren(noticeUpdates).addOnSuccessListener {
            Toast.makeText(this, "공지사항이 수정되었습니다.", Toast.LENGTH_SHORT).show()
            finish() // 수정 후 화면 종료
        }.addOnFailureListener {
            Toast.makeText(this, "수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
