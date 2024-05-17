package together.devs.playtogether

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import java.io.IOException
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.TilesOverlay
import together.devs.playtogether.init.Event
import together.devs.playtogether.init.Team

class HomeActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mapView: MapView
    private var roadOverlay: Polyline? = null
    private lateinit var roadManager: RoadManager

    // Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false
    private var smoothedAzimuth = 0f
    private var lastUpdateTime = 0L
    private val updateInterval = 500

    private var lightSensor: Sensor? = null
    private lateinit var lightEventListener: SensorEventListener

    private lateinit var compassImage: ImageView
    private lateinit var Direccion: EditText
    private var deporteSeleccionado: String = "Todos"
    private val markers = ArrayList<Marker>()
    private val marcadoresDeportes = mapOf(
        "Cancha Fútbol" to "Fútbol",
        "Cancha Tenis" to "Tenis",
        "Cancha Americano" to "Americano",
        "Cancha Voleibol" to "Voleibol",
        "Cancha Baloncesto" to "Baloncesto",
        "Cancha Beisbol" to "Beisbol"
    )

    // Location
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        compassImage = findViewById(R.id.compassImage)
        Direccion = findViewById(R.id.address)

        roadManager = OSRMRoadManager(this, "ANDROID")

        // Sensor initialization
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Light Sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            Toast.makeText(this, "No light sensor found!", Toast.LENGTH_SHORT).show()
        } else {
            lightEventListener = createLightSensorListener()
            sensorManager.registerListener(
                lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

        // Location setup
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = createLocationCallback()
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(
                this, fineLocationPermission
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, coarseLocationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, fineLocationPermission
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, coarseLocationPermission
                )
            ) {
                // Show explanation if needed
            }

            // Request location permissions
            ActivityCompat.requestPermissions(
                this, arrayOf(fineLocationPermission, coarseLocationPermission), LOCALIZACION
            )
        } else {
            setupMap()
            setUpMapViewListener()
            setupSpinner()
            setupEditorActionListener()
            startLocationUpdates()
        }

        val but2: Button = findViewById(R.id.verEquiposButton)
        val but3: Button = findViewById(R.id.verEventosButton)
        val but4: Button = findViewById(R.id.verPerfilButton)
        val but5: Button = findViewById(R.id.verChatButton)

        but2.setOnClickListener {
            intent = Intent(this, Team::class.java)
            startActivity(intent)
        }
        but3.setOnClickListener {
            intent = Intent(this, Event::class.java)
            startActivity(intent)
        }
        but4.setOnClickListener {
            intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        but5.setOnClickListener {
            intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permissions granted
            startLocationUpdates()
        } else {
            Toast.makeText(
                applicationContext, "Location permission is required", Toast.LENGTH_LONG
            ).show()
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCALIZACION
            )
        }
    }

    private fun setupMap() {
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
    }

    private fun setupSpinner() {
        val spinnerDeportes = findViewById<Spinner>(R.id.spinnerDeportes)
        val deportes = resources.getStringArray(R.array.deportes_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deportes)
        spinnerDeportes.adapter = adapter

        spinnerDeportes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val deporte = deportes[position]
                deporteSeleccionado = deporte
                aplicarFiltroDeporte(deporte)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }
    }

    private fun setupEditorActionListener() {
        Direccion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val dir = Direccion.text.toString()
                val geoPoint = buscarDireccion(dir)
                if (dir.isNotEmpty()) {
                    if (geoPoint != null) {
                        showMarker(geoPoint, "Ubicacion encontrada", "B_Dir")
                        val mapController = mapView.controller
                        mapController.setZoom(18.0)
                        mapController.setCenter(geoPoint)
                    } else {
                        Toast.makeText(
                            this, "La direccion no pudo ser encontrada", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                true
            } else {
                false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCALIZACION) {
            var fineLocationGranted = false
            var coarseLocationGranted = false
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        fineLocationGranted = true
                    }
                } else if (permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        coarseLocationGranted = true
                    }
                }
            }

            if (fineLocationGranted && coarseLocationGranted) {
                Toast.makeText(this, "PERMISOS DE LOCALIZACIÓN DADOS", Toast.LENGTH_SHORT).show()
                startLocationUpdates()
            } else {
                Toast.makeText(
                    this, "ALGUNO DE LOS PERMISOS DE LOCALIZACIÓN NO FUE DADO", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        sensorManager.registerListener(
            lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
        sensorManager.unregisterListener(lightEventListener)
        stopLocationUpdates()
    }

    private fun createLocationRequest(): LocationRequest {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000).build()
        return locationRequest
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if (result != null) {
                    val location = result.lastLocation!!
                    updateUI(GeoPoint(location.latitude, location.longitude))
                    locationClient.removeLocationUpdates(this)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            createLocationRequest()
            locationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateUI(location: GeoPoint) {
        showMarker(location, "My location", "U_A")
        mapView.controller.setZoom(18.0)
        mapView.controller.setCenter(location)
    }

    private fun showMarker(geoPoint: GeoPoint, markerName: String, tipo: String) {
        val marker = Marker(mapView)
        marker.title = markerName
        marker.position = geoPoint

        marker.setOnMarkerClickListener { _, _ ->
            calculateRouteToMarker(geoPoint)
            true
        }

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        when (tipo) {
            "U_A" -> marker.icon = resources.getDrawable(R.drawable.baseline_emoji_people_24, theme)
            "C_TENIS" -> marker.icon = resources.getDrawable(R.drawable.pista_de_tenis, theme)
            "C_FUTA" -> marker.icon =
                resources.getDrawable(R.drawable.campo_de_futbol_americano, theme)
            "C_BAL" -> marker.icon = resources.getDrawable(R.drawable.pista_de_baloncesto, theme)
            "C_FUT" -> marker.icon = resources.getDrawable(R.drawable.campo_de_futbol, theme)
            "C_VOL" -> marker.icon = resources.getDrawable(R.drawable.red_de_voleibol, theme)
            "C_BEI" -> marker.icon = resources.getDrawable(R.drawable.campo_de_beisbol, theme)
            "B_Dir" -> marker.icon = resources.getDrawable(R.drawable.baseline_location_on_24, theme)
        }

        mapView.overlays.add(marker)
        markers.add(marker)
    }

    private fun buscarDireccion(direccion: String): GeoPoint? {
        val mGeocoder = Geocoder(baseContext)
        val addressString = direccion
        if (addressString.isNotEmpty()) {
            try {
                val addresses = mGeocoder.getFromLocationName(addressString, 2)
                if (!addresses.isNullOrEmpty()) {
                    val addressResult = addresses[0]
                    return GeoPoint(
                        addressResult.latitude, addressResult.longitude
                    )
                }
            } catch (e: IOException) {
                Log.e("Error", "Error en buscarDireccion: ${e.message}")
                e.printStackTrace()
            }
        }
        return null
    }

    private fun aplicarFiltroDeporte(deporte: String) {
        for (marker in markers) {
            val deporteMarcador = obtenerDeporteMarcador(marker)
            if (deporte == "Todos" || deporte == deporteMarcador) {
                if (!mapView.overlays.contains(marker)) {
                    mapView.overlays.add(marker)
                }
            } else {
                if (mapView.overlays.contains(marker)) {
                    mapView.overlays.remove(marker)
                }
            }
        }
        mapView.invalidate()
    }

    private fun obtenerDeporteMarcador(marker: Marker): String {
        val titulo = marker.title
        return marcadoresDeportes[titulo] ?: "Desconocido"
    }

    fun createLightSensorListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val lux = event.values[0]
                    if (lux < 10) {
                        mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                    } else {
                        mapView.overlayManager.tilesOverlay.setColorFilter(null)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                gravity = event.values.clone()
                hasGravity = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagnetic = event.values.clone()
                hasGeomagnetic = true
            }
        }

        if (hasGravity && hasGeomagnetic) {
            val rotationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                // Apply low-pass filter
                smoothedAzimuth = lowPassFilter(azimuth, smoothedAzimuth)

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime > updateInterval) {
                    lastUpdateTime = currentTime
                    // Update compass image rotation
                    compassImage.rotation = -smoothedAzimuth
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed
    }

    private fun lowPassFilter(input: Float, output: Float): Float {
        val alpha = 0.25f
        return output + alpha * (input - output)
    }

    private fun calculateRouteToMarker(destinationPoint: GeoPoint) {
        GetRouteTask().execute(destinationPoint)
    }

    private inner class GetRouteTask : AsyncTask<GeoPoint, Void, Road>() {
        override fun doInBackground(vararg params: GeoPoint): Road? {
            val routePoints = ArrayList<GeoPoint>()

            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                routePoints.add(
                    GeoPoint(
                        currentLocation.latitude, currentLocation.longitude
                    )
                )
            } else {
                Toast.makeText(
                    this@HomeActivity, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT
                ).show()
                return null
            }

            routePoints.add(params[0])

            return roadManager.getRoad(routePoints)
        }

        override fun onPostExecute(result: Road?) {
            super.onPostExecute(result)
            if (result != null) {
                drawRoadOverlay(result)
            } else {
                Toast.makeText(this@HomeActivity, "Error al obtener la ruta", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun drawRoadOverlay(road: Road) {
        roadOverlay?.let { mapView.overlays.remove(it) }
        roadOverlay = RoadManager.buildRoadOverlay(road)
        roadOverlay?.outlinePaint?.color = ContextCompat.getColor(this, R.color.red)
        roadOverlay?.outlinePaint?.strokeWidth = 10f
        mapView.overlays.add(roadOverlay)
    }

    private fun setUpMapViewListener() {
        mapView.setOnClickListener {
            val currentLocation = getCurrentLocation()

            if (currentLocation != null) {
                val mapController = mapView.controller
                mapController.setZoom(18.0)
                mapController.setCenter(
                    GeoPoint(
                        currentLocation.latitude, currentLocation.longitude
                    )
                )

                showMarker(
                    GeoPoint(currentLocation.latitude, currentLocation.longitude),
                    "Ubicacion actual",
                    "U_A"
                )
            } else {
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation(): Location? {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val LOCALIZACION = 1
    }
}
