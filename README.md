Disaster Management App
Overview
The Disaster Management App is an Android application designed to help users stay informed about natural disasters and weather conditions in real-time. The app provides mapping capabilities, weather updates, and geocoding services to assist in disaster preparedness and management. It uses multiple APIs to achieve its functionality, including OpenCage Geocoder, OpenWeather, and osmdroid.

Features:
Real-time Weather Information: The app provides weather updates such as temperature, wind speed, and other weather-related details for specific cities or locations.
Mapping and Location Services: Using osmdroid and OpenCage APIs, the app allows users to see their current location and find routes to destinations on a map.
Disaster Alerts: The app offers real-time alerts related to weather and potential natural disasters (floods, storms, etc.).
APIs Used
1. OpenCage Geocoder API
Overview: The OpenCage Geocoder API provides forward and reverse geocoding services. It allows the app to convert geographic coordinates (latitude and longitude) into readable addresses (reverse geocoding) and vice versa (forward geocoding). This feature is crucial for determining a user’s location or destination based on input.
Key Use in App:
Convert user’s coordinates to an address to show where they are currently located on the map.
Convert a destination address into latitude and longitude to map routes.
How to use:
To use OpenCage, you need an API key. The request format looks like this:

bash
Copy code
https://api.opencagedata.com/geocode/v1/json?q={latitude},{longitude}&key=YOUR_API_KEY
In your app, you’ve integrated this API to help users see both their current location and search for places by address.

2. OpenWeather API
Overview: The OpenWeather API provides real-time weather data such as temperature, humidity, wind speed, and disaster-related warnings. It supports various endpoints, including those for current weather, hourly forecasts, and severe weather alerts.
Key Use in App:
Display the current weather for the user’s location or a city selected by the user.
Display additional weather details like wind speed, humidity, and warnings about severe weather conditions.
Use this information to alert users to possible weather-related disasters.
How to use:
The OpenWeather API requires an API key. Example request:

bash
Copy code
https://api.openweathermap.org/data/2.5/weather?q={city_name}&appid=YOUR_API_KEY
In the app, it fetches and displays weather data on the dashboard, allowing users to stay updated about local and global weather conditions.

3. osmdroid Library
Overview: osmdroid is an open-source library that provides a powerful way to add maps and mapping functionality to Android apps. Unlike Google Maps, osmdroid uses OpenStreetMap data, which is free to use, making it ideal for applications with global requirements.
Key Use in App:
Display the user's current location on an OpenStreetMap-based map.
Allow users to input a destination address and display the route from their current location to that destination.
Provide map-based visualizations, such as highlighting disaster-prone areas or showing nearby shelters during emergencies.
How to use:
osmdroid is integrated by including the dependency in your project and configuring it to show maps and manage geolocation services. For example:

gradle
Copy code
implementation 'org.osmdroid:osmdroid-android:6.1.10'
In the app, osmdroid is used to present an interactive map interface where users can search for locations, view their current position, and see routes.

App Setup and Installation
Prerequisites
Android Studio (latest version)
Android SDK and emulator or physical device for testing
API keys for:
OpenCage Geocoder (https://opencagedata.com/)
OpenWeather (https://openweathermap.org/)
Installation Steps
Clone the repository:

bash
Copy code
git clone https://github.com/Mavinkip/Disasters-Alert-App
Open the project in Android Studio.

Configure your API keys:

Replace the placeholder YOUR_API_KEY in your code with your actual API keys for OpenCage and OpenWeather.
Sync the Gradle project to download all dependencies.

Run the app on an Android emulator or device.

Dependencies
OpenCage Geocoder: Used for converting between geographic coordinates and readable addresses.
OpenWeather API: Used for fetching real-time weather data.
osmdroid: Used for displaying interactive maps and routing.
How the App Works
Dashboard:

The home screen of the app displays the current weather conditions for the user’s location, along with any warnings or disaster alerts related to weather.
Users can also search for weather information for different cities using the OpenWeather API.
Map View:

The map screen shows the user's current location using osmdroid and OpenStreetMap data.
Users can input a destination, and the app will plot a route on the map using reverse geocoding from the OpenCage API and routing logic.
Alerts and Notifications:

When severe weather conditions or disasters (such as storms, floods, etc.) are detected, the app will notify the user with alerts and suggestions for action.
API Configuration
OpenCage Geocoder API
To configure the OpenCage API in your app:

Sign up for an API key at OpenCage Geocoder.
Set your API key in the code where geocoding requests are made.
Example integration:

kotlin
Copy code
val requestUrl = "https://api.opencagedata.com/geocode/v1/json?q=$latitude,$longitude&key=$API_KEY"
OpenWeather API
To set up OpenWeather in the app:

Create an API key at OpenWeather.
Replace the placeholder API key in the weather request URL in your project.
Example integration:

kotlin
Copy code
val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY"
osmdroid
osmdroid requires minimal configuration:

Add the osmdroid dependency to your build.gradle file.
Set permissions for location and map display in your AndroidManifest.xml.
Example integration:

xml
Copy code
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
License
This project is licensed under the MIT License - see the LICENSE file for details.

