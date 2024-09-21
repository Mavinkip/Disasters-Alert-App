package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            openMediaDetailActivity(mediaItem)
        }
        mediaRecyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()
        firestore.collection("media").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val description = document.getString("description") ?: ""
                val mediaUrl = document.getString("mediaUrl") ?: ""
                val mediaType = document.getString("mediaType") ?: ""
                mediaList.add(MediaItem(description, mediaUrl, mediaType))
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun openMediaDetailActivity(mediaItem: MediaItem) {
        val intent = Intent(this, MediaDetailActivity::class.java)
        intent.putExtra("description", mediaItem.description)
        intent.putExtra("mediaUrl", mediaItem.mediaUrl)
        intent.putExtra("mediaType", mediaItem.mediaType)
        startActivity(intent)
    }

    data class MediaItem(val description: String, val mediaUrl: String, val mediaType: String)

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
            holder.itemView.setOnClickListener { onItemClick(mediaItem) }
        }

        override fun getItemCount(): Int = mediaList.size

        class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val descriptionTextView: TextView = view.findViewById(R.id.media_description)
            val mediaTypeTextView: TextView = view.findViewById(R.id.media_type)
        }
    }
}
