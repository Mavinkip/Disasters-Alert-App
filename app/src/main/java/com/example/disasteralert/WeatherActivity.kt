package com.example.disasteralert

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    private var city: String? = null // Nullable property for city name
    private val API: String = "2bccda4adbd64e5fcb506f56f4cdda49"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather)

        // Get the city name and country code from intent, default to "Kapsabet, KE"
        city = intent.getStringExtra("CITY_NAME") ?: "kapsabet,ke"

        // Fetch the weather data for the city
        fetchWeatherData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CITY_CODE && resultCode == RESULT_OK) {
            val newCity = data?.getStringExtra("CITY_NAME")
            if (!newCity.isNullOrEmpty()) {
                city = newCity // Update city variable
                fetchWeatherData() // Fetch data for the new city
            }
        }
    }

    private fun fetchWeatherData() {
        city?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Fetch weather data for the city using OpenWeatherMap API
                    val response = URL("https://api.openweathermap.org/data/2.5/weather?q=$it&units=metric&appid=$API").readText(Charsets.UTF_8)

                    // Update UI with the response data on the main thread
                    withContext(Dispatchers.Main) {
                        updateUI(response)
                    }
                } catch (e: Exception) {
                    // Show error message on the main thread if there's an exception
                    withContext(Dispatchers.Main) {
                        showError(e.message)
                    }
                }
            }
        }
    }

    private fun updateUI(response: String) {
        try {
            // Parse the JSON response
            val jsonOb = JSONObject(response)
            val main = jsonOb.getJSONObject("main")
            val sys = jsonOb.getJSONObject("sys")
            val wind = jsonOb.getJSONObject("wind")
            val weather = jsonOb.getJSONArray("weather").getJSONObject(0)
            val updatedAt: Long = jsonOb.getLong("dt")
            val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(updatedAt * 1000)
            val temp = main.getString("temp") + "C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")
            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description").capitalize()
            val address = jsonOb.getString("name") + "," + sys.getString("country")

            // Update UI elements with fetched weather details
            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.update_at).text = updatedAtText
            findViewById<TextView>(R.id.status).text = weatherDescription
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<TextView>(R.id.temp_min).text = tempMin
            findViewById<TextView>(R.id.temp_max).text = tempMax
            findViewById<TextView>(R.id.Sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            findViewById<TextView>(R.id.wind).text = windSpeed
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity

            // Hide loader and show main container
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            findViewById<TextView>(R.id.errortext).visibility = View.GONE

        } catch (e: Exception) {
            showError(e.message) // Show error if JSON parsing fails
        }
    }

    private fun showError(errorMessage: String?) {
        // Show error message and hide the main container
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        val errorTextView = findViewById<TextView>(R.id.errortext)
        errorTextView.text = errorMessage ?: "Something went wrong"
        errorTextView.visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
    }

    companion object {
        const val REQUEST_CITY_CODE = 1 // Request code for city selection
    }
}
