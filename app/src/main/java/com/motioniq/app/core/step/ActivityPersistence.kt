package com.motioniq.app.core.step

import android.content.Context
import android.util.Log
import com.motioniq.app.model.*
import java.io.File
import java.util.Locale

/**
 * Persists completed workout sessions and route points to private storage.
 *
 * Implements:
 * - Thread-safe atomic file writes using temp file rename
 * - Zero-dependency, crash-resilient JSON parser that runs on Android and JVM unit tests
 * - Backward/forward compatibility with safe defaults for missing fields
 * - Automatic recovery against file corruption
 */
class ActivityPersistence(private val storageFile: File) {

    constructor(context: Context) : this(File(context.filesDir, DEFAULT_FILENAME))

    companion object {
        private const val TAG = "ActivityPersistence"
        const val DEFAULT_FILENAME = "motioniq_activities.json"
        private const val MAX_SAVED_ACTIVITIES = 200
    }

    private val lock = Any()

    /**
     * Saves a list of completed activities to storage atomically.
     */
    fun saveActivities(activities: List<MovementActivity>): Boolean {
        return synchronized(lock) {
            try {
                val clamped = activities.take(MAX_SAVED_ACTIVITIES)
                val json = serializeActivities(clamped)
                val tempFile = File(storageFile.parentFile, "${storageFile.name}.tmp")
                tempFile.parentFile?.mkdirs()
                tempFile.writeText(json, Charsets.UTF_8)
                if (tempFile.renameTo(storageFile)) {
                    true
                } else {
                    // Fallback on platforms where renameTo might fail if target exists
                    storageFile.delete()
                    val success = tempFile.renameTo(storageFile)
                    if (!success) {
                        storageFile.writeText(json, Charsets.UTF_8)
                        tempFile.delete()
                    }
                    true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist activities", e)
                false
            }
        }
    }

    /**
     * Loads all saved activities from storage. Returns empty list if no file or corrupted.
     */
    fun loadActivities(): List<MovementActivity> {
        return synchronized(lock) {
            try {
                if (!storageFile.exists() || storageFile.length() == 0L) {
                    return emptyList()
                }
                val content = storageFile.readText(Charsets.UTF_8)
                deserializeActivities(content)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load activities, returning empty list", e)
                emptyList()
            }
        }
    }

    /**
     * Adds a single newly completed activity to the top of the persistent list.
     */
    fun addActivity(activity: MovementActivity): Boolean {
        return synchronized(lock) {
            val current = loadActivities()
            val updated = listOf(activity) + current
            saveActivities(updated)
        }
    }

    /**
     * Clears all stored activities.
     */
    fun clearActivities(): Boolean {
        return synchronized(lock) {
            try {
                if (storageFile.exists()) {
                    storageFile.delete()
                } else {
                    true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear activities", e)
                false
            }
        }
    }

    // ── Serialization ──

    fun serializeActivities(activities: List<MovementActivity>): String {
        val sb = StringBuilder()
        sb.append("[\n")
        activities.forEachIndexed { index, activity ->
            sb.append(serializeActivity(activity))
            if (index < activities.size - 1) {
                sb.append(",\n")
            } else {
                sb.append("\n")
            }
        }
        sb.append("]")
        return sb.toString()
    }

    fun serializeActivity(activity: MovementActivity): String {
        val sb = StringBuilder()
        sb.append("  {\n")
        sb.append("    \"id\": \"${escapeJson(activity.id)}\",\n")
        sb.append("    \"type\": \"${activity.type.name}\",\n")
        sb.append("    \"startTimeMillis\": ${activity.startTimeMillis},\n")
        sb.append("    \"endTimeMillis\": ${activity.endTimeMillis},\n")
        sb.append("    \"durationSeconds\": ${activity.durationSeconds},\n")
        sb.append("    \"steps\": ${activity.steps},\n")
        sb.append("    \"distanceMeters\": ${String.format(Locale.US, "%.2f", activity.distanceMeters)},\n")
        sb.append("    \"caloriesKcal\": ${activity.caloriesKcal},\n")
        sb.append("    \"avgSpeedKmh\": ${String.format(Locale.US, "%.2f", activity.avgSpeedKmh)},\n")
        sb.append("    \"avgPaceMinPerKm\": ${String.format(Locale.US, "%.2f", activity.avgPaceMinPerKm)},\n")
        sb.append("    \"startPlaceName\": \"${escapeJson(activity.startPlaceName)}\",\n")
        sb.append("    \"endPlaceName\": \"${escapeJson(activity.endPlaceName)}\",\n")
        sb.append("    \"confidenceLevel\": \"${activity.confidenceLevel.name}\",\n")
        sb.append("    \"stepSource\": \"${activity.stepSource.name}\",\n")
        sb.append("    \"routePoints\": [")

        activity.routePoints.forEachIndexed { i, pt ->
            sb.append("{\"lat\":${String.format(Locale.US, "%.6f", pt.latitude)},\"lng\":${String.format(Locale.US, "%.6f", pt.longitude)}")
            if (pt.altitudeMeters != null) sb.append(",\"alt\":${String.format(Locale.US, "%.1f", pt.altitudeMeters)}")
            if (pt.speedMps != null) sb.append(",\"spd\":${String.format(Locale.US, "%.2f", pt.speedMps)}")
            sb.append(",\"time\":${pt.timestampMillis}}")
            if (i < activity.routePoints.size - 1) sb.append(",")
        }
        sb.append("]\n")
        sb.append("  }")
        return sb.toString()
    }

    // ── Deserialization ──

    fun deserializeActivities(json: String): List<MovementActivity> {
        val root = MiniJsonParser(json).parse() as? MiniJsonValue.JArray ?: return emptyList()
        val result = mutableListOf<MovementActivity>()
        for (item in root.items) {
            val obj = item as? MiniJsonValue.JObject ?: continue
            val activity = parseActivityObject(obj)
            if (activity != null) {
                result.add(activity)
            }
        }
        return result
    }

    fun deserializeActivity(json: String): MovementActivity? {
        val root = MiniJsonParser(json).parse() as? MiniJsonValue.JObject ?: return null
        return parseActivityObject(root)
    }

    private fun parseActivityObject(obj: MiniJsonValue.JObject): MovementActivity? {
        return try {
            val id = obj.getString("id", java.util.UUID.randomUUID().toString())
            val typeStr = obj.getString("type", ActivityType.WALKING.name)
            val type = try { ActivityType.valueOf(typeStr) } catch (_: Exception) { ActivityType.WALKING }

            val startTimeMillis = obj.getLong("startTimeMillis", 0L)
            val endTimeMillis = obj.getLong("endTimeMillis", 0L)
            val durationSeconds = obj.getLong("durationSeconds", 0L)
            val steps = obj.getLong("steps", 0L)
            val distanceMeters = obj.getDouble("distanceMeters", 0.0)
            val caloriesKcal = obj.getInt("caloriesKcal", 0)
            val avgSpeedKmh = obj.getDouble("avgSpeedKmh", 0.0)
            val avgPaceMinPerKm = obj.getDouble("avgPaceMinPerKm", 0.0)
            val startPlaceName = obj.getString("startPlaceName", "Start Point")
            val endPlaceName = obj.getString("endPlaceName", "End Point")

            val confStr = obj.getString("confidenceLevel", ConfidenceLevel.HIGH.name)
            val confidenceLevel = try { ConfidenceLevel.valueOf(confStr) } catch (_: Exception) { ConfidenceLevel.HIGH }

            val sourceStr = obj.getString("stepSource", StepSource.HARDWARE_SENSOR.name)
            val stepSource = try { StepSource.valueOf(sourceStr) } catch (_: Exception) { StepSource.HARDWARE_SENSOR }

            val pointsArray = obj.getArray("routePoints")
            val routePoints = mutableListOf<RoutePoint>()
            for (p in pointsArray) {
                val pObj = p as? MiniJsonValue.JObject ?: continue
                val lat = pObj.getDouble("lat", 0.0)
                val lng = pObj.getDouble("lng", 0.0)
                val alt = pObj.getDoubleOrNull("alt")
                val spd = pObj.getDoubleOrNull("spd")?.toFloat()
                val time = pObj.getLong("time", System.currentTimeMillis())
                routePoints.add(RoutePoint(lat, lng, alt, spd, time))
            }

            MovementActivity(
                id = id,
                type = type,
                startTimeMillis = startTimeMillis,
                endTimeMillis = endTimeMillis,
                durationSeconds = durationSeconds,
                steps = steps,
                distanceMeters = distanceMeters,
                caloriesKcal = caloriesKcal,
                avgSpeedKmh = avgSpeedKmh,
                avgPaceMinPerKm = avgPaceMinPerKm,
                startPlaceName = startPlaceName,
                endPlaceName = endPlaceName,
                confidenceLevel = confidenceLevel,
                stepSource = stepSource,
                routePoints = routePoints
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun escapeJson(s: String): String = buildString {
        for (c in s) {
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }
    }
}

// ── Lightweight Self-Contained JSON Parser ──

sealed class MiniJsonValue {
    data class JObject(val map: Map<String, MiniJsonValue>) : MiniJsonValue() {
        fun getString(key: String, default: String = ""): String =
            (map[key] as? JString)?.value ?: (map[key] as? JNumber)?.value ?: default

        fun getLong(key: String, default: Long = 0L): Long =
            (map[key] as? JNumber)?.value?.toLongOrNull() ?: default

        fun getInt(key: String, default: Int = 0): Int =
            (map[key] as? JNumber)?.value?.toIntOrNull() ?: default

        fun getDouble(key: String, default: Double = 0.0): Double =
            (map[key] as? JNumber)?.value?.toDoubleOrNull() ?: default

        fun getDoubleOrNull(key: String): Double? =
            (map[key] as? JNumber)?.value?.toDoubleOrNull()

        fun getArray(key: String): List<MiniJsonValue> =
            (map[key] as? JArray)?.items ?: emptyList()
    }

    data class JArray(val items: List<MiniJsonValue>) : MiniJsonValue()
    data class JString(val value: String) : MiniJsonValue()
    data class JNumber(val value: String) : MiniJsonValue()
    data class JBool(val value: Boolean) : MiniJsonValue()
    object JNull : MiniJsonValue()
}

class MiniJsonParser(private val src: String) {
    private var idx = 0
    private val len = src.length

    fun parse(): MiniJsonValue? {
        skipWhitespace()
        if (idx >= len) return null
        return parseValue()
    }

    private fun parseValue(): MiniJsonValue? {
        skipWhitespace()
        if (idx >= len) return null
        return when (src[idx]) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> parseString()
            't', 'f' -> parseBool()
            'n' -> parseNull()
            else -> if (src[idx] == '-' || src[idx].isDigit()) parseNumber() else null
        }
    }

    private fun parseObject(): MiniJsonValue.JObject {
        idx++ // skip '{'
        val map = mutableMapOf<String, MiniJsonValue>()
        while (idx < len) {
            skipWhitespace()
            if (idx < len && src[idx] == '}') {
                idx++
                break
            }
            val key = (parseValue() as? MiniJsonValue.JString)?.value ?: break
            skipWhitespace()
            if (idx < len && src[idx] == ':') {
                idx++
            }
            val value = parseValue() ?: MiniJsonValue.JNull
            map[key] = value
            skipWhitespace()
            if (idx < len && src[idx] == ',') {
                idx++
            }
        }
        return MiniJsonValue.JObject(map)
    }

    private fun parseArray(): MiniJsonValue.JArray {
        idx++ // skip '['
        val list = mutableListOf<MiniJsonValue>()
        while (idx < len) {
            skipWhitespace()
            if (idx < len && src[idx] == ']') {
                idx++
                break
            }
            val value = parseValue()
            if (value != null) {
                list.add(value)
            }
            skipWhitespace()
            if (idx < len && src[idx] == ',') {
                idx++
            }
        }
        return MiniJsonValue.JArray(list)
    }

    private fun parseString(): MiniJsonValue.JString {
        idx++ // skip '"'
        val sb = StringBuilder()
        while (idx < len) {
            val c = src[idx++]
            if (c == '"') break
            if (c == '\\' && idx < len) {
                val next = src[idx++]
                when (next) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    'b' -> sb.append('\b')
                    else -> sb.append(next)
                }
            } else {
                sb.append(c)
            }
        }
        return MiniJsonValue.JString(sb.toString())
    }

    private fun parseNumber(): MiniJsonValue.JNumber {
        val start = idx
        if (idx < len && src[idx] == '-') idx++
        while (idx < len && (src[idx].isDigit() || src[idx] == '.' || src[idx] == 'e' || src[idx] == 'E' || src[idx] == '+')) {
            idx++
        }
        return MiniJsonValue.JNumber(src.substring(start, idx))
    }

    private fun parseBool(): MiniJsonValue.JBool {
        return if (src.startsWith("true", idx)) {
            idx += 4
            MiniJsonValue.JBool(true)
        } else if (src.startsWith("false", idx)) {
            idx += 5
            MiniJsonValue.JBool(false)
        } else {
            idx++
            MiniJsonValue.JBool(false)
        }
    }

    private fun parseNull(): MiniJsonValue {
        if (src.startsWith("null", idx)) {
            idx += 4
        } else {
            idx++
        }
        return MiniJsonValue.JNull
    }

    private fun skipWhitespace() {
        while (idx < len && src[idx].isWhitespace()) {
            idx++
        }
    }
}
