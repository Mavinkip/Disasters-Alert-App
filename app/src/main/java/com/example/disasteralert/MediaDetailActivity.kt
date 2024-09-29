package com.example.disasteralert

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

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

    private lateinit var radioLevel1: RadioButton
    private lateinit var radioLevel2: RadioButton
    private lateinit var radioLevel3: RadioButton
    private lateinit var radioLevel4: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)

        mediaImageView = findViewById(R.id.media_image)
        mediaVideoView = findViewById(R.id.media_video)
        descriptionTextView = findViewById(R.id.media_description)
        loadingProgressBar = findViewById(R.id.media_loading)
        weatherButton = findViewById(R.id.weatherid)
        mapButton = findViewById(R.id.mapid)

        deployButton = findViewById(R.id.deployid)
        disasterDetailsEditText =
            findViewById(R.id.disaster_details_input) // Update this ID based on your XML
        alertButton = findViewById(R.id.alert_button) // Update this ID based on your XML

        radioGroup = findViewById(R.id.radio_group) // Make sure this ID is correct in your XML

        radioLevel1 = findViewById(R.id.radio_level_1)
        radioLevel2 = findViewById(R.id.radio_level_2)
        radioLevel3 = findViewById(R.id.radio_level_3)
        radioLevel4 = findViewById(R.id.radio_level_4)

        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Clear tick from all RadioButtons first
            radioLevel1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            when (checkedId) {
                R.id.radio_level_1 -> {
                    // Show tick icon for Level 1 on the right side
                    radioLevel1.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_2 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel2.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_3 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel3.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_4 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel4.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Clear tick from all RadioButtons first
            radioLevel1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            when (checkedId) {
                R.id.radio_level_1 -> {
                    // Show tick icon for Level 1 on the right side
                    radioLevel1.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_2 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel2.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_3 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel3.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_4 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel4.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Clear tick from all RadioButtons first
            radioLevel1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel4.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)


            when (checkedId) {
                R.id.radio_level_1 -> {
                    // Show tick icon for Level 1 on the right side
                    radioLevel1.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_2 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel2.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }
                R.id.radio_level_3 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel3.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

                R.id.radio_level_4 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel4.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }

            }
        }


        // Set click listeners for circular image buttons
        weatherButton.setOnClickListener {
            startActivity(Intent(this, GeocodeActivity::class.java))
        }

        mapButton.setOnClickListener {
            startActivity(Intent(this, RouteActivity::class.java))
        }

        deployButton.setOnClickListener {
            startActivity(Intent(this, DeploymentActivity::class.java))
        }

        // Set up Alert button click listener
        alertButton.setOnClickListener {
            val disasterDetails = disasterDetailsEditText.text.toString()
            // Handle the alert logic here (e.g., send data to a server)
        }

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
                descriptionTextView.text = "Failed to play video. Error: what=$what, extra=$extra"
                true
            }
        } else {
            descriptionTextView.text = "Video URL is not valid."
            mediaVideoView.visibility = VideoView.GONE
            loadingProgressBar.visibility = ProgressBar.GONE
        }
    }
}