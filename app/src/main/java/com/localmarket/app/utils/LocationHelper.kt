package com.localmarket.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationHelper(private val locationManager: LocationManager) {
    
    // Get last known location from system location provider
    fun getLastLocation(): Location? {
        try {
            // Try to get from GPS provider first
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            
            // If not available, try network provider
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            
            return location
        } catch (e: SecurityException) {
            // Permission not granted
            return null
        } catch (e: Exception) {
            // Other errors
            return null
        }
    }
    
    // Get current location using FusedLocationProvider (more accurate)
    suspend fun getCurrentLocation(context: Context): Location? {
        return try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            
            val fusedLocationClient: FusedLocationProviderClient = 
                LocationServices.getFusedLocationProviderClient(context)
            
            val cancellationToken = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }
    
    // Calculate distance between two locations in kilometers
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Radius of the earth in km
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c // Distance in km
    }
    
    // Format distance for display
    fun formatDistance(distance: Double): String {
        return when {
            distance < 1.0 -> "${(distance * 1000).toInt()} m away"
            else -> String.format("%.1f km away", distance)
        }
    }
}