package com.example.a220945_kasthoori_nelson_project1

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.shape.CircleShape
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.style.TextOverflow
import com.example.a220945_kasthoori_nelson_project1.ui.theme.EduQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduQuestTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    EduQuestApp()
                }
            }
        }
    }
}

// --- APP NAVIGATION ENGINE ---
// This composable handles our 5-screen flow using Jetpack Compose Navigation
@Composable
fun EduQuestApp(viewModel: EduQuestViewModel = viewModel()) {
    // rememberNavController keeps track of the back stack (which screens we've visited)
    val navController = rememberNavController()

    // NavHost maps route strings (like "Dashboard") to the actual Composable functions
    NavHost(navController = navController, startDestination = "ProfileSetup") {

        composable("ProfileSetup") {
            ProfileSetupScreen(viewModel = viewModel, navController = navController)
        }
        composable("Dashboard") {
            DashboardScreen(viewModel = viewModel, navController = navController)
        }
        composable("QuestDetails") {
            QuestDetailsScreen(viewModel = viewModel, navController = navController)
        }
        // Project 1: User-created task screens
        composable("ActiveQuests") {
            ActiveQuestsScreen(viewModel = viewModel, navController = navController)
        }
        composable("CreateQuest") {
            CreateQuestScreen(viewModel = viewModel, navController = navController)
        }
    }
}

// --- SCREEN 1: PROFILE SETUP ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(viewModel: EduQuestViewModel, navController: NavController) {
    // remember { mutableStateOf() } holds temporary UI state just for this screen
    var nameInput by remember { mutableStateOf("") }
    var matricInput by remember { mutableStateOf("") }

    val programs = listOf("Software Engineering", "Computer Science", "Information Technology")
    var expanded by remember { mutableStateOf(false) }
    var selectedProgram by remember { mutableStateOf("Choose your program") }
    var errorMessage by remember { mutableStateOf("") }

    // Column arranges UI elements vertically
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to EduQuest", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        // Card provides Material Design elevation and rounded corners
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Set Up Your Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        errorMessage = ""
                    },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = matricInput,
                    onValueChange = {
                        matricInput = it
                        errorMessage = ""
                    },
                    label = { Text("Matric Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Material 3 Dropdown Menu for selecting programs
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProgram,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Program") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        programs.forEach { selectionOption ->
                            val isSelected = selectedProgram == selectionOption

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = selectionOption,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedProgram = selectionOption
                                    expanded = false
                                    errorMessage = ""
                                },
                                modifier = Modifier.background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Conditional rendering: Only shows if there is an error
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Basic Form Validation logic
                        if (nameInput.isBlank() || matricInput.isBlank() || selectedProgram == "Choose your program") {
                            errorMessage = "Please fill in all fields to continue."
                        } else {
                            errorMessage = ""
                            // Send data to ViewModel and navigate
                            viewModel.updateProfile(nameInput, matricInput, selectedProgram)
                            navController.navigate("Dashboard")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Enter Dashboard")
                }
            }
        }
    }
}

// --- SCREEN 2: MAIN DASHBOARD ---
@Composable
fun DashboardScreen(viewModel: EduQuestViewModel, navController: NavController) {
    // collectAsState() forces this screen to redraw (recompose) whenever the ViewModel data changes
    val uiState by viewModel.uiState.collectAsState()
    val allCourses by viewModel.allCourses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

    // Dynamic filtering using standard Kotlin list functions
    val displayedCourses = allCourses.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
    }

    val coreCourses = displayedCourses.filter { it.isCore }
    val citraCourses = displayedCourses.filter { !it.isCore }

    // Alert Dialog for safe app reset logic
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Profile", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out? This will erase your current profile, XP, streak, and all course progress.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetApp()
                        navController.navigate("ProfileSetup") {
                            popUpTo(0) // Clears navigation history so user can't press 'Back' to return to dashboard
                        }
                    }
                ) {
                    Text("Reset App", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // verticalScroll makes the entire dashboard scrollable
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search courses...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        // Profile Header Card (Box allows overlapping layout)
        Box(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)).padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Welcome back,", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text(text = uiState.name.ifEmpty { "Student" }, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)

                    // Displays the computed title from the ViewModel
                    Text(text = "🏆 ${uiState.currentTitle}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFCD34D))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${uiState.matricNumber} | ${uiState.program}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }

                // Profile Avatar Icon (Clicking opens reset dialog)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(24.dp))
                        .clickable { showResetDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = "Profile Settings", modifier = Modifier.size(24.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigates to Screen 4 (ActiveQuests)
        Button(
            onClick = { navController.navigate("ActiveQuests") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("View My Custom Quests", fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Gamification Stats Cards (Row puts them side-by-side)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = android.R.drawable.ic_menu_today), contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Streak", fontSize = 10.sp, color = Color.Gray)
                        Text("${uiState.currentStreak} Days", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(id = android.R.drawable.star_on), contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Total XP", fontSize = 10.sp, color = Color.Gray)
                        Text("${uiState.totalXP} XP", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // UI Logic: Only show core courses if program matches
        if (uiState.program == "Software Engineering" && coreCourses.isNotEmpty()) {
            Text(text = "Current Semester Courses", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            coreCourses.forEach { course ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable {
                        // Tells ViewModel which course was clicked before navigating
                        viewModel.selectQuest(course.id)
                        navController.navigate("QuestDetails")
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = course.title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Progress Bar: Uses Box overlapping to show fill percentage
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp))) {
                            Box(modifier = Modifier.fillMaxWidth(course.progress).fillMaxHeight().background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${(course.progress * 100).toInt()}% Mastered", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Citra Courses dynamically loaded into a 2-column grid
        if (citraCourses.isNotEmpty()) {
            Text(text = "Explore Citra Quests", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            // .chunked(2) splits the list into rows of 2 items
            val rows = citraCourses.chunked(2)
            rows.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                    rowItems.forEach { course ->
                        AnimatedSubjectCard(
                            title = course.id,
                            subtitle = course.title,
                            details = course.description,
                            bg = MaterialTheme.colorScheme.primary,
                            txt = Color.White,
                            modifier = Modifier.weight(1f) // Ensures equal width in row
                        ) {
                            viewModel.selectQuest(course.id)
                            navController.navigate("QuestDetails")
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f)) // Balances grid if odd number of items
                    }
                }
            }
        }
    }
}

// Data structure for the local quizzes
data class QuizQuestion(val text: String, val options: List<String>, val correctIndex: Int)

// --- SCREEN 3: QUEST DETAILS (SMART QUIZ & READING ENGINE) ---
@Composable
fun QuestDetailsScreen(viewModel: EduQuestViewModel, navController: NavController) {
    val selectedQuest by viewModel.selectedQuest.collectAsState()
    val allCourses by viewModel.allCourses.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val activeCourse = allCourses.find { it.id == selectedQuest }
    val primaryColor = if (activeCourse?.isCore == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    // Dictionary defining the questions for every core course
    val courseQuizzes = mapOf(
        "MAD" to mapOf(
            "Lesson 1" to listOf(
                QuizQuestion("Which function is used to remember state?", listOf("Modifier.padding()", "remember { mutableStateOf() }", "NavHost()"), 1),
                QuizQuestion("What manages moving between screens?", listOf("NavController", "Card", "Button"), 0),
                QuizQuestion("What is a composable?", listOf("A UI element", "A database", "A server file"), 0)
            ),
            "Lesson 2" to listOf(
                QuizQuestion("What does ViewModel survive?", listOf("Phone calls", "App uninstalls", "Configuration changes/rotations"), 2),
                QuizQuestion("What handles live data streams?", listOf("StateFlow", "StaticText", "XML"), 0),
                QuizQuestion("How do you add space in Compose?", listOf("<br>", "Spacer()", "Margin()"), 1)
            )
        ),
        "SD" to mapOf(
            "Lesson 1" to listOf(
                QuizQuestion("What does SOLID stand for?", listOf("A design principle", "A database type", "A UI color"), 0),
                QuizQuestion("What is a UML diagram?", listOf("A visual model of a system", "A programming language", "A server"), 0),
                QuizQuestion("Which is a structural pattern?", listOf("Observer", "Singleton", "Adapter"), 2)
            ),
            "Lesson 2" to listOf(
                QuizQuestion("What does API stand for?", listOf("Application Programming Interface", "Apple Product Index", "Android Process Interactor"), 0),
                QuizQuestion("What is coupling?", listOf("Degree of interdependence", "Joining databases", "UI design"), 0),
                QuizQuestion("What is cohesion?", listOf("How strongly related elements are within a module", "Network speed", "Disk space"), 0)
            )
        ),
        "SQM" to mapOf(
            "Lesson 1" to listOf(
                QuizQuestion("What is Quality Assurance?", listOf("Testing software", "Preventing defects", "Writing code"), 1),
                QuizQuestion("What is a bug report?", listOf("A code error document", "A feature request", "A design file"), 0),
                QuizQuestion("Which testing is done by users?", listOf("Unit Testing", "UAT (User Acceptance Testing)", "Integration Testing"), 1)
            ),
            "Lesson 2" to listOf(
                QuizQuestion("What is Agile?", listOf("A waterfall method", "An iterative approach", "A programming language"), 1),
                QuizQuestion("What is a Scrum Master?", listOf("A boss", "A facilitator for an agile team", "A developer"), 1),
                QuizQuestion("What is a sprint?", listOf("A short, time-boxed period", "A database query", "A fast code compiler"), 0)
            )
        ),
        "UX" to mapOf(
            "Lesson 1" to listOf(
                QuizQuestion("What is User Persona?", listOf("A fictional character representing a user type", "A real user", "The app developer"), 0),
                QuizQuestion("What does Wireframing mean?", listOf("Connecting cables", "Creating a basic visual guide", "Writing backend logic"), 1),
                QuizQuestion("What is A/B testing?", listOf("Testing two versions to see which performs better", "A grading system", "Testing alphabet inputs"), 0)
            ),
            "Lesson 2" to listOf(
                QuizQuestion("What is accessibility?", listOf("Making apps usable for people with disabilities", "App download speed", "Server uptime"), 0),
                QuizQuestion("What is a Call to Action (CTA)?", listOf("A prompt urging the user to take action", "A phone call", "An error message"), 0),
                QuizQuestion("Which color contrast is best for reading?", listOf("Light gray on white", "Black on white", "Neon green on yellow"), 1)
            )
        )
    )

    // Local states for running the quiz engine
    var activeLessonName by remember { mutableStateOf<String?>(null) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(modifier = Modifier.height(32.dp))

        // Custom Top Bar with Back Button
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { navController.popBackStack() }) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, shape = CircleShape)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Abandon Quest", fontWeight = FontWeight.Bold, color = primaryColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = primaryColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(text = activeCourse?.title ?: "Course", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                // VIEW LOGIC: If it's a Citra Course, show Reading UI
                if (activeCourse?.isCore == false) {
                    val isCompleted = activeCourse.progress >= 1f
                    Text(text = activeCourse.description, color = Color.White, fontSize = 16.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            viewModel.completeReading(selectedQuest)
                            navController.popBackStack()
                        },
                        enabled = !isCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, disabledContainerColor = Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (isCompleted) "Lesson Completed" else "Finish Reading (+50 XP)", color = if (isCompleted) Color.White else primaryColor, fontWeight = FontWeight.ExtraBold)
                    }
                }
                // VIEW LOGIC: If Core Course & no lesson selected, show Lesson Menu
                else if (activeLessonName == null) {
                    Text("Select a lesson to begin:", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))

                    courseQuizzes[selectedQuest]?.keys?.forEach { lessonName ->
                        val lessonId = "${selectedQuest}_$lessonName"
                        // Fetches the user's past high score from ViewModel state
                        val highScore = uiState.lessonHighScores[lessonId] ?: 0

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { activeLessonName = lessonName },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(lessonName, color = primaryColor, fontWeight = FontWeight.Bold)
                                Text("Best: $highScore/3", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
                // VIEW LOGIC: Active Quiz Engine Screen
                else {
                    val currentQuiz = courseQuizzes[selectedQuest]?.get(activeLessonName) ?: emptyList()
                    val lessonId = "${selectedQuest}_$activeLessonName"
                    val previousHighScore = uiState.lessonHighScores[lessonId] ?: 0

                    // Shows results if quiz is over
                    if (quizFinished) {
                        val xpEarned = if (score > previousHighScore) (score - previousHighScore) * 50 else 0

                        Text("Quiz Complete!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("You scored $score out of 3.", fontSize = 16.sp, color = Color.White)

                        if (xpEarned > 0) {
                            Text("You beat your high score! Earned +$xpEarned XP!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                        } else {
                            Text("You didn't beat your high score of $previousHighScore. Keep trying!", fontSize = 16.sp, color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.completeQuiz(selectedQuest, lessonId, score)
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Claim Rewards & Return", color = primaryColor, fontWeight = FontWeight.ExtraBold)
                        }
                    } else { // Shows active question
                        val currentQ = currentQuiz[currentQuestionIndex]

                        Text("$activeLessonName - Question ${currentQuestionIndex + 1} of 3", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentQ.text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 24.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Maps through multiple choice options
                        currentQ.options.forEachIndexed { index, answerText ->
                            val isSelected = selectedAnswer == index
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedAnswer = index },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(answerText, modifier = Modifier.padding(16.dp), color = if (isSelected) primaryColor else Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Handles Next Question / Finish Logic
                        Button(
                            onClick = {
                                if (selectedAnswer == currentQ.correctIndex) score++
                                if (currentQuestionIndex < 2) {
                                    currentQuestionIndex++
                                    selectedAnswer = -1
                                } else {
                                    quizFinished = true
                                }
                            },
                            enabled = selectedAnswer != -1, // Enforces user to pick an answer
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, disabledContainerColor = Color.White.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (currentQuestionIndex == 2) "Finish Quiz" else "Next Question", color = primaryColor, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

// EXPANDABLE ANIMATED CARD (For Citra Courses on Dashboard)
@Composable
fun AnimatedSubjectCard(title: String, subtitle: String, details: String, bg: Color, txt: Color, modifier: Modifier, onClickAction: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        // .animateContentSize() enables smooth expansion animation without complex Keyframes
        modifier = modifier.clickable { expanded = !expanded }.animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = txt, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = txt, fontSize = 12.sp, lineHeight = 14.sp)

            // When user taps, expanded becomes true, revealing details
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                // TextOverflow.Ellipsis safely truncates text to fit UI design
                Text(
                    text = details,
                    color = txt,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClickAction, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.4f)), modifier = Modifier.fillMaxWidth()) {
                    Text("Start", color = Color.White)
                }
            }
        }
    }
}

// --- SCREEN 4: ACTIVE QUESTS (GAMIFIED TO-DO LIST) ---
@Composable
fun ActiveQuestsScreen(viewModel: EduQuestViewModel, navController: NavController) {
    // Listens dynamically to the StateFlow list in ViewModel
    val myQuests by viewModel.customQuests.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, shape = CircleShape)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("My Custom Quests", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("CreateQuest") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+ Create New Quest", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DYNAMIC LIST RENDERING: Maps through the StateFlow data
        if (myQuests.isEmpty()) {
            Text("No active quests. Create one to earn more XP!", color = Color.Gray)
        } else {
            myQuests.forEach { quest ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = quest.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "+${quest.xpReward} XP Reward", color = Color(0xFFF59E0B), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.completeCustomQuest(quest) }, // Triggers ViewModel math on click
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 5: CREATE QUEST (ADD ITEM FORM) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestScreen(viewModel: EduQuestViewModel, navController: NavController) {
    var titleInput by remember { mutableStateOf("") }
    var xpInput by remember { mutableStateOf("50") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("Forging a New Quest", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it; errorMessage = "" },
            label = { Text("What do you need to study?") },
            placeholder = { Text("e.g., Read Chapter 5") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Note: Using standard text field for numbers, but handling string-to-int conversion manually
        OutlinedTextField(
            value = xpInput,
            onValueChange = { xpInput = it; errorMessage = "" },
            label = { Text("XP Reward") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val xpValue = xpInput.toIntOrNull() // Safety check for valid numbers
                    if (titleInput.isBlank() || xpValue == null || xpValue <= 0) {
                        errorMessage = "Please enter a valid task and XP amount."
                    } else {
                        // Project 1: Saving input state safely to the ViewModel
                        viewModel.addCustomQuest(titleInput, xpValue)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Quest")
            }
        }
    }
}
