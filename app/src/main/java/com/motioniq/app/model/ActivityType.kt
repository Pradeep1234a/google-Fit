package com.motioniq.app.model

enum class ActivityType(
    val displayName: String,
    val emoji: String,
    val defaultMet: Double,
    val isOutdoorGps: Boolean
) {
    WALKING("Walking", "🚶", 3.8, true),
    RUNNING("Running", "🏃", 9.8, true),
    CYCLING("Cycling", "🚴", 7.5, true),
    SPORTS("Sports", "🏀", 7.0, false),
    JUMP("Jump", "🦘", 8.0, false),
    SWIMMING("Swimming", "🏊", 6.0, false);

    companion object {
        fun fromString(value: String): ActivityType =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: WALKING
    }
}
