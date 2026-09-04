package com.motioniq.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.motioniq.app.model.ParkPlace

@Composable
fun ExploreMapComposable(
    parks: List<ParkPlace>,
    modifier: Modifier = Modifier
) {
    if (parks.isEmpty()) {
        Box(modifier = modifier)
        return
    }

    val firstPark = parks.first()
    val center = LatLng(firstPark.latitude, firstPark.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 13f)
    }

    LaunchedEffect(parks) {
        if (parks.size >= 2) {
            val builder = LatLngBounds.builder()
            parks.forEach {
                builder.include(LatLng(it.latitude, it.longitude))
            }
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 70)
                )
            } catch (_: Exception) {
                // Layout not ready
            }
        }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(20.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            ),
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL
            )
        ) {
            parks.forEach { park ->
                Marker(
                    state = MarkerState(position = LatLng(park.latitude, park.longitude)),
                    title = park.name,
                    snippet = "${park.distanceKm} km • ${park.etaMinutes} min"
                )
            }
        }
    }
}
