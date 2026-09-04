package com.motioniq.app.data.repository

import com.motioniq.app.data.local.db.dao.GoalDao
import com.motioniq.app.data.local.db.entity.GoalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val goals: Flow<List<GoalEntity>> = goalDao.getAllGoals().onStart {
        initDefaultGoalsIfEmpty()
    }

    private fun initDefaultGoalsIfEmpty() {
        scope.launch {
            val existing = goalDao.getAllGoals().first()
            if (existing.isEmpty()) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val defaults = listOf(
                    GoalEntity("goal_steps", "Kinetic Steps", "STEPS", 10000.0, 0.0, "steps", "DAILY", false, today),
                    GoalEntity("goal_distance", "Spatial Range", "DISTANCE", 5.0, 0.0, "km", "DAILY", false, today),
                    GoalEntity("goal_active", "Active Cadence", "ACTIVE_MINUTES", 60.0, 0.0, "min", "DAILY", false, today),
                    GoalEntity("goal_weekly", "Session Volume", "SESSIONS", 5.0, 0.0, "sessions", "WEEKLY", false, today)
                )
                goalDao.upsertGoals(defaults)
            }
        }
    }

    suspend fun updateGoalProgress(id: String, currentValue: Double) {
        val goal = goalDao.getGoalById(id) ?: return
        val isDone = currentValue >= goal.targetValue
        goalDao.upsertGoal(goal.copy(currentValue = currentValue, isCompleted = isDone))
    }

    suspend fun upsertGoal(goal: GoalEntity) {
        goalDao.upsertGoal(goal)
    }

    suspend fun clearAll() {
        goalDao.deleteAllGoals()
    }
}
