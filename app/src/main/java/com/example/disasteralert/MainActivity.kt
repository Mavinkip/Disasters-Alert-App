package com.example.disasteralert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadButton: Button
    private lateinit var locationText: TextView
    private lateinit var status_text: TextView
    private lateinit var alertlayout: View



    private lateinit var alertLevelText: TextView
    private lateinit var disasterInfoText: TextView
    private lateinit var alertDetailsText: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_VIDEO_CAPTURE = 2
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_LOCATION_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Storage and Firestore
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize UI components
        imageView = findViewById(R.id.captured_image)
        videoView = findViewById(R.id.captured_video)
        progressBar = findViewById(R.id.upload_progress)
        uploadButton = findViewById(R.id.upload_button)
        locationText = findViewById(R.id.location_text)
        status_text = findViewById(R.id.status_text)

        alertLevelText = findViewById(R.id.alert_level_text)
        disasterInfoText = findViewById(R.id.disaster_info_text)
        alertDetailsText = findViewById(R.id.alert_details_text)

        alertlayout = findViewById(R.id.alertLayout) // Ensure you reference the correct layout ID here



        firestore = FirebaseFirestore.getInstance()
        fetchDisasterAlertDetails()


        alertlayout.setOnClickListener{
            startActivity(Intent(this, InformActivity ::class.java))
        }


        status_text.setOnClickListener {
            val intent = Intent(this, MediaListActivity::class.java)
            startActivity(intent)
        }

        // Check and request permissions
        checkPermissions()

        // Set click listeners for buttons
        findViewById<ImageButton>(R.id.capture_image_button).setOnClickListener {
            dispatchTakePictureIntent()
        }

        findViewById<ImageButton>(R.id.capture_video_button).setOnClickListener {
            dispatchTakeVideoIntent()
        }

        uploadButton.setOnClickListener {
            handleUpload()
        }

        // Get the current location
        getLocation()
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
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
                    latitude = location.latitude
                    longitude = location.longitude
                    locationText.text = "Location: Lat: $latitude, Long: $longitude"
                } else {
                    locationText.text = "Location: Not available"
                    Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error getting location: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun dispatchTakeVideoIntent() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> previewImage(data!!)
                REQUEST_VIDEO_CAPTURE -> previewVideo(data!!)
            }
        } else {
            Toast.makeText(this, "Capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun previewImage(data: Intent) {
        videoView.visibility = VideoView.GONE
        imageView.visibility = ImageView.VISIBLE
        imageUri = data.data ?: getImageUriFromBitmap(data.extras?.get("data") as Bitmap)
        imageView.setImageURI(imageUri)
        uploadButton.visibility = Button.VISIBLE
    }

    private fun previewVideo(data: Intent) {
        imageView.visibility = ImageView.GONE
        videoView.visibility = VideoView.VISIBLE
        videoUri = data.data!!
        videoView.setVideoURI(videoUri)
        videoView.start()
        uploadButton.visibility = Button.VISIBLE
    }

    private fun handleUpload() {
        val description = findViewById<EditText>(R.id.description).text.toString().trim()

        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description.", Toast.LENGTH_SHORT).show()
            return
        }

        if (::imageUri.isInitialized) {
            uploadImage(description)
        } else if (::videoUri.isInitialized) {
            uploadVideo(description)
        } else {
            Toast.makeText(this, "No media to upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(description: String) {
        val uniqueId = firestore.collection("media").document().id
        val imageRef = storage.reference.child("images/$uniqueId.jpg")
        progressBar.visibility = ProgressBar.VISIBLE

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    sendDescriptionToFirestore(description, downloadUrl, "image")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = ProgressBar.GONE
            }
    }

    private fun uploadVideo(description: String) {
        val uniqueId = firestore.collection("media").document().id
        val videoRef = storage.reference.child("videos/$uniqueId.mp4")
        progressBar.visibility = ProgressBar.VISIBLE

        videoRef.putFile(videoUri)
            .addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    sendDescriptionToFirestore(description, downloadUrl, "video")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = ProgressBar.GONE
            }
    }

    private fun sendDescriptionToFirestore(description: String, mediaUrl: String, mediaType: String) {
        val uniqueId = firestore.collection("media").document().id
        val data = hashMapOf(
            "uniqueId" to uniqueId,
            "description" to description,
            "mediaUrl" to mediaUrl,
            "mediaType" to mediaType,
            "latitude" to latitude,
            "longitude" to longitude
        )

        firestore.collection("media").document(uniqueId)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Description added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add description", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "CapturedImage", null)
        return Uri.parse(path)
    }
    private fun fetchDisasterAlertDetails() {
        firestore.collection("alerts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { documents, exception ->
                if (exception != null) {
                    Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null && !documents.isEmpty) {
                    for (document in documents) {
                        val alertLevel = document.getString("alert_level") ?: "No Level"
                        val details = document.getString("details") ?: "No Details"
                        val status = document.getString("status") ?: "Processing"  // Retrieve the status

                        alertLevelText.text = alertLevel
                        alertDetailsText.text = details
                        status_text.text = status  // Update the status TextView

                        // Call the function to update background and show toast
                        updateAlertLayoutColor(alertLevel)
                    }
                } else {
                    alertLevelText.text = "No Alerts"
                    alertDetailsText.text = "No Details"
                    status_text.text = "No Status"  // Handle case with no alerts
                }
            }
    }

    private fun updateAlertLayoutColor(alertLevel: String) {
        val alertLayout: LinearLayout = findViewById(R.id.alertLayout) // Reference your LinearLayout

        // Ensure the background update happens on the main thread
        runOnUiThread {
            when (alertLevel) {
                "Level 1" -> {
                    alertLayout.setBackgroundResource(R.drawable.radio_button_background_green)
                    Toast.makeText(this, "Alert Level 1", Toast.LENGTH_SHORT).show()
                }
                "Level 2" -> {
                    alertLayout.setBackgroundResource(R.drawable.radio_button_background_yellow)
                    Toast.makeText(this, "Alert Level 2", Toast.LENGTH_SHORT).show()
                }
                "Level 3" -> {
                    alertLayout.setBackgroundResource(R.drawable.radio_button_background_orange)
                    Toast.makeText(this, "Alert Level 3", Toast.LENGTH_SHORT).show()
                }
                "Level 4" -> {
                    alertLayout.setBackgroundResource(R.drawable.radio_button_background_red)
                    Toast.makeText(this, "Alert Level 4", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    alertLayout.setBackgroundResource(R.drawable.radio_button_background_green) // Default to green
                    Toast.makeText(this, "Unknown Alert Level", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





}
