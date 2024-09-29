package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class GeocodeActivity : AppCompatActivity() {

    private val openCageApiKey = "73377f87c0e143fca40cd7132e705b07"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geocode)

        val latitudeEditText = findViewById<EditText>(R.id.latitudeEditText)
        val longitudeEditText = findViewById<EditText>(R.id.longitudeEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val convertButton = findViewById<Button>(R.id.convertButton)
        val getCoordinatesButton = findViewById<Button>(R.id.getCoordinatesButton)
        val fullAddressTextView = findViewById<TextView>(R.id.fullAddressTextView)
        val cityTextView = findViewById<TextView>(R.id.cityTextView)

        // Get address from coordinates
        convertButton.setOnClickListener {
            val latitude = latitudeEditText.text.toString()
            val longitude = longitudeEditText.text.toString()

            if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                getGeocodingData(latitude, longitude, fullAddressTextView, cityTextView)
            } else {
                Toast.makeText(this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show()
            }
        }

        // Get coordinates from city name
        getCoordinatesButton.setOnClickListener {
            val cityName = cityEditText.text.toString()

            if (cityName.isNotEmpty()) {
                getCoordinatesFromCity(cityName, fullAddressTextView, cityTextView)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to get address from coordinates
    private fun getGeocodingData(lat: String, lng: String, fullAddressTextView: TextView, cityTextView: TextView) {
        val client = OkHttpClient()
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$lat+$lng&key=$openCageApiKey"

        // Start the network request in a background thread
        Thread {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parse the JSON response
                        val json = JSONObject(it)
                        val resultsArray = json.getJSONArray("results")
                        if (resultsArray.length() > 0) {
                            val result = resultsArray.getJSONObject(0)
                            val formattedAddress = result.getString("formatted")

                            // Update the UI with the formatted address
                            runOnUiThread {
                                fullAddressTextView.text = "Full Address: $formattedAddress"
                            }

                            // Extract city name and country code
                            val components = result.getJSONObject("components")
                            val city = components.optString("city", components.optString("town", components.optString("village", "")))
                            val countryCode = components.optString("country_code", "")

                            // Update the city TextView with "City, Country" format
                            runOnUiThread {
                                if (city.isNotEmpty() && countryCode.isNotEmpty()) {
                                    cityTextView.text = "City: $city, $countryCode"
                                    // Pass city to WeatherActivity if needed
                                    val weatherIntent = Intent(this@GeocodeActivity, WeatherActivity::class.java)
                                    weatherIntent.putExtra("CITY_NAME", "$city, $countryCode")
                                    startActivity(weatherIntent)
                                } else {
                                    cityTextView.text = "City or Country not found in the address components"
                                }
                            }
                        } else {
                            runOnUiThread {
                                fullAddressTextView.text = "No address found for the provided coordinates"
                                cityTextView.text = ""
                            }
                        }
                    }
                } else {
                    Log.e("GeocodeActivity", "Geocoding request failed")
                }
            } catch (e: IOException) {
                Log.e("GeocodeActivity", "Network error", e)
            }
        }.start()
    }

    // Function to get coordinates from city name
    private fun getCoordinatesFromCity(cityName: String, fullAddressTextView: TextView, cityTextView: TextView) {
        val client = OkHttpClient()
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$cityName&key=$openCageApiKey"

        // Start the network request in a background thread
        Thread {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parse the JSON response
                        val json = JSONObject(it)
                        val resultsArray = json.getJSONArray("results")
                        if (resultsArray.length() > 0) {
                            val result = resultsArray.getJSONObject(0)
                            val geometry = result.getJSONObject("geometry")
                            val lat = geometry.getDouble("lat")
                            val lng = geometry.getDouble("lng")

                            // Update the UI with the coordinates
                            runOnUiThread {
                                fullAddressTextView.text = "Coordinates: Latitude: $lat, Longitude: $lng"
                                cityTextView.text = ""
                            }
                        } else {
                            runOnUiThread {
                                fullAddressTextView.text = "No coordinates found for the provided city name"
                                cityTextView.text = ""
                            }
                        }
                    }
                } else {
                    Log.e("GeocodeActivity", "Geocoding request failed")
                }
            } catch (e: IOException) {
                Log.e("GeocodeActivity", "Network error", e)
            }
        }.start()
    }
}
