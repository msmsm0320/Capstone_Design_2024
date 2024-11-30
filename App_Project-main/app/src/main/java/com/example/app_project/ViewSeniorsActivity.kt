package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewSeniorsActivity : AppCompatActivity() {

    private lateinit var seniorsListView: ListView
    private lateinit var seniorsListAdapter: ArrayAdapter<String>
    private val seniorsList = ArrayList<String>()
    private val seniorIDsList = ArrayList<String>() // Senior ID 목록 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_seniors)

        seniorsListView = findViewById(R.id.seniorsListView)
        seniorsListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, seniorsList)
        seniorsListView.adapter = seniorsListAdapter

        loadSeniors()

        // 리스트에서 클릭하면 해당 시니어의 ID를 SeniorDetailActivity로 전달
        seniorsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSeniorID = seniorIDsList[position]  // 클릭된 시니어의 ID 가져오기
            val intent = Intent(this, SeniorDetailActivity::class.java)
            intent.putExtra("seniorID", selectedSeniorID)
            startActivity(intent)  // SeniorDetailActivity로 이동
        }
    }

    // Firebase에서 시니어 목록 로드
    private fun loadSeniors() {
        val seniorsRef = FirebaseDatabase.getInstance().getReference("seniors")
        seniorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seniorsList.clear()
                seniorIDsList.clear()

                for (seniorSnapshot in snapshot.children) {
                    val name = seniorSnapshot.child("name").getValue(String::class.java) ?: ""
                    val roomID = seniorSnapshot.child("roomID").getValue(String::class.java) ?: ""
                    val seniorID = seniorSnapshot.key ?: "" // Firebase에서 시니어 ID 가져오기

                    seniorsList.add("호실: $roomID, 이름: $name, 시니어 ID: $seniorID")
                    seniorIDsList.add(seniorID) // ID 저장
                }

                // Room ID를 숫자로 변환 후 정렬
                seniorsList.sortWith(compareBy { entry ->
                    val roomID = entry.substringAfter("호실: ").substringBefore(", 이름").toIntOrNull()
                    roomID ?: 0  // 방번호가 숫자가 아닐 경우 기본값 0으로 설정
                })

                seniorsListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewSeniorsActivity, "Failed to load seniors.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}