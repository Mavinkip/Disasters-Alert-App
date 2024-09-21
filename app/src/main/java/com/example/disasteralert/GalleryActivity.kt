package com.example.disasteralert


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class GalleryActivity : AppCompatActivity() {

    private lateinit var chooseImage: Button
    private lateinit var chooseVideo: Button
    private lateinit var sendToCloud: Button
    private lateinit var selectedImage: ImageView
    private lateinit var selectedVideo: VideoView
    private lateinit var txtDescription: TextView

    private var imageUri: Uri? = null
    private var videoUri: Uri? = null

    private val PICK_IMAGE = 100
    private val PICK_VIDEO = 200

    // Firebase Storage
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        txtDescription = findViewById(R.id.diplay)
        selectedImage = findViewById(R.id.image_taken)
        selectedVideo = findViewById(R.id.video_taken)
        chooseImage = findViewById(R.id.imageBtn)
        chooseVideo = findViewById(R.id.videoBtn)
        sendToCloud = findViewById(R.id.send_to_cloud)

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        chooseImage.setOnClickListener { chooseImageFromGallery() }
        chooseVideo.setOnClickListener { chooseVideoFromGallery() }

        sendToCloud.setOnClickListener {
            if (imageUri != null) {
                uploadImage()
            } else if (videoUri != null) {
                uploadVideo()
            }
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun chooseVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                previewImage(data)
            } else if (requestCode == PICK_VIDEO) {
                previewVideo(data)
            }
        }
    }

    private fun previewImage(data: Intent) {
        txtDescription.visibility = TextView.GONE
        selectedVideo.visibility = VideoView.GONE
        selectedImage.visibility = ImageView.VISIBLE
        videoUri = null


        imageUri = data.data
        selectedImage.setImageURI(imageUri)

        sendToCloud.visibility = Button.VISIBLE
    }

    private fun previewVideo(data: Intent) {
        txtDescription.visibility = TextView.GONE
        selectedImage.visibility = ImageView.GONE
        selectedVideo.visibility = VideoView.VISIBLE
        imageUri = null


        videoUri = data.data
        selectedVideo.setVideoURI(videoUri)
        selectedVideo.start()

        sendToCloud.visibility = Button.VISIBLE
    }

    private fun uploadImage() {
        imageUri?.let {
            val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Here you can save the image URL to Firestore
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun uploadVideo() {
        videoUri?.let {
            val videoRef = storageReference.child("videos/${UUID.randomUUID()}.mp4")
            videoRef.putFile(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show()
                    videoRef.downloadUrl.addOnSuccessListener { uri ->
                        // Here you can save the video URL to Firestore
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
