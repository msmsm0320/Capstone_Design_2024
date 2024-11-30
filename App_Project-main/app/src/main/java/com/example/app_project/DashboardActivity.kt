package com.example.app_project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var patientStatusTextView: TextView
    private lateinit var contactSeniorButton: Button
    private lateinit var addSeniorButton: Button
    private lateinit var connectSeniorButton: Button
    private lateinit var logoutButton: Button
    private lateinit var viewSeniorsButton: Button
    private lateinit var liveStreamButton: Button
    private lateinit var bulletinBoardButton: Button  // Bulletin Board 버튼 추가


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        patientStatusTextView = findViewById(R.id.patientStatusTextView)
        contactSeniorButton = findViewById(R.id.contactSeniorButton)
        addSeniorButton = findViewById(R.id.addSeniorButton)
        logoutButton = findViewById(R.id.logoutButton)
        viewSeniorsButton = findViewById(R.id.viewSeniorsButton)
        liveStreamButton = findViewById(R.id.liveStreamButton)
        bulletinBoardButton = findViewById(R.id.bulletinBoardButton) // Bulletin Board 버튼 초기화
// Bulletin Board 버튼 초기화

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userType = snapshot.child("role").getValue(String::class.java) ?: "Unknown"
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val seniorID = snapshot.child("seniorID").getValue(String::class.java)

                if (userType == "Admin") {
                    addSeniorButton.visibility = View.VISIBLE
                    viewSeniorsButton.visibility = View.VISIBLE
                    liveStreamButton.visibility = View.VISIBLE
                    bulletinBoardButton.visibility = View.VISIBLE
                    bulletinBoardButton.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, BulletinBoardActivity::class.java)
                        startActivity(intent)
                    }

                    addSeniorButton.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, AddSeniorActivity::class.java)
                        startActivity(intent)
                    }
                    viewSeniorsButton.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, ViewSeniorsActivity::class.java)
                        startActivity(intent)
                    }
                } else if (userType == "Caregiver") {
                    contactSeniorButton.visibility = View.GONE
                    bulletinBoardButton.visibility = View.VISIBLE
                    bulletinBoardButton.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, BulletinBoardActivity::class.java)
                        startActivity(intent)
                    }

                    loadSeniorInfo(seniorID)

                    liveStreamButton.setOnClickListener {
                        val intent = Intent(this@DashboardActivity, LiveStreamActivity::class.java)
                        startActivity(intent)
                    }
                }

                // 모든 사용자에게 Bulletin Board 버튼 표시 및 클릭 이벤트 추가
                bulletinBoardButton.visibility = View.VISIBLE
                bulletinBoardButton.setOnClickListener {
                    val intent = Intent(this@DashboardActivity, BulletinBoardActivity::class.java)
                    startActivity(intent)
                }

                patientStatusTextView.text = "반갑습니다! $name 님!"
            }

            override fun onCancelled(error: DatabaseError) {
                patientStatusTextView.text = "Failed to load user data."
            }
        })
    }

    private fun loadSeniorInfo(seniorID: String?) {
        if (seniorID == null) {
            Toast.makeText(this, "Senior ID not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val seniorRef = FirebaseDatabase.getInstance().getReference("seniors").child(seniorID)
        seniorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val admissionDate = snapshot.child("admissionDate").getValue(String::class.java)
                    val birthDate = snapshot.child("birthDate").getValue(String::class.java)
                    val existingConditions = snapshot.child("existingConditions").getValue(String::class.java)
                    val gender = snapshot.child("gender").getValue(String::class.java)
                    val roomID = snapshot.child("roomID").getValue(String::class.java)

                    findViewById<TextView>(R.id.seniorNameTextView).text = "Name: $name"
                    findViewById<TextView>(R.id.seniorAdmissionDateTextView).text = "Admission Date: $admissionDate"
                    findViewById<TextView>(R.id.seniorBirthDateTextView).text = "Birth Date: $birthDate"
                    findViewById<TextView>(R.id.seniorConditionsTextView).text = "Conditions: $existingConditions"
                    findViewById<TextView>(R.id.seniorGenderTextView).text = "Gender: $gender"
                    findViewById<TextView>(R.id.seniorRoomIDTextView).text = "Room ID: $roomID"

                    findViewById<LinearLayout>(R.id.seniorInfoLayout).visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@DashboardActivity, "Senior not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Failed to load senior info.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 다른 Navigation Drawer 메뉴 항목 처리
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}