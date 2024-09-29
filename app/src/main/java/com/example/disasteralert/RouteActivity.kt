package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class RouteActivity : AppCompatActivity() {

    private val graphHopperApiKey = "f52302b1-1371-4272-a89e-6c895e20ceaa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        val startPointEditText = findViewById<EditText>(R.id.startPointEditText)
        val destinationPointEditText = findViewById<EditText>(R.id.destinationPointEditText)
        val transportModeSpinner = findViewById<Spinner>(R.id.transportModeSpinner)
        val calculateRouteButton = findViewById<Button>(R.id.calculateRouteButton)
        val routeTextView = findViewById<TextView>(R.id.routeTextView)

        // Receive latitude and longitude from the Intent
        val myLatitude = intent.getStringExtra("MyLATITUDE")
        val myLongitude = intent.getStringExtra("MyLONGITUDE")
        val destLat = intent.getStringExtra("LATITUDE")
        val destLng = intent.getStringExtra("LONGITUDE")

        Toast.makeText(this, "My Location: ($myLatitude, $myLongitude)\nDestination: ($destLat, $destLng)", Toast.LENGTH_LONG).show()

        // Set the received coordinates in the EditText fields
        if (myLatitude != null && myLongitude != null) {
            startPointEditText.setText("$myLatitude, $myLongitude")
        }
        if (destLat != null && destLng != null) {
            destinationPointEditText.setText("$destLat, $destLng")
        }

        // Handle route calculation button click
        calculateRouteButton.setOnClickListener {
            val startPoint = startPointEditText.text.toString()
            val destinationPoint = destinationPointEditText.text.toString()
            val selectedTransportMode = transportModeSpinner.selectedItem.toString()

            if (isValidInput(startPoint, destinationPoint)) {
                calculateRoute(startPoint, destinationPoint, selectedTransportMode, routeTextView)
            } else {
                Toast.makeText(this, "Please enter valid start and destination coordinates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Validate if the input is correct
    private fun isValidInput(start: String, destination: String): Boolean {
        return start.contains(",") && destination.contains(",")
    }

    // Function to calculate route using GraphHopper API
    private fun calculateRoute(start: String, destination: String, mode: String, resultTextView: TextView) {
        val client = OkHttpClient()

        // Split the input into latitude and longitude for start and destination
        val startParts = start.split(",")
        val startLat = startParts[0].trim() // Trim to avoid leading/trailing spaces
        val startLng = startParts[1].trim()

        val destinationParts = destination.split(",")
        val destinationLat = destinationParts[0].trim()
        val destinationLng = destinationParts[1].trim()

        // Build the GraphHopper Directions API URL
        val url = "https://graphhopper.com/api/1/route?point=$startLat,$startLng&point=$destinationLat,$destinationLng&profile=$mode&key=$graphHopperApiKey"

        // Perform the network request in a background thread
        Thread {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parse the JSON response
                        val json = JSONObject(it)
                        val paths = json.getJSONArray("paths")
                        if (paths.length() > 0) {
                            val path = paths.getJSONObject(0)
                            val distance = path.getDouble("distance") // Distance in meters
                            val time = path.getLong("time") / 1000 // Time in seconds

                            // Update the UI with the route details
                            runOnUiThread {
                                resultTextView.text = """
                                Mode: $mode
                                Distance: ${distance / 1000} km
                                Estimated Time: ${time / 60} min
                            """.trimIndent()

                                // Start MapActivity with coordinates
                                val intent = Intent(this@RouteActivity, MapActivity::class.java)
                                intent.putExtra("START_LAT", startLat)
                                intent.putExtra("START_LNG", startLng)
                                intent.putExtra("DEST_LAT", destinationLat)
                                intent.putExtra("DEST_LNG", destinationLng)
                                startActivity(intent)
                            }
                        } else {
                            runOnUiThread {
                                resultTextView.text = "No route found."
                            }
                        }
                    }
                } else {
                    Log.e("RouteActivity", "Route request failed")
                }
            } catch (e: IOException) {
                Log.e("RouteActivity", "Network error", e)
            }
        }.start()
    }
}
