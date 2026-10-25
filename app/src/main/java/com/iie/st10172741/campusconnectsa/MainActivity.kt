
package com.iie.st10172741.campusconnectsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

val CampusBlue = Color(0xFF1E3A8A)
val CampusBlueLight = Color(0xFF3B82F6)
val CampusBackground = Color(0xFFF8FAFC)
val CardWhite = Color.White

data class Announcement(
    val title: String = "",
    val content: String = "",
    val category: String = "General",
    val date: String = ""
)

data class CampusService(
    val service_name: String = "",
    val service_location: String = "",
    val description: String = ""
)

data class CampusEvent(
    val name: String = "",
    val event_date: String = "",
    val location: String = ""
)

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showSplash by remember {
                mutableStateOf(true)
            }
            var currentScreen by remember {
                mutableStateOf(
                    if (auth.currentUser != null) "HOME" else "LOGIN"
                )
            }

            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CampusBackground
                ) {
                    if (showSplash) {
                        SplashScreen()
                    } else {
                        when (currentScreen) {
                            "LOGIN" -> LoginScreen(
                                onLoginSuccess = {
                                    currentScreen = "HOME"
                                },
                                onGoToRegister = {
                                    currentScreen = "REGISTER"
                                }
                            )
                            "REGISTER" -> RegisterScreen(
                                onRegisterSuccess = {
                                    currentScreen = "HOME"
                                },
                                onGoToLogin = {
                                    currentScreen = "LOGIN"
                                }
                            )
                            "HOME" -> MainAppWithBottomNav(
                                onLogout = {
                                    auth.signOut()
                                    currentScreen = "LOGIN"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDBEAFE),
                        Color.White
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎓",
                    fontSize = 32.sp
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = "📍",
                    fontSize = 32.sp
                )
            }
            Spacer(
                modifier = Modifier.height(12.dp)
            )
            Text(
                text = "CAMPUSCONNECT SA",
                color = CampusBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "Smart Student Assistant",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(
                modifier = Modifier.height(80.dp)
            )
            Text(
                text = "Loading...",
                color = Color.Gray,
                fontSize = 10.sp
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            LinearProgressIndicator(
                modifier = Modifier.width(60.dp),
                color = CampusBlue
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CampusBlue
                )
            ) {
                Text(
                    text = "CC",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CAMPUSCONNECT SA",
                color = CampusBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Log in to continue your campus",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Email or Username",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    text = "you@example.com",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Password",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "••••••••",
                    color = Color.Gray
                )
            },
            visualTransformation = if (showPass) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPass = !showPass }
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot Password?",
            color = CampusBlue,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.length < 6) {
                    msg = "Enter email & password (min 6)"
                    return@Button
                }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        onLoginSuccess()
                    }
                    .addOnFailureListener { ex: Exception ->
                        msg = ex.message ?: "Login failed"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CampusBlue
            )
        ) {
            Text(
                text = "LOGIN",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "or continue with",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                msg = "Google SSO will be implemented during final PoE"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "G Continue with Google",
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = "Don't have an account? ",
                fontSize = 12.sp,
                color = Color.Gray
            )
            TextButton(
                onClick = onGoToRegister
            ) {
                Text(
                    text = "Register",
                    color = CampusBlue,
                    fontSize = 12.sp
                )
            }
        }

        if (msg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var studentNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agree by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        item {
            Row {
                IconButton(
                    onClick = onGoToLogin
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = CampusBlue
                    )
                }
                Text(
                    text = "CREATE ACCOUNT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Text(
                text = "Join CampusConnect SA today",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    start = 4.dp,
                    bottom = 16.dp
                )
            )

            Text(text = "First Name", fontSize = 12.sp)
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("Enter your first name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Surname", fontSize = 12.sp)
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                placeholder = { Text("Enter your surname") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Student Number", fontSize = 12.sp)
            OutlinedTextField(
                value = studentNo,
                onValueChange = { studentNo = it },
                placeholder = { Text("e.g. ST10234876") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Email", fontSize = 12.sp)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("student@campus.ac.za") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Password", fontSize = 12.sp)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Create a password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agree,
                    onCheckedChange = { agree = it }
                )
                Text(
                    text = "I agree to the Terms & Privacy Policy",
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!agree) {
                        msg = "Agree to terms"
                        return@Button
                    }
                    if (email.isBlank() || password.length < 6) {
                        msg = "Email & password min 6"
                        return@Button
                    }
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val profile = hashMapOf(
                                "firstName" to firstName,
                                "surname" to surname,
                                "studentNumber" to studentNo,
                                "email" to email
                            )
                            db.collection("students")
                                .document(it.user!!.uid)
                                .set(profile)
                            onRegisterSuccess()
                        }
                        .addOnFailureListener { ex: Exception ->
                            msg = ex.message ?: "Register failed"
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CampusBlue
                )
            ) {
                Text(
                    text = "REGISTER",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = onGoToLogin
                ) {
                    Text(
                        text = "Sign In",
                        color = CampusBlue,
                        fontSize = 12.sp
                    )
                }
            }

            if (msg.isNotEmpty()) {
                Text(
                    text = msg,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun MainAppWithBottomNav(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var announcements by remember {
        mutableStateOf<List<Announcement>>(emptyList())
    }
    var services by remember {
        mutableStateOf<List<CampusService>>(emptyList())
    }
    var events by remember {
        mutableStateOf<List<CampusEvent>>(emptyList())
    }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(selectedTab) {
        try {
            val annSnap = db.collection("announcements")
                .get().await()
            val srvSnap = db.collection("campus_services")
                .get().await()
            val evSnap = db.collection("events")
                .get().await()

            announcements = annSnap.documents.mapNotNull { d ->
                Announcement(
                    title = d.getString("title") ?: "",
                    content = d.getString("content") ?: ""
                )
            }
            services = srvSnap.documents.mapNotNull { d ->
                CampusService(
                    service_name = d.getString("service_name")
                        ?: d.getString("name") ?: "",
                    service_location = d.getString("service_location") ?: "",
                    description = d.getString("description") ?: ""
                )
            }
            events = evSnap.documents.mapNotNull { d ->
                CampusEvent(
                    name = d.getString("name") ?: "",
                    event_date = d.getString("event_date") ?: "",
                    location = d.getString("location") ?: ""
                )
            }
        } catch (e: Exception) {
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, null) },
                    label = { Text("Academics") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Business, null) },
                    label = { Text("Campus") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, null) },
                    label = { Text("Alerts") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            when (selectedTab) {
                0 -> HomeDashboardScreen(announcements)
                1 -> AcademicPlannerScreen()
                2 -> CampusServicesScreen(services)
                3 -> AnnouncementsScreen(announcements)
                4 -> SettingsScreen(onLogout)
            }
        }
    }
}

@Composable
fun HomeDashboardScreen(
    announcements: List<Announcement>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CampusBackground)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Good Morning, Student",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "Monday, 21 Aug - Week 7 of Semester",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "TODAY",
                        color = CampusBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Programming",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "12:00 • T401 • ROOM 422",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "UPCOMING ASSIGNMENT",
                        color = Color(0xFFF59E0B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Database POE",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Due in 2 days - Stay on IT",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "NEXT EXAM",
                        color = Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Database Systems",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "15 Sept 09:00",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "CAMPUS NEWS",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Registration closes soon",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Final registration for Semester 2 closes on 21 Aug 2025 17:00",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "✅ CONNECTED TO FIREBASE FIRESTORE NoSQL | ${announcements.size} announcements | Blob Storage Ready",
                fontSize = 10.sp,
                color = CampusBlue,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AcademicPlannerScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Column {
                        Text(
                            text = "ACADEMIC",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CampusBlue
                        )
                        Text(
                            text = "PLANNER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CampusBlue
                        )
                        Text(
                            text = "CampusConnect SA - Semester 2, 2024",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf(
                    "MON\n19",
                    "TUE\n20",
                    "WED\n21",
                    "THU\n22",
                    "FRI\n23"
                )
                days.forEachIndexed { index, day ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (index == 2) {
                                CampusBlue
                            } else {
                                Color(0xFFF1F5F9)
                            }
                        ),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = day,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (index == 2) {
                                Color.White
                            } else {
                                Color.Black
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TODAY - WED, 21 AUG 2024",
                fontSize = 10.sp,
                color = CampusBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = CampusBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SCHEDULE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(
                                CampusBlue,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "08:00  Programming",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Thu 22 Aug - Lab - Room B12 - Prof. Jacobs",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(
                                CampusBlue,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "10:00  Database",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Lecture - Room C03 - Prof. Mokoena",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(
                                CampusBlue,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "13:00  Web Development",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Workshop - Lab D1 - Ms. van Wyk",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    tint = CampusBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ASSIGNMENTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Submit DB Assignment - Due Fri, 23 Aug  Due in 2 days",
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = "• Web Dev Project  Draft due Tue, 27 Aug  Pending",
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = CampusBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "TESTS / EXAMS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Programming Quiz  Thu 22 Aug - 10:00  Room A05 - 30 min",
                fontSize = 10.sp
            )
            Text(
                text = "Database Test  Fri 23 Aug - 14:00  Hall 3 - 60 min",
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CampusBlue
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ADD",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Purpose: Allows students to view academic activities.",
                fontSize = 10.sp,
                color = CampusBlue,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CampusServicesScreen(
    services: List<CampusService>
) {
    var showTransport by remember { mutableStateOf(false) }
    if (showTransport) {
        CampusTransportScreen(onBack = { showTransport = false })
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = CampusBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CAMPUS SERVICES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E3A8A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CampusBlue
                    )
                ) {
                    Text(
                        text = "CC",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 6.dp,
                            vertical = 2.dp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CampusConnect SA",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = "Search services...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SERVICES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Figma static list matching screenshot
        item {
            CampusServiceCard(
                icon = "📚",
                title = "Library",
                subtitle = "Open 8am-10pm - Study spaces"
            )
            CampusServiceCard(
                icon = "💻",
                title = "Computer Labs",
                subtitle = "12 workstations available - Building B"
            )
            CampusServiceCard(
                icon = "🍽️",
                title = "Cafeteria",
                subtitle = "Open now - Breakfast & Lunch"
            )
            CampusServiceCard(
                icon = "🎧",
                title = "Student Support",
                subtitle = "Counselling - Academic help"
            )
            CampusServiceCard(
                icon = "🚌",
                title = "Campus Transport",
                subtitle = "Next shuttle: 5 min - Routes",
                onClick = { showTransport = true }
            )
            CampusServiceCard(
                icon = "🛡️",
                title = "Campus Security",
                subtitle = "24/7 - Emergency: 10111"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // If Firestore has services, show them too
            if (services.isNotEmpty()) {
                Text(
                    text = "Live from Firestore NoSQL (${services.size})",
                    fontSize = 10.sp,
                    color = CampusBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Purpose: Provides information about useful campus services.",
                fontSize = 10.sp,
                color = CampusBlue,
                fontWeight = FontWeight.Medium
            )
        }

        if (services.isNotEmpty()) {
            items(services) { s ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Color(0xFFDBEAFE),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📍", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = s.service_name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = s.description,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = s.service_location,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CampusServiceCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        CampusBlue,
                        RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color(0xFFDBEAFE),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Composable
fun AnnouncementsScreen(
    announcements: List<Announcement>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CampusBackground)
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CampusBlue
                    )
                ) {
                    Text(
                        text = "CC",
                        color = Color.White,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CampusConnect SA",
                    color = CampusBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ANNOUNCEMENTS",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Stay updated with the latest campus news - 3 new updates",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (announcements.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Registration closes Friday",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Final registration for Semester 2 closes on 21 Aug 2025 17:00",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Test timetable updated",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Updated test schedule now available for Engineering Dept - View details",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Career Fair next week",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Meet top employers on campus - 28 Aug - 10:00-16:00 - Main Hall",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(announcements) { a ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = a.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = a.content,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun CampusTransportScreen(
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(0.dp)
    ) {
        item {
            // Header blue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CampusBlue)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CAMPUS TRANSPORT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CampusConnect SA • Real-time Shuttle Tracking\nStellenbosch Campus",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = CampusBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ROUTE A
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(40.dp)
                                    .background(CampusBlue, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFDBEAFE), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = CampusBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ROUTE A",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 8.sp,
                                            color = CampusBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Main Gate → Residence",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Next Shuttle: 14:30",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Arriving in 5 min • 2 stops",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CampusBlue),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "VIEW ROUTE",
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }

                // ROUTE B
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(40.dp)
                                    .background(CampusBlue, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFDBEAFE), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = CampusBlue
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ROUTE B",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDBEAFE))
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 8.sp,
                                            color = CampusBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Campus → Shopping Centre",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Next Shuttle: 15:00",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Arriving in 28 min • 4 stops",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CampusBlue),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "VIEW ROUTE",
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Shuttles run Mon-Fri • 07:00-18:00",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Purpose: Provides students with transport information.\n4.8 Campus Transport Screen - Firestore collection: transport\nProject: campusconnectsa-3880d | NoSQL | Blob Storage",
                    fontSize = 9.sp,
                    color = CampusBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .background(CampusBlue, RoundedCornerShape(50))
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Edit Profile")
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Language,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .background(CampusBlue, RoundedCornerShape(50))
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Language")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "English", color = Color.Gray)
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .background(CampusBlue, RoundedCornerShape(50))
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Notifications")
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = {})
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .background(CampusBlue, RoundedCornerShape(50))
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Dark Mode")
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = false, onCheckedChange = {})
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val db = FirebaseFirestore.getInstance()
                db.collection("services").document("SVC001").set(
                    mapOf(
                        "service_id" to "SVC001",
                        "name" to "Library",
                        "description" to "Study spaces and books",
                        "location" to "Main Campus Building A",
                        "operating_hours" to "08:00-17:00"
                    )
                )
                db.collection("services").document("SVC002").set(
                    mapOf(
                        "service_id" to "SVC002",
                        "name" to "Computer Lab",
                        "description" to "IT Hub",
                        "location" to "Building B",
                        "operating_hours" to "07:00-18:00"
                    )
                )
                db.collection("events").document("EVT001").set(
                    mapOf(
                        "event_id" to "EVT001",
                        "title" to "Career Fair",
                        "description" to "Meet top employers",
                        "venue" to "Main Hall",
                        "event_date" to "2025-08-28"
                    )
                )
                db.collection("transport").document("R001").set(
                    mapOf(
                        "route_id" to "R001",
                        "route_name" to "Campus Shuttle",
                        "pickup_point" to "Rosebank Station",
                        "destination" to "Main Campus",
                        "schedule" to "Every 30 mins"
                    )
                )
                db.collection("modules").document("MOD001").set(
                    mapOf(
                        "module_id" to "MOD001",
                        "module_name" to "Open Source Programming",
                        "module_code" to "OPSC6312"
                    )
                )
                db.collection("classes").document("CLS001").set(
                    mapOf(
                        "class_id" to "CLS001",
                        "user_id" to "ST10172741",
                        "module_id" to "MOD001",
                        "venue" to "T401 ROOM 422",
                        "date" to "2025-08-21",
                        "start_time" to "08:00",
                        "end_time" to "10:00"
                    )
                )
                db.collection("assignments").document("ASS001").set(
                    mapOf(
                        "assignment_id" to "ASS001",
                        "user_id" to "ST10172741",
                        "module_id" to "MOD001",
                        "title" to "Database POE",
                        "description" to "Part 3 NoSQL",
                        "due_date" to "2025-08-25",
                        "priority" to "high",
                        "completed" to false
                    )
                )
                db.collection("exams").document("EX001").set(
                    mapOf(
                        "exam_id" to "EX001",
                        "user_id" to "ST10172741",
                        "module_id" to "MOD001",
                        "date" to "2025-09-15"
                    )
                )
                db.collection("resources").document("RES001").set(
                    mapOf(
                        "resource_id" to "RES001",
                        "module_id" to "MOD001",
                        "title" to "POE Part 1 Document",
                        "type" to "pdf",
                        "location" to "resources/OPSC6312.pdf"
                    )
                )
                db.collection("notifications").document("NOT001").set(
                    mapOf(
                        "notification_id" to "NOT001",
                        "user_id" to "ST10172741",
                        "title" to "Assignment Due",
                        "message" to "Database POE due in 2 days",
                        "created_at" to "2025-08-25"
                    )
                )
                db.collection("users").document("ST10172741").set(
                    mapOf(
                        "user_id" to "ST10172741",
                        "first_name" to "Keoagile",
                        "surname" to "Letlape",
                        "student_number" to "ST10172741",
                        "email" to "ST10172741@iiemail.com",
                        "created_at" to "2025-08-25"
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981)
            )
        ) {
            Text(text = "POPULATE ALL COLLECTIONS (1 click)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LOG OUT",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Project: campusconnectsa-3880d | NoSQL Firestore | Blob Storage | Auth bcrypt encrypted",
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
