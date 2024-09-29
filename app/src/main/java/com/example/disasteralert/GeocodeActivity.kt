package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
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

        // Retrieve latitude and longitude from intent
        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        val description = intent.getStringExtra("DESCRIPTION")

        // Automatically populate the fields if data is provided
        if (latitude != 0.0) {
            latitudeEditText.setText(latitude.toString())
        } else {
            latitudeEditText.setText("Latitude not available")
        }

        if (longitude != 0.0) {
            longitudeEditText.setText(longitude.toString())
        } else {
            longitudeEditText.setText("Longitude not available")
        }

        cityEditText.setText(description ?: "")  // Optionally show the description

        // Get address from coordinates
        convertButton.setOnClickListener {
            if (latitudeEditText.text.isNotEmpty() && longitudeEditText.text.isNotEmpty()) {
                getGeocodingData(latitudeEditText.text.toString(), longitudeEditText.text.toString(), fullAddressTextView, cityTextView)
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

    private val countyCityMap = mapOf(
        "Nairobi" to "Nairobi",
        "Mombasa" to "Mombasa",
        "Kisumu" to "Kisumu",
        "Nakuru" to "Nakuru",
        "Eldoret" to "Uasin Gishu",  // Eldoret is a town in Uasin Gishu County
        "Machakos" to "Machakos",
        "Nyeri" to "Nyeri",
        "Kiambu" to "Kiambu",
        "Meru" to "Meru",
        "Kericho" to "Kericho",
        "Embu" to "Embu",
        "Bomet" to "Bomet",
        "Narok" to "Narok",
        "Kajiado" to "Kajiado",
        "Laikipia" to "Nanyuki",  // Nanyuki is a town in Laikipia County
        "Isiolo" to "Isiolo",
        "Tharaka-Nithi" to "Chuka",  // Chuka is a town in Tharaka-Nithi County
        "Kitui" to "Kitui",
        "Machakos" to "Machakos",
        "Kwale" to "Ukunda",  // Ukunda is a town in Kwale County
        "Lamu" to "Lamu",
        "Tana River" to "Hola",  // Hola is a town in Tana River County
        "Garissa" to "Garissa",
        "Wajir" to "Wajir",
        "Mandera" to "Mandera",
        "Marsabit" to "Marsabit",
        "Meru" to "Meru",
        "Nyamira" to "Nyamira",
        "Migori" to "Migori",
        "Homa Bay" to "Homa Bay",
        "Siaya" to "Siaya",
        "Busia" to "Busia",
        "Vihiga" to "Vihiga",
        "Bungoma" to "Bungoma",
        "Trans Nzoia" to "Kitale",  // Kitale is a town in Trans Nzoia County
        "Uasin Gishu" to "Eldoret",  // Eldoret is a town in Uasin Gishu County
        "West Pokot" to "Kapenguria",  // Kapenguria is a town in West Pokot County
        "Elgeyo-Marakwet" to "Iten",  // Iten is a town in Elgeyo-Marakwet County
        "Bomet" to "Bomet",
        "Nyandarua" to "Ol Kalou",  // Ol Kalou is a town in Nyandarua County
        "Laikipia" to "Nanyuki",  // Nanyuki is a town in Laikipia County
        "Kirinyaga" to "Kerugoya",  // Kerugoya is a town in Kirinyaga County
        "Embu" to "Embu"
    )


    private fun getGeocodingData(lat: String, lng: String, fullAddressTextView: TextView, cityTextView: TextView) {
        val client = OkHttpClient()
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$lat,$lng&key=$openCageApiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GeocodeActivity, "Failed to get data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val jsonObject = JSONObject(jsonData)
                    val results = jsonObject.getJSONArray("results")

                    if (results.length() > 0) {
                        val firstResult = results.getJSONObject(0)
                        val formattedAddress = firstResult.getString("formatted")
                        val components = firstResult.getJSONObject("components")

                        val county = components.optString("county", "")
                        val country = components.optString("country", "") // Retrieve country

                        // Determine the city based on the county
                        val finalCity = countyCityMap[county] ?: "Nakuru"

                        runOnUiThread {
                            fullAddressTextView.text = "Full Address: $formattedAddress"
                            cityTextView.text = "City: $finalCity, County: $county, Country: $country"

                            // Pass finalCity to WeatherActivity if needed
                            val weatherIntent = Intent(this@GeocodeActivity, WeatherActivity::class.java)
                            weatherIntent.putExtra("CITY_NAME", finalCity)
                            startActivity(weatherIntent)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@GeocodeActivity, "No results found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    // Function to get coordinates from city name
    private fun getCoordinatesFromCity(cityName: String, fullAddressTextView: TextView, cityTextView: TextView) {
        val client = OkHttpClient()
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$cityName&key=$openCageApiKey"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GeocodeActivity, "Failed to get data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val jsonObject = JSONObject(jsonData)
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() > 0) {
                        val firstResult = results.getJSONObject(0)
                        val geometry = firstResult.getJSONObject("geometry")
                        val lat = geometry.getDouble("lat")
                        val lng = geometry.getDouble("lng")

                        runOnUiThread {
                            fullAddressTextView.text = "Coordinates: Latitude: $lat, Longitude: $lng"
                            cityTextView.text = ""
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@GeocodeActivity, "No coordinates found for the provided city name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}
