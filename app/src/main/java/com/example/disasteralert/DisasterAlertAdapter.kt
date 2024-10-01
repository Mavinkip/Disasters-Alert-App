package com.example.disasteralert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class DisasterAlertAdapter(private var alertList: List<DisasterAlert>) :
    RecyclerView.Adapter<DisasterAlertAdapter.AlertViewHolder>() {

    inner class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detailsTextView: TextView = view.findViewById(R.id.details_text)
        val levelTextView: TextView = view.findViewById(R.id.level_text)
        val locationTextView: TextView = view.findViewById(R.id.location_text)
        val timestampTextView: TextView = view.findViewById(R.id.timestamp_text)
        val serviceTextView: TextView = view.findViewById(R.id.service_text) // Add service view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_disaster_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]
        holder.detailsTextView.text = "Details: ${alert.details}"
        holder.levelTextView.text = "Level: ${alert.level}"

        // Get location details
        val latitude = alert.location["latitude"] ?: 0.0
        val longitude = alert.location["longitude"] ?: 0.0
        holder.locationTextView.text = "Location: Lat: $latitude, Long: $longitude"

        // Get and format the timestamp (make sure it's not null)
        val timestamp = alert.timestamp?.toDate()?.toString() ?: "No timestamp"
        holder.timestampTextView.text = "Timestamp: $timestamp"

        // Set the service deployed
        holder.serviceTextView.text = "Service Deployed: ${alert.serviceDeployed}"
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    fun updateData(newAlertList: List<DisasterAlert>) {
        alertList = newAlertList
        notifyDataSetChanged()
    }
}
