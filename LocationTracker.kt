package com.example.engine

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

data class LatLngPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val speedKmh: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class TrackerLocationState(
    val lastLocation: LatLngPoint? = null,
    val path: List<LatLngPoint> = emptyList(),
    val totalDistanceMeters: Double = 0.0,
    val currentSpeedKmh: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val isGpsLocked: Boolean = false,
    val isSimulating: Boolean = false
)

class LocationTracker(private val context: Context) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _locationState = MutableStateFlow(TrackerLocationState())
    val locationState: StateFlow<TrackerLocationState> = _locationState.asStateFlow()

    private var isTracking = false
    private var locationCallback: LocationCallback? = null
    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Simulation params: simulated 400m oval track
    private var simAngle = 0.0
    private val simCenterLat = 22.5726
    private val simCenterLon = 88.3639

    @SuppressLint("MissingPermission")
    fun startRealTracking(onFirstLocation: ((LatLngPoint) -> Unit)? = null) {
        stopSimulation()
        isTracking = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1500)
            .setMinUpdateIntervalMillis(1000)
            .setMinUpdateDistanceMeters(1.0f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (!isTracking) return
                val location = result.lastLocation ?: return
                processNewLocation(location)
            }
        }

        try {
            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null && _locationState.value.lastLocation == null) {
                    processNewLocation(loc)
                    onFirstLocation?.invoke(
                        LatLngPoint(
                            loc.latitude,
                            loc.longitude,
                            loc.altitude,
                            loc.speed * 3.6,
                            loc.time
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted, can fallback to simulation
        }
    }

    fun startSimulationTracking(speedKmh: Double = 12.0) {
        stopRealTracking()
        isTracking = true
        _locationState.value = _locationState.value.copy(isSimulating = true, isGpsLocked = true)

        simulationJob?.cancel()
        simulationJob = scope.launch {
            val metersPerSecond = speedKmh / 3.6
            // 400m track radius approx 45m x 85m oval
            val earthRadius = 6378137.0
            while (isTracking) {
                delay(1000)
                // angular speed for 400m circumference
                val angularSpeed = (metersPerSecond / 400.0) * (2 * Math.PI)
                simAngle = (simAngle + angularSpeed) % (2 * Math.PI)

                // Oval shape simulation
                val radiusX = 55.0 * (1.0 + 0.3 * cos(simAngle * 2))
                val radiusY = 35.0

                val dNorth = radiusY * sin(simAngle)
                val dEast = radiusX * cos(simAngle)

                val lat = simCenterLat + (dNorth / earthRadius) * (180.0 / Math.PI)
                val lon = simCenterLon + (dEast / (earthRadius * cos(simCenterLat * Math.PI / 180.0))) * (180.0 / Math.PI)

                val simulatedLoc = Location("simulation").apply {
                    latitude = lat
                    longitude = lon
                    speed = (metersPerSecond + (Math.random() - 0.5) * 0.4).toFloat().coerceAtLeast(0f)
                    accuracy = 2.5f
                    time = System.currentTimeMillis()
                }

                processNewLocation(simulatedLoc)
            }
        }
    }

    private fun processNewLocation(location: Location) {
        val currentPt = LatLngPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            speedKmh = (location.speed * 3.6).coerceAtLeast(0.0),
            timestamp = location.time
        )

        val currentState = _locationState.value
        val lastPt = currentState.lastLocation

        var distDelta = 0.0
        if (lastPt != null) {
            distDelta = calculateDistance(
                lastPt.latitude,
                lastPt.longitude,
                currentPt.latitude,
                currentPt.longitude
            )
        }

        // Filter out GPS noise / teleport jumps (e.g. > 50m in 1 sec is invalid for running)
        val validDelta = if (distDelta in 0.8..50.0) distDelta else if (lastPt == null) 0.0 else 0.0

        val newTotalDist = currentState.totalDistanceMeters + validDelta
        val updatedPath = if (distDelta > 0.5 || lastPt == null) {
            currentState.path + currentPt
        } else {
            currentState.path
        }

        _locationState.value = currentState.copy(
            lastLocation = currentPt,
            path = updatedPath,
            totalDistanceMeters = newTotalDist,
            currentSpeedKmh = currentPt.speedKmh,
            accuracyMeters = location.accuracy,
            isGpsLocked = true
        )
    }

    fun stopTracking() {
        isTracking = false
        stopRealTracking()
        stopSimulation()
    }

    fun resetDistanceAndPath() {
        _locationState.value = _locationState.value.copy(
            path = emptyList(),
            totalDistanceMeters = 0.0,
            currentSpeedKmh = 0.0
        )
        simAngle = 0.0
    }

    private fun stopRealTracking() {
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }
}
