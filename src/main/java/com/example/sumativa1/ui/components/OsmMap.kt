package com.example.sumativa1.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMap(
    lat: Double = -33.4489,        // fallback: Santiago
    lng: Double = -70.6693,
    zoom: Double = 15.0,
    showMarker: Boolean = true,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                // Controles multitouch y posición inicial
                setMultiTouchControls(true)
                controller.setZoom(zoom)
                controller.setCenter(GeoPoint(lat, lng))

                if (showMarker) {
                    val marker = Marker(this).apply {
                        position = GeoPoint(lat, lng)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Ubicación"
                    }
                    overlays.add(marker)
                }
            }
        },
        update = { map ->
            // Si cambian lat/lng al recomponer, centra la cámara de nuevo
            map.controller.setCenter(GeoPoint(lat, lng))
        }
    )
}
