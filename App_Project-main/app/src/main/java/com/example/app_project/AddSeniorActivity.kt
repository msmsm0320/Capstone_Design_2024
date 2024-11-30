package com.example.app_project

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Senior(
    val seniorID: String = "",
    val careFacilityID: String = "",
    val roomID: String = "",
    val caregiverID: String = "",
    val name: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val admissionDate: String = "",
    val existingConditions: String = ""
)

class AddSeniorActivity : AppCompatActivity() {

    private lateinit var seniorIDEditText: EditText
    private lateinit var careFacilityIDEditText: EditText
    private lateinit var roomIDEditText: EditText
    private lateinit var caregiverIDEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var admissionDateEditText: EditText
    private lateinit var existingConditionsEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var seniorsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_senior)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        seniorsRef = database.getReference("seniors")

        // Initialize UI elements
        seniorIDEditText = findViewById(R.id.seniorIDEditText)
        careFacilityIDEditText = findViewById(R.id.careFacilityIDEditText)
        roomIDEditText = findViewById(R.id.roomIDEditText)
        caregiverIDEditText = findViewById(R.id.caregiverIDEditText)
        nameEditText = findViewById(R.id.nameEditText)
        birthDateEditText = findViewById(R.id.birthDateEditText)
        genderEditText = findViewById(R.id.genderEditText)
        admissionDateEditText = findViewById(R.id.admissionDateEditText)
        existingConditionsEditText = findViewById(R.id.existingConditionsEditText)
        saveButton = findViewById(R.id.saveButton)

        // Set button click listener
        saveButton.setOnClickListener {
            saveSeniorInfo()
        }
    }

    private fun saveSeniorInfo() {
        val seniorID = seniorIDEditText.text.toString().trim()
        val careFacilityID = careFacilityIDEditText.text.toString().trim()
        val roomID = roomIDEditText.text.toString().trim()
        val caregiverID = caregiverIDEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val birthDate = birthDateEditText.text.toString().trim()
        val gender = genderEditText.text.toString().trim()
        val admissionDate = admissionDateEditText.text.toString().trim()
        val existingConditions = existingConditionsEditText.text.toString().trim()

        if (TextUtils.isEmpty(seniorID) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Senior ID and Name are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val senior = Senior(
            seniorID,
            careFacilityID,
            roomID,
            caregiverID,
            name,
            birthDate,
            gender,
            admissionDate,
            existingConditions
        )

        seniorsRef.child(seniorID).setValue(senior).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Senior information saved successfully.", Toast.LENGTH_SHORT).show()
                finish() // Close the activity or navigate back
            } else {
                task.exception?.let {
                    Toast.makeText(this, "Failed to save senior information: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
