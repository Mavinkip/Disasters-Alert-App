package com.example.disasteralert

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DeploymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deployment)

        // Find views
        val emergencyServiceSpinner = findViewById<Spinner>(R.id.emergencyServiceSpinner)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        // Set the button click listener
        confirmButton.setOnClickListener {
            // Get the selected service
            val selectedService = emergencyServiceSpinner.selectedItem.toString()

            // Display a toast message
            Toast.makeText(this, "Selected service: $selectedService", Toast.LENGTH_LONG).show()

            // End the activity
            finish()
        }
    }
}
