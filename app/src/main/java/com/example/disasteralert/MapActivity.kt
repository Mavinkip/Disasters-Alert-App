package com.example.disasteralert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting up the osmdroid configuration
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_map)

        // Initialize the MapView
        map = findViewById(R.id.map)
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Retrieve coordinates from the intent
        val startLat = intent.getStringExtra("START_LAT")?.toDoubleOrNull()
        val startLng = intent.getStringExtra("START_LNG")?.toDoubleOrNull()
        val destLat = intent.getStringExtra("DEST_LAT")?.toDoubleOrNull()
        val destLng = intent.getStringExtra("DEST_LNG")?.toDoubleOrNull()

        // Set default map position and zoom level
        if (startLat != null && startLng != null) {
            map.controller.setCenter(org.osmdroid.util.GeoPoint(startLat, startLng))
        }
        map.controller.setZoom(8.0) // Adjust zoom level as needed

        // Add markers for the start and destination coordinates
        startLat?.let { startLat ->
            startLng?.let { startLng ->
                addMarker(startLat, startLng, "Start Point")
            }
        }
        destLat?.let { destLat ->
            destLng?.let { destLng ->
                addMarker(destLat, destLng, "Destination Point")
            }
        }
    }

    // Function to add a marker to the map
    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val marker = Marker(map)
        marker.position = org.osmdroid.util.GeoPoint(latitude, longitude)
        marker.title = title
        marker.icon = resources.getDrawable(R.drawable.baseline_location_pin_24) // Replace with your marker icon drawable
        marker.setOnMarkerClickListener { _, _ ->
            // Handle marker click if needed
            return@setOnMarkerClickListener true
        }
        map.overlays.add(marker)
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // Call the MapView's onResume method
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // Call the MapView's onPause method
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDetach() // Call the MapView's onDetach method
    }
}
