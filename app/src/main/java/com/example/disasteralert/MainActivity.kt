package com.example.disasteralert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var sendToCloud: Button
    private lateinit var locationText: TextView

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

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        imageView = findViewById(R.id.captured_image)
        videoView = findViewById(R.id.captured_video)
        progressBar = findViewById(R.id.upload_progress)
        sendToCloud = findViewById(R.id.upload_button)
        locationText = findViewById(R.id.location_text)

        // Check and request permissions
        checkPermissions()

        findViewById<Button>(R.id.capture_image_button).setOnClickListener {
            dispatchTakePictureIntent()
        }

        findViewById<Button>(R.id.capture_video_button).setOnClickListener {
            dispatchTakeVideoIntent()
        }

        sendToCloud.setOnClickListener {
            val description = findViewById<EditText>(R.id.description).text.toString().trim()

            if (description.isEmpty()) {
                Toast.makeText(this, "Please provide a description.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (::imageUri.isInitialized) {
                uploadImage(description)
            } else if (::videoUri.isInitialized) {
                uploadVideo(description)
            } else {
                Toast.makeText(this, "No media to upload", Toast.LENGTH_SHORT).show()
            }
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    locationText.text = "Location: Lat: $latitude, Long: $longitude"
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                    locationText.text = "Location: Not available"
                }
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
        sendToCloud.visibility = Button.VISIBLE
    }

    private fun previewVideo(data: Intent) {
        imageView.visibility = ImageView.GONE
        videoView.visibility = VideoView.VISIBLE
        videoUri = data.data!!
        videoView.setVideoURI(videoUri)
        videoView.start()
        sendToCloud.visibility = Button.VISIBLE
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
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show()
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
            "mediaType" to mediaType
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
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Captured Image", null)
        return Uri.parse(path)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
