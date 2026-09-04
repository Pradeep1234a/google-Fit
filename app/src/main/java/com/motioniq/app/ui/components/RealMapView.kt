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
import com.motioniq.app.model.RoutePoint
import com.motioniq.app.theme.StitchCyan

@Composable
fun RealMapView(
    routePoints: List<RoutePoint>,
    modifier: Modifier = Modifier,
    isLiveTracking: Boolean = false
) {
    if (routePoints.isEmpty()) {
        RouteMapCanvas(routePoints = routePoints, modifier = modifier, isLiveTracking = isLiveTracking)
        return
    }

    val latLngList = remember(routePoints) {
        routePoints.map { LatLng(it.latitude, it.longitude) }
    }

    val initialPosition = remember(latLngList) {
        latLngList.lastOrNull() ?: LatLng(0.0, 0.0)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 16f)
    }

    LaunchedEffect(latLngList.size) {
        if (latLngList.size >= 2) {
            val builder = LatLngBounds.builder()
            latLngList.forEach { builder.include(it) }
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 60)
                )
            } catch (_: Exception) {
                // Ignore if view layout is not ready
            }
        } else if (latLngList.isNotEmpty()) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLngList.last(), 16f)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
    ) {
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
            if (latLngList.size >= 2) {
                Polyline(
                    points = latLngList,
                    color = StitchCyan,
                    width = 10f
                )
            }

            if (latLngList.isNotEmpty()) {
                Marker(
                    state = MarkerState(position = latLngList.first()),
                    title = "Origin"
                )
                if (latLngList.size > 1) {
                    Marker(
                        state = MarkerState(position = latLngList.last()),
                        title = if (isLiveTracking) "Current Fix" else "Finish"
                    )
                }
            }
        }
    }
}
