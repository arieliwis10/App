package com.example.sumativa1.services


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class DeviceFinderService(private val context: Context) {

    private var tts: TextToSpeech? = null

    fun initTts() {
        if (tts != null) return
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("es", "CL")
        }
    }

    fun speak(text: String) {
        initTts()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "finder-tts")
    }

    fun releaseTts() { tts?.shutdown(); tts = null }

    suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cont.resume(null); return@suspendCancellableCoroutine
        }
        val fused = LocationServices.getFusedLocationProviderClient(context)
        fused.lastLocation
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { cont.resume(null) }
    }

    fun reverseGeocode(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("es", "CL"))
            val list = geocoder.getFromLocation(lat, lng, 1)
            list?.firstOrNull()?.getAddressLine(0)
        } catch (_: Exception) { null }
    }

    fun formatLocation(loc: Location?): String =
        if (loc == null) "Ubicación no disponible"
        else "Tu ubicación aproximada es latitud ${"%.5f".format(loc.latitude)}, longitud ${"%.5f".format(loc.longitude)}"
}
