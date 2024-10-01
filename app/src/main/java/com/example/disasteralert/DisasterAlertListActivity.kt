package com.example.disasteralert

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

data class DisasterAlert(
    val details: String = "",
    val level: String = "",
    val location: Map<String, Double> = mapOf("latitude" to 0.0, "longitude" to 0.0),
    val timestamp: com.google.firebase.Timestamp? = null,
    val serviceDeployed: String = ""
)
class DisasterAlertListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var alertAdapter: DisasterAlertAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_alert_list)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.alert_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        alertAdapter = DisasterAlertAdapter(arrayListOf())
        recyclerView.adapter = alertAdapter

        // Fetch data from Firestore
        retrieveDisasterAlerts()
    }

    private fun retrieveDisasterAlerts() {
        firestore.collection("disaster_alerts")
            .get()
            .addOnSuccessListener { result ->
                val alertList = mutableListOf<DisasterAlert>()
                for (document in result) {
                    val alert = document.toObject(DisasterAlert::class.java)
                    alertList.add(alert)
                }
                // Update the adapter with the retrieved alerts
                alertAdapter.updateData(alertList)
            }
            .addOnFailureListener { exception ->
                Log.w("DisasterAlertActivity", "Error getting documents: ", exception)
            }
    }
}
