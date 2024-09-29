package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MediaListActivity : AppCompatActivity() {

    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private val mediaList = mutableListOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_list)

        mediaRecyclerView = findViewById(R.id.media_recycler_view)
        mediaRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = MediaAdapter(mediaList) { mediaItem ->
            onMediaItemClick(mediaItem) // Use the new click handler
        }
        mediaRecyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()
        // Fetch all necessary fields from Firestore
        firestore.collection("media").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val description = document.getString("description") ?: ""
                val mediaUrl = document.getString("mediaUrl") ?: ""
                val mediaType = document.getString("mediaType") ?: ""
                val latitude = document.getDouble("latitude") ?: 0.0
                val longitude = document.getDouble("longitude") ?: 0.0
                val uniqueId = document.getString("uniqueId") ?: ""

                // Add the MediaItem to the list with all the fields
                mediaList.add(MediaItem(description, mediaUrl, mediaType, latitude, longitude, uniqueId))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun onMediaItemClick(mediaItem: MediaItem) {
        // Open MediaDetailActivity
        val intent = Intent(this, MediaDetailActivity::class.java)
        intent.putExtra("description", mediaItem.description)
        intent.        putExtra("mediaType", mediaItem.mediaType)

        intent.        putExtra("mediaUrl", mediaItem.mediaUrl)

        intent.  putExtra("latitude", mediaItem.latitude) // Pass latitude
        intent. putExtra("longitude", mediaItem.longitude) // Pass longitu
        // Update Firestore and log the media item details


        updateMediaItemStatus(mediaItem)

        // Start the MediaDetailActivity
        startActivity(intent)
    }

    private fun updateMediaItemStatus(mediaItem: MediaItem) {
        // Change status to "Processed"
        mediaItem.status = "Processed"

        // Update the Firestore document with the new status
        firestore.collection("media").document(mediaItem.uniqueId).update("status", mediaItem.status)
            .addOnSuccessListener {
                Toast.makeText(this, "Status updated to 'Processed'", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Optionally log media item details
        logMediaItem(mediaItem)
    }

    private fun logMediaItem(mediaItem: MediaItem) {
        // Log the details of the selected media item
        val message = """
            Description: ${mediaItem.description}
            Latitude: ${mediaItem.latitude}
            Longitude: ${mediaItem.longitude}
            Media Type: ${mediaItem.mediaType}
            Media URL: ${mediaItem.mediaUrl}
            Unique ID: ${mediaItem.uniqueId}
            Status: ${mediaItem.status}
        """.trimIndent()

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // MediaItem data class to include latitude, longitude, and uniqueId
    data class MediaItem(
        val description: String,
        val mediaUrl: String,
        val mediaType: String,
        val latitude: Double,
        val longitude: Double,
        val uniqueId: String,
        var status: String = "Processing" // Default status
    )

    class MediaAdapter(
        private val mediaList: List<MediaItem>,
        private val onItemClick: (MediaItem) -> Unit
    ) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.media_item, parent, false)
            return MediaViewHolder(view)
        }

        override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
            val mediaItem = mediaList[position]
            holder.descriptionTextView.text = mediaItem.description
            holder.mediaTypeTextView.text = mediaItem.mediaType

            // Set the initial status
            holder.statusTextView.text = mediaItem.status

            holder.itemView.setOnClickListener {
                // Notify the click action
                onItemClick(mediaItem)
            }
        }

        override fun getItemCount(): Int = mediaList.size

        class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val descriptionTextView: TextView = view.findViewById(R.id.media_description)
            val mediaTypeTextView: TextView = view.findViewById(R.id.media_type)
            val statusTextView: TextView = view.findViewById(R.id.media_status) // Add this line
        }
    }
}
