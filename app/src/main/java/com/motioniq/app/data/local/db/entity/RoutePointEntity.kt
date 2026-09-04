package com.motioniq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.motioniq.app.model.RoutePoint

@Entity(
    tableName = "route_points",
    foreignKeys = [
        ForeignKey(
            entity = ActivitySessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    val speedMps: Float?,
    val timestampMillis: Long
) {
    fun toDomain(): RoutePoint {
        return RoutePoint(
            latitude = latitude,
            longitude = longitude,
            altitudeMeters = altitudeMeters,
            speedMps = speedMps,
            timestampMillis = timestampMillis
        )
    }

    companion object {
        fun fromDomain(sessionId: String, point: RoutePoint): RoutePointEntity {
            return RoutePointEntity(
                sessionId = sessionId,
                latitude = point.latitude,
                longitude = point.longitude,
                altitudeMeters = point.altitudeMeters,
                speedMps = point.speedMps,
                timestampMillis = point.timestampMillis
            )
        }
    }
}
