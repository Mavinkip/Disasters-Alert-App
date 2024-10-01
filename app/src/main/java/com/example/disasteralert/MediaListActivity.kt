package com.example.disasteralert

import android.content.Intent
import com.google.firebase.firestore.SetOptions

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

        // Fetch media items
        fetchMediaItems(adapter)
    }

    private fun fetchMediaItems(adapter: MediaAdapter) {
        firestore.collection("media").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val description = document.getString("description") ?: ""
                val mediaUrl = document.getString("mediaUrl") ?: ""
                val mediaType = document.getString("mediaType") ?: ""
                val latitude = document.getDouble("latitude") ?: 0.0
                val longitude = document.getDouble("longitude") ?: 0.0
                val uniqueId = document.getString("uniqueId") ?: ""

                ///Toast.makeText(this, "Fetched uniqueId: $uniqueId", Toast.LENGTH_SHORT).show()


                // Create a MediaItem and add it to the list
                mediaList.add(MediaItem(description, mediaUrl, mediaType, latitude, longitude, uniqueId))
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
         //   Toast.makeText(this, "Error fetching media items: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMediaItemClick(mediaItem: MediaItem) {
        // Open MediaDetailActivity
        val intent = Intent(this, MediaDetailActivity::class.java).apply {
            putExtra("description", mediaItem.description)
            putExtra("mediaType", mediaItem.mediaType)
            putExtra("mediaUrl", mediaItem.mediaUrl)
            putExtra("latitude", mediaItem.latitude) // Pass latitude
            putExtra("longitude", mediaItem.longitude) // Pass longitude
        }

        // Update Firestore and log the media item details


        updateMediaItemStatus(mediaItem.uniqueId) // Pass the uniqueId to the update function
        //Toast.makeText(this, "Fetched detais: $mediaItem", Toast.LENGTH_SHORT).show()

        // Start the MediaDetailActivity
        startActivity(intent)
    }

    private fun updateMediaItemStatus(userId: String) {
        // Query the statuses collection where the userId matches
        firestore.collection("statuses")
            .whereEqualTo("userId", userId)
            .limit(1) // Assuming only one status per user
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.firstOrNull()
                    val documentId = document?.id // Get the document ID

                    if (documentId != null) {
                        // Proceed with updating the status
                        val statusData: MutableMap<String, Any> = HashMap()
                        statusData["status"] = "Processed" // Set the status to "Processed"

                        firestore.collection("statuses").document(documentId)
                            .update(statusData)
                            .addOnSuccessListener {
                             //   Toast.makeText(this, "Status updated to 'Processed' for user ID: $userId", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                            //    Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                     //   Toast.makeText(this, "No document found with user ID: $userId", Toast.LENGTH_SHORT).show()
                    }
                } else {
                  //  Toast.makeText(this, "No status document found for user ID: $userId", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving status document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
