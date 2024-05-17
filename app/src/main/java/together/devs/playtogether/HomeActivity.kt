package together.devs.playtogether

import android.Manifest
import android.app.UiModeManager
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
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration // Agrega esta importación
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
import android.widget.TextView
import org.osmdroid.views.overlay.TilesOverlay

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
    private var isCompassClicked = false

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

        // Agrega más entradas según tus marcadores
    )

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



        if (ContextCompat.checkSelfPermission(
                this, fineLocationPermission
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, coarseLocationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Mostrar explicación si es necesario (esto es opcional)
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, fineLocationPermission
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, coarseLocationPermission
                )
            ) {
                // Puedes mostrar una explicación aquí si lo deseas.
            }

            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(
                this, arrayOf(fineLocationPermission, coarseLocationPermission), LOCALIZACION
            )
        } else {
            mapView = findViewById(R.id.mapView)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            setUpMapViewListener()
            // Después de inicializar la variable Direccion
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
                    // No se necesita implementación aquí
                }
            }


            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude, currentLocation.longitude
                    )
                )
            }
            Direccion.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    val dir = Direccion.text.toString()
                    val geoPoint = buscarDireccion(dir)
                    if (dir.isNotEmpty()) {
                        if (geoPoint != null) {

                            showMarker(geoPoint, "Ubicacion encontrada", "B_Dir")
                            val mapController = mapView.controller
                            mapController.setZoom(18.0)  // Puedes ajustar el nivel de zoom
                            mapController.setCenter(geoPoint)
                        } else {
                            Toast.makeText(
                                this, "La direccion no pudo ser encontrada", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return@setOnEditorActionListener true // Agregar esta línea
                }
                false // No se maneja otro tipo de evento de acción
            }


        }

        val but2: Button = findViewById(R.id.verEquiposButton)
        val but3: Button = findViewById(R.id.verEventosButton)
        val but4: Button = findViewById(R.id.verPerfilButton)


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
            } else {
                Toast.makeText(
                    this, "ALGUNO DE LOS PERMISOS DE LOCALIZACIÓN NO FUE DADO", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        // Reanudar el mapa de OSMDroid
        mapView.onResume()

        sensorManager.registerListener(
            lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)


        // Obtener la ubicación actual
        val currentLocation = getCurrentLocation()

        // Verificar si se pudo obtener la ubicación actual
        if (currentLocation != null) {
            // Obtener el controlador del mapa
            val mapController = mapView.controller

            // Configurar el zoom y el centro del mapa en la ubicación actual
            mapController.setZoom(18.0)
            mapController.setCenter(
                org.osmdroid.util.GeoPoint(
                    currentLocation.latitude, currentLocation.longitude
                )
            )

            // Establecer marcadores en el mapa
            showMarker(
                org.osmdroid.util.GeoPoint(currentLocation.latitude, currentLocation.longitude),
                "Ubicacion actual",
                "U_A"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.647327414834436, -74.07575100117528),
                "Cancha Tenis",
                "C_TENIS"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.7112444237342395, -74.07177802349177),
                "Cancha Fútbol",
                "C_FUT"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.704653322769599, -74.12105912354257),
                "Cancha Americano",
                "C_FUTA"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.734634729222503, -74.0615729005996),
                "Cancha Voleibol",
                "C_VOL"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.713044447775575, -74.14344360073954),
                "Cancha Baloncesto",
                "C_BAL"
            )
            showMarker(
                org.osmdroid.util.GeoPoint(4.664892128032969, -74.09746650107071),
                "Cancha Beisbol",
                "C_BEI"
            )


        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
        }

        val uims = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uims.nightMode == UiModeManager.MODE_NIGHT_YES) {
            mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        }
        sensorManager.registerListener(
            lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun showMarker(geoPoint: org.osmdroid.util.GeoPoint, markerName: String, tipo: String) {
        // Crea y muestra un nuevo marcador en la ubicación proporcionada
        val marker = Marker(mapView)
        marker.title = markerName
        marker.position = geoPoint


        marker.setOnMarkerClickListener { _, _ ->
            calculateRouteToMarker(geoPoint)
            true
        }




        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Icono personalizado según el deporte
        when (tipo) {
            "U_A" -> marker.icon = resources.getDrawable(R.drawable.baseline_emoji_people_24, theme)
            "C_TENIS" -> marker.icon = resources.getDrawable(R.drawable.pista_de_tenis, theme)
            "C_FUTA" -> marker.icon =
                resources.getDrawable(R.drawable.campo_de_futbol_americano, theme)

            "C_BAL" -> marker.icon = resources.getDrawable(R.drawable.pista_de_baloncesto, theme)
            "C_FUT" -> marker.icon = resources.getDrawable(R.drawable.campo_de_futbol, theme)
            "C_VOL" -> marker.icon = resources.getDrawable(R.drawable.red_de_voleibol, theme)
            "C_BEI" -> marker.icon = resources.getDrawable(R.drawable.campo_de_beisbol, theme)
            "B_Dir" -> marker.icon =
                resources.getDrawable(R.drawable.baseline_location_on_24, theme)


        }

        mapView.overlays.add(marker)
        markers.add(marker)
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

    private fun calculateRouteToMarker(destinationPoint: org.osmdroid.util.GeoPoint) {
        // Inicia la tarea asíncrona para calcular la ruta
        GetRouteTask().execute(destinationPoint)
    }

    private inner class GetRouteTask : AsyncTask<org.osmdroid.util.GeoPoint, Void, Road>() {
        override fun doInBackground(vararg params: org.osmdroid.util.GeoPoint): Road? {
            val routePoints = ArrayList<org.osmdroid.util.GeoPoint>()

            // Obtener la ubicación actual del usuario
            val currentLocation = getCurrentLocation()
            if (currentLocation != null) {
                routePoints.add(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude, currentLocation.longitude
                    )
                )
            } else {
                // Mostrar un mensaje de error si no se pudo obtener la ubicación actual
                Toast.makeText(
                    this@HomeActivity, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT
                ).show()
                return null
            }

            // Agregar el destino a los puntos de la ruta
            routePoints.add(params[0]) // Destino

            return roadManager.getRoad(routePoints)
        }

        override fun onPostExecute(result: Road?) {
            super.onPostExecute(result)
            if (result != null) {
                // Dibujar la ruta
                drawRoadOverlay(result)
            } else {
                // Mostrar un mensaje de error en caso de que no se pueda obtener la ruta
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
            // Ubicación actual del usuario
            val currentLocation = getCurrentLocation()

            // Verifica si se pudo obtener la ubicación actual
            if (currentLocation != null) {
                // Obtener el controlador del mapa
                val mapController = mapView.controller

                // Configurar el zoom y el centro del mapa en la ubicación actual
                mapController.setZoom(18.0)
                mapController.setCenter(
                    org.osmdroid.util.GeoPoint(
                        currentLocation.latitude, currentLocation.longitude
                    )
                )

                // Establecer marcadores en el mapa
                showMarker(
                    org.osmdroid.util.GeoPoint(currentLocation.latitude, currentLocation.longitude),
                    "Ubicacion actual",
                    "U_A"
                )

                // ... (otros marcadores)

            } else {
                Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarDireccion(direccion: String): org.osmdroid.util.GeoPoint? {
        val mGeocoder = Geocoder(baseContext)
        val addressString = direccion
        if (addressString.isNotEmpty()) {
            try {
                val addresses = mGeocoder.getFromLocationName(addressString, 2)
                if (!addresses.isNullOrEmpty()) {
                    val addressResult = addresses[0]
                    return org.osmdroid.util.GeoPoint(
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
                // Si el deporte coincide o se selecciona "Todos", muestra el marcador
                if (!mapView.overlays.contains(marker)) {
                    mapView.overlays.add(marker)
                }
            } else {
                // Si el deporte no coincide, oculta el marcador
                if (mapView.overlays.contains(marker)) {
                    mapView.overlays.remove(marker)
                }
            }
        }
        mapView.invalidate() // Solicita la actualización del mapa
    }


    private fun obtenerDeporteMarcador(marker: Marker): String {
        // Obtén el título del marcador
        val titulo = marker.title

        // Busca el deporte correspondiente en el mapa de marcadoresDeportes
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


    override fun onPause() {
        super.onPause()
        mapView.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
        sensorManager.unregisterListener(lightEventListener)
    }

    companion object {
        const val LOCALIZACION = 1

    }



}