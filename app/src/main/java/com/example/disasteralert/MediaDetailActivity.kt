package com.example.disasteralert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MediaDetailActivity : AppCompatActivity() {

    private lateinit var mediaImageView: ImageView
    private lateinit var mediaVideoView: VideoView
    private lateinit var descriptionTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var weatherButton: ImageView
    private lateinit var mapButton: ImageView
    private lateinit var deployButton: ImageView
    private lateinit var disasterDetailsEditText: EditText
    private lateinit var alertButton: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var logoutButton: TextView
    private lateinit var auth: FirebaseAuth // Declare auth here



    private var myLatitude: Double = 0.0
    private var myLongitude: Double = 0.0

    private lateinit var radioLevel1: RadioButton
    private lateinit var radioLevel2: RadioButton
    private lateinit var radioLevel3: RadioButton
    private lateinit var radioLevel4: RadioButton

    private lateinit var mylocationText: TextView
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    private val REQUEST_LOCATION_PERMISSION = 101
    private val firestore = FirebaseFirestore.getInstance() // Initialize Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)

        // Initialize views
        initializeViews()

        // Check for permissions and get location
        checkPermissions()
        getLocation()
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth here
        logoutButton = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }


        // Retrieve data from intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val uniqueId = intent.getStringExtra("uniqueId")

        // Show latitude, longitude, and uniqueId in Toast
        showLocationInfo(latitude, longitude, uniqueId)

        // Set up listeners
        setupListeners(latitude, longitude)

        // Load media based on its type
        loadMedia()
    }

    private fun initializeViews() {
        mylocationText = findViewById(R.id.mylocation_text)
        mediaImageView = findViewById(R.id.media_image)
        mediaVideoView = findViewById(R.id.media_video)
        descriptionTextView = findViewById(R.id.media_description)
        loadingProgressBar = findViewById(R.id.media_loading)
        weatherButton = findViewById(R.id.weatherid)
        mapButton = findViewById(R.id.mapid)
        deployButton = findViewById(R.id.deployid)
        disasterDetailsEditText = findViewById(R.id.disaster_details_input)
        alertButton = findViewById(R.id.alert_button)
        radioGroup = findViewById(R.id.radio_group)
        radioLevel1 = findViewById(R.id.radio_level_1)
        radioLevel2 = findViewById(R.id.radio_level_2)
        radioLevel3 = findViewById(R.id.radio_level_3)
        radioLevel4 = findViewById(R.id.radio_level_4)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    // Function to save deployment data to Firestore



    private fun showLocationInfo(latitude: Double, longitude: Double, uniqueId: String?) {
        Toast.makeText(this, "Latitude: $latitude", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Longitude: $longitude", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "UniqueId: $uniqueId", Toast.LENGTH_SHORT).show()
    }
    private fun logout() {
        auth.signOut() // Sign out from Firebase Auth
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Optionally, navigate back to login activity or update UI accordingly
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupListeners(latitude: Double, longitude: Double) {
        // Set up click listeners for various buttons
        weatherButton.setOnClickListener {
            val intent = Intent(this, GeocodeActivity::class.java).apply {
                putExtra("LATITUDE", latitude)
                putExtra("LONGITUDE", longitude)
            }
            startActivity(intent)
        }

        mapButton.setOnClickListener {
          //  Toast.makeText(this, "Passing Coordinates:\nMy Location: ($myLatitude, $myLongitude)\nDestination: ($latitude, $longitude)", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RouteActivity::class.java).apply {
                putExtra("LATITUDE", latitude)
                putExtra("LONGITUDE", longitude)
                putExtra("MyLATITUDE", myLatitude)
                putExtra("MyLONGITUDE", myLongitude)
            }
            startActivity(intent)
        }

        deployButton.setOnClickListener {
            startActivity(Intent(this, DisasterAlertListActivity::class.java))
        }
        val emergencyServiceSpinner = findViewById<Spinner>(R.id.emergencyServiceSpinner)

        alertButton.setOnClickListener {
            // Get the selected service from the Spinner
            val selectedService = emergencyServiceSpinner.selectedItem.toString()

            // Pass the selected service to the saveDisasterAlert function
            saveDisasterAlert(selectedService)
        }


        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            clearRadioButtons()
            when (checkedId) {
                R.id.radio_level_1 -> {
                    showTickIcon(radioLevel1)
                }
                R.id.radio_level_2 -> {
                    showTickIcon(radioLevel2)
                }
                R.id.radio_level_3 -> {
                    showTickIcon(radioLevel3)
                }
                R.id.radio_level_4 -> {
                    showTickIcon(radioLevel4)
                }
            }
        }
    }

    private fun clearRadioButtons() {
        radioLevel1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        radioLevel2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        radioLevel3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        radioLevel4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    private fun showTickIcon(radioButton: RadioButton) {
        radioButton.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(this, R.drawable.tick_icon), null
        )
    }

    private fun loadMedia() {
        val description = intent.getStringExtra("description") ?: "No description available"
        val mediaUrl = intent.getStringExtra("mediaUrl")
        val mediaType = intent.getStringExtra("mediaType")

        descriptionTextView.text = description

        when (mediaType) {
            "image" -> loadImage(mediaUrl)
            "video" -> loadVideo(mediaUrl)
            else -> descriptionTextView.text = "Unsupported media type."
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myLatitude = location.latitude
                    myLongitude = location.longitude
                    mylocationText.text = "Location: Lat: $myLatitude, Long: $myLongitude"
                    //Toast.makeText(this, "Latitude: $myLatitude, Longitude: $myLongitude", Toast.LENGTH_SHORT).show()

                } else {
                    mylocationText.text = "Location: Not available"
                    //Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun loadImage(mediaUrl: String?) {
        mediaImageView.visibility = ImageView.VISIBLE
        mediaVideoView.visibility = VideoView.GONE
        loadingProgressBar.visibility = ProgressBar.GONE

        if (!mediaUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(mediaUrl)
                .into(mediaImageView)
        } else {
            descriptionTextView.text = "Image URL is not valid."
        }
    }

    private fun loadVideo(mediaUrl: String?) {
        mediaImageView.visibility = ImageView.GONE
        mediaVideoView.visibility = VideoView.VISIBLE
        loadingProgressBar.visibility = ProgressBar.VISIBLE

        if (!mediaUrl.isNullOrEmpty()) {
            val mediaController = android.widget.MediaController(this)
            mediaVideoView.setMediaController(mediaController)
            mediaController.setAnchorView(mediaVideoView)

            mediaVideoView.setVideoURI(Uri.parse(mediaUrl))

            mediaVideoView.setOnPreparedListener { mediaPlayer ->
                loadingProgressBar.visibility = ProgressBar.GONE
                mediaPlayer.start()
            }

            mediaVideoView.setOnErrorListener { _, what, extra ->
                loadingProgressBar.visibility = ProgressBar.GONE
                mediaVideoView.visibility = VideoView.GONE
                descriptionTextView.text = "Error loading video."
                true
            }
        } else {
            descriptionTextView.text = "Video URL is not valid."
        }
    }

    private fun saveDisasterAlert(selectedService: String) {
        val disasterDetails = disasterDetailsEditText.text.toString()
        val disasterLevel = when (radioGroup.checkedRadioButtonId) {
            R.id.radio_level_1 -> "Level 1"
            R.id.radio_level_2 -> "Level 2"
            R.id.radio_level_3 -> "Level 3"
            R.id.radio_level_4 -> "Level 4"
            else -> "Unknown Level"
        }

        // Create alertData with deployment as a nested field
        val alertData = hashMapOf(
            "details" to disasterDetails,
            "level" to disasterLevel,
            "location" to mapOf("latitude" to myLatitude, "longitude" to myLongitude),
            "timestamp" to FieldValue.serverTimestamp(),
            "deployment" to mapOf(  // Nested deployment data
                "serviceDeployed" to selectedService,
                "timestamp" to System.currentTimeMillis() // Example of timestamp
            )
        )

        // Save the alert and deployment in a single Firestore document
        firestore.collection("disaster_alerts")
            .add(alertData)
            .addOnSuccessListener {
              //  Toast.makeText(this, "Disaster alert and deployment saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("MediaDetailActivity", "Error saving disaster alert", e)
               // Toast.makeText(this, "Failed to save disaster alert.", Toast.LENGTH_SHORT).show()
            }
    }


}

