package com.example.disasteralert

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MediaDetailActivity : AppCompatActivity() {

    private lateinit var mediaImageView: ImageView
    private lateinit var mediaVideoView: VideoView
    private lateinit var descriptionTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)

        mediaImageView = findViewById(R.id.media_image)
        mediaVideoView = findViewById(R.id.media_video)
        descriptionTextView = findViewById(R.id.media_description)
        loadingProgressBar = findViewById(R.id.media_loading) // Initialize ProgressBar

        // Retrieve data from intent
        val description = intent.getStringExtra("description") ?: "No description available"
        val mediaUrl = intent.getStringExtra("mediaUrl")
        val mediaType = intent.getStringExtra("mediaType")

        descriptionTextView.text = description

        // Load media based on its type
        if (mediaType == "image") {
            loadImage(mediaUrl)
        } else if (mediaType == "video") {
            loadVideo(mediaUrl)
        } else {
            descriptionTextView.text = "Unsupported media type."
        }
    }

    private fun loadImage(mediaUrl: String?) {
        mediaImageView.visibility = ImageView.VISIBLE
        mediaVideoView.visibility = VideoView.GONE
        loadingProgressBar.visibility = ProgressBar.GONE // No need to show loader for image

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
        loadingProgressBar.visibility = ProgressBar.VISIBLE // Show loading indicator

        if (!mediaUrl.isNullOrEmpty()) {
            val mediaController = android.widget.MediaController(this)
            mediaVideoView.setMediaController(mediaController)
            mediaController.setAnchorView(mediaVideoView)

            mediaVideoView.setVideoURI(Uri.parse(mediaUrl))

            mediaVideoView.setOnPreparedListener { mediaPlayer ->
                loadingProgressBar.visibility = ProgressBar.GONE // Hide loader when ready
                mediaPlayer.start() // Start playback automatically when ready
            }

            mediaVideoView.setOnErrorListener { _, what, extra ->
                loadingProgressBar.visibility = ProgressBar.GONE // Hide loader on error
                mediaVideoView.visibility = VideoView.GONE
                descriptionTextView.text = "Failed to play video. Error: what=$what, extra=$extra"
                true // Returning true indicates the error was handled
            }
        } else {
            descriptionTextView.text = "Video URL is not valid."
            mediaVideoView.visibility = VideoView.GONE
            loadingProgressBar.visibility = ProgressBar.GONE // Hide loader if URL is invalid
        }
    }
}
