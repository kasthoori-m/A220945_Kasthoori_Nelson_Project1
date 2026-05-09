package com.example.a220945_kasthoori_nelson_project1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Updated UserProfile with the dynamic "Level Up & Title" logic
data class UserProfile(
    val name: String = "",
    val matricNumber: String = "",
    val program: String = "",
    val totalXP: Int = 0,
    val currentStreak: Int = 0,
    val lastActiveDate: String = "",
    val lessonHighScores: Map<String, Int> = emptyMap()
) {
    // NEW: Automatically calculates the player's title based on current XP!
    val currentTitle: String
        get() = when (totalXP) {
            in 0..499 -> "Novice Coder"
            in 500..999 -> "Logic Apprentice"
            in 1000..2499 -> "System Architect"
            else -> "Software Grandmaster"
        }
}

// Defines the course structure, separating core courses from Citra electives
data class Course(
    val id: String,
    val title: String,
    val isCore: Boolean,
    val description: String,
    val progress: Float = 0f
)

// Data class for user-created tasks
data class CustomQuest(
    val id: Int,
    val title: String,
    val xpReward: Int
)

class EduQuestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfile())
    val uiState: StateFlow<UserProfile> = _uiState.asStateFlow()

    private val _selectedQuest = MutableStateFlow("")
    val selectedQuest: StateFlow<String> = _selectedQuest.asStateFlow()

    // ENRICHED CITRA COURSE DESCRIPTIONS!
    private val _allCourses = MutableStateFlow(
        listOf(
            Course("MAD", "Mobile Application Development", true, "Basic UI, Interaction, and Material Design.", 0f),
            Course("SD", "Software Design", true, "Design principles, techniques, methods, and interface design.", 0f),
            Course("SQM", "Software Quality & Management", true, "Project planning, quality culture, and software standards.", 0f),
            Course("UX", "User Experience Design", true, "UX principles, user needs, and digital interfaces (Topics 1-10).", 0f),
            Course("LMCR1252", "Pronunciation in English", false, "Master phonetics, intonation, and rhythm. This course involves intensive speaking drills and audio analysis to build professional confidence in spoken English.", 0f),
            Course("LMCR2482", "Basic Graphic Design", false, "Dive into color theory, typography, and layout composition. Students will use modern software to create digital posters, logos, and vector illustrations.", 0f),
            Course("LMCP1012", "Intro to Liberal Studies", false, "Analyze the intersection of history, philosophy, and sociology. Cultivate critical thinking skills to understand complex human behaviors and global cultural shifts.", 0f),
            Course("LMCR2322", "Health and Environment", false, "Investigate how pollution, climate change, and urbanization affect public health outcomes. Includes case studies on local Malaysian environmental challenges.", 0f),
            Course("LMCR1102", "Volunteerism & Social Service", false, "Learn the ethics of community engagement. Students will plan and execute a real-world social impact project targeting marginalized communities.", 0f),
            Course("LMCS1672", "Basic Table Tennis", false, "Develop hand-eye coordination, footwork, and professional serving techniques. Understand tournament rules and the psychology of competitive sports.", 0f)
        )
    )
    val allCourses: StateFlow<List<Course>> = _allCourses.asStateFlow()

    fun updateProfile(newName: String, newMatric: String, newProgram: String) {
        val currentProfile = _uiState.value
        _uiState.value = UserProfile(newName, newMatric, newProgram, currentProfile.totalXP, currentProfile.currentStreak, currentProfile.lastActiveDate, currentProfile.lessonHighScores)
    }

    fun selectQuest(questId: String) {
        _selectedQuest.value = questId
    }

    // SMART XP LOGIC: Calculates exactly how much new XP they deserve
    fun completeQuiz(courseId: String, lessonId: String, score: Int) {
        val currentProfile = _uiState.value

        // Find their previous best score (defaults to 0 if playing for the first time)
        val previousHighScore = currentProfile.lessonHighScores[lessonId] ?: 0

        // If they scored higher than before, they get the missing XP! (50 per question)
        val newXPEarned = if (score > previousHighScore) {
            (score - previousHighScore) * 50
        } else {
            0
        }

        // Update the high score map
        val updatedHighScores = currentProfile.lessonHighScores.toMutableMap()
        if (score > previousHighScore) {
            updatedHighScores[lessonId] = score
        }

        // Streak logic
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())
        val newStreak = if (currentProfile.lastActiveDate == todayDate) currentProfile.currentStreak else currentProfile.currentStreak + 1

        _uiState.value = currentProfile.copy(
            totalXP = currentProfile.totalXP + newXPEarned,
            currentStreak = newStreak,
            lastActiveDate = todayDate,
            lessonHighScores = updatedHighScores
        )

        // Update Course Progress based on total questions mastered across the 2 lessons
        // 6 total questions per course. Each newly mastered question gives 16.6% progress.
        val progressEarned = if (score > previousHighScore) ((score - previousHighScore).toFloat() / 6f) else 0f

        val updatedCourses = _allCourses.value.map { course ->
            if (course.id == courseId) {
                course.copy(progress = (course.progress + progressEarned).coerceAtMost(1f))
            } else course
        }
        _allCourses.value = updatedCourses
    }

    // Generic completion for reading tasks (Citra)
    fun completeReading(courseId: String) {
        val currentCourse = _allCourses.value.find { it.id == courseId }
        if (currentCourse != null && currentCourse.progress < 1f) {
            val currentProfile = _uiState.value
            _uiState.value = currentProfile.copy(totalXP = currentProfile.totalXP + 50)

            val updatedCourses = _allCourses.value.map { course ->
                if (course.id == courseId) course.copy(progress = 1f) else course
            }
            _allCourses.value = updatedCourses
        }
    }

    fun resetApp() {
        _uiState.value = UserProfile()
        _selectedQuest.value = ""
        val resetCourses = _allCourses.value.map { course -> course.copy(progress = 0f) }
        _allCourses.value = resetCourses
    }

    // --- CUSTOM QUESTS LOGIC REMAINS THE SAME ---
    private val _customQuests = MutableStateFlow<List<CustomQuest>>(emptyList())
    val customQuests: StateFlow<List<CustomQuest>> = _customQuests.asStateFlow()
    private var nextQuestId = 1

    fun addCustomQuest(title: String, xp: Int) {
        val newQuest = CustomQuest(nextQuestId++, title, xp)
        _customQuests.value = _customQuests.value + newQuest
    }

    fun completeCustomQuest(quest: CustomQuest) {
        val currentProfile = _uiState.value
        _uiState.value = currentProfile.copy(totalXP = currentProfile.totalXP + quest.xpReward)
        _customQuests.value = _customQuests.value.filter { it.id != quest.id }
    }
}