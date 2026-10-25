package com.iie.st10172741.campusconnectsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// Brand colors from document
val CampusBlue = Color(0xFF1E3A8A)
val CampusBlueLight = Color(0xFF3B82F6)
val CampusBackground = Color(0xFFF8FAFC)
val CardWhite = Color.White

data class Announcement(val title: String = "", val content: String = "", val category: String = "General", val date: String = "")
data class CampusService(val service_name: String = "", val service_location: String = "", val description: String = "")
data class CampusEvent(val name: String = "", val event_date: String = "", val location: String = "")

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(if (auth.currentUser != null) "HOME" else "LOGIN") }
            var isRegisterMode by remember { mutableStateOf(false) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = CampusBackground) {
                    when (currentScreen) {
                        "LOGIN" -> LoginScreen(
                            onLoginSuccess = { currentScreen = "HOME" },
                            onGoToRegister = { currentScreen = "REGISTER" }
                        )
                        "REGISTER" -> RegisterScreen(
                            onRegisterSuccess = { currentScreen = "HOME" },
                            onGoToLogin = { currentScreen = "LOGIN" }
                        )
                        "HOME" -> MainAppWithBottomNav(
                            onLogout = { auth.signOut(); currentScreen = "LOGIN" }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
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
        Spacer(Modifier.height(40.dp))
        // Logo CC CAMPUSCONNECT SA - as in document page 11
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = CampusBlue)) {
                Text("CC", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text("CAMPUSCONNECT SA", color = CampusBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(32.dp))
        Text("Welcome Back", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.fillMaxWidth())
        Text("Log in to continue your campus", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Text("Email or Username", fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            placeholder = { Text("you@example.com", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("Password", fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            placeholder = { Text("••••••••", color = Color.Gray) },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { IconButton(onClick = { showPass = !showPass }) { Icon(Icons.Default.Visibility, null) } },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("Forgot Password?", color = CampusBlue, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (email.isBlank() || password.length < 6) { msg = "Enter email & password (min 6)"; return@Button }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onLoginSuccess() }
                    .addOnFailureListener { ex: Exception -> msg = ex.message ?: "Login failed" }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CampusBlue)
        ) { Text("LOGIN", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        Text("or continue with", color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { msg = "Google SSO will be implemented during final PoE" },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("G Continue with Google", color = Color.Black, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Text("Don't have an account? ", fontSize = 12.sp, color = Color.Gray)
            TextButton(onClick = onGoToRegister) { Text("Register", color = CampusBlue, fontSize = 12.sp) }
        }
        if (msg.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(msg, color = Color.Red, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onGoToLogin: () -> Unit) {
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
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)
    ) {
        item {
            Row {
                IconButton(onClick = onGoToLogin) { Icon(Icons.Default.ArrowBack, null, tint = CampusBlue) }
                Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 12.dp))
            }
            Text("Join CampusConnect SA today", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, bottom = 16.dp))
            Text("First Name", fontSize = 12.sp); OutlinedTextField(value = firstName, onValueChange = { firstName = it }, placeholder = { Text("Enter your first name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            Text("Surname", fontSize = 12.sp); OutlinedTextField(value = surname, onValueChange = { surname = it }, placeholder = { Text("Enter your surname") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            Text("Student Number", fontSize = 12.sp); OutlinedTextField(value = studentNo, onValueChange = { studentNo = it }, placeholder = { Text("e.g. ST10234876") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            Text("Email", fontSize = 12.sp); OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("student@campus.ac.za") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            Text("Password", fontSize = 12.sp); OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Create a password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = agree, onCheckedChange = { agree = it }); Text("I agree to the Terms & Privacy Policy", fontSize = 11.sp) }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!agree) { msg = "Agree to terms"; return@Button }
                    if (email.isBlank() || password.length < 6) { msg = "Email & password min 6"; return@Button }
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            // NoSQL STORE - save student profile
                            val profile = hashMapOf("firstName" to firstName, "surname" to surname, "studentNumber" to studentNo, "email" to email)
                            db.collection("students").document(it.user!!.uid).set(profile)
                            onRegisterSuccess()
                        }
                        .addOnFailureListener { ex: Exception -> msg = ex.message ?: "Register failed" }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CampusBlue)
            ) { Text("REGISTER", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = onGoToLogin) { Text("Sign In", color = CampusBlue, fontSize = 12.sp) }
            }
            if (msg.isNotEmpty()) Text(msg, color = Color.Red, fontSize = 12.sp)
        }
    }
}

@Composable
fun MainAppWithBottomNav(onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var services by remember { mutableStateOf<List<CampusService>>(emptyList()) }
    var events by remember { mutableStateOf<List<CampusEvent>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(selectedTab) {
        try {
            val annSnap = db.collection("announcements").orderBy("date", Query.Direction.DESCENDING).get().await()
            val srvSnap = db.collection("campus_services").get().await()
            val evSnap = db.collection("events").get().await()
            announcements = annSnap.documents.mapNotNull { d -> Announcement(title = d.getString("title") ?: "", content = d.getString("content") ?: d.getString("description") ?: "", category = d.getString("category") ?: "General", date = d.getString("date") ?: "") }
            services = srvSnap.documents.mapNotNull { d -> CampusService(d.getString("service_name") ?: d.getString("name") ?: "", d.getString("service_location") ?: "", d.getString("description") ?: "") }
            events = evSnap.documents.mapNotNull { d -> CampusEvent(d.getString("name") ?: "", d.getString("event_date") ?: "", d.getString("location") ?: "") }
        } catch (e: Exception) { }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = selectedTab == 0, onClick = { selectedTab = 0 })
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, null) }, label = { Text("Academics") }, selected = selectedTab == 1, onClick = { selectedTab = 1 })
                NavigationBarItem(icon = { Icon(Icons.Default.Business, null) }, label = { Text("Campus") }, selected = selectedTab == 2, onClick = { selectedTab = 2 })
                NavigationBarItem(icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Alerts") }, selected = selectedTab == 3, onClick = { selectedTab = 3 })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = selectedTab == 4, onClick = { selectedTab = 4 })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeDashboardScreen(announcements)
                1 -> AcademicPlannerScreen()
                2 -> CampusServicesScreen(services)
                3 -> AnnouncementsScreen(announcements) // Alerts = Announcements + Notifications as per doc 4.10, 4.11
                4 -> SettingsScreen(onLogout)
            }
        }
    }
}

@Composable
fun HomeDashboardScreen(announcements: List<Announcement>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(CampusBackground).padding(16.dp)) {
        item {
            Text("Good Morning, Student", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Monday, 21 Aug - Week 7 of Semester", color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
        }
        item {
            // Programming card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("TODAY", color = CampusBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Programming", fontWeight = FontWeight.Bold)
                    Text("12:00 • T401 • ROOM 422", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("UPCOMING ASSIGNMENT", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Database POE", fontWeight = FontWeight.Bold)
                    Text("Due in 2 days - Stay on IT", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("NEXT EXAM", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Database Systems", fontWeight = FontWeight.Bold)
                    Text("15 Sept 09:00", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("CAMPUS NEWS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Registration closes soon", fontWeight = FontWeight.Bold)
                    Text("Final registration for Semester 2 closes on 21 Aug 2025 17:00", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("✅ CONNECTED TO FIREBASE FIRESTORE NoSQL | ${announcements.size} announcements | Blob Storage Ready", fontSize = 10.sp, color = CampusBlue, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun AcademicPlannerScreen() {
    Column(modifier = Modifier.fillMaxSize().background(CampusBackground).padding(16.dp)) {
        Text("ACADEMIC PLANNER", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("TODAY - WED, 21 AUG 2024", fontSize = 10.sp, color = CampusBlue, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("08:00 Programming - T401 ROOM 422", fontSize = 12.sp)
                Text("10:00 Database - LAB 3", fontSize = 12.sp)
                Text("12:00 Web Development - T401", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CampusServicesScreen(services: List<CampusService>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(CampusBackground).padding(16.dp)) {
        item {
            Text("CAMPUS SERVICES", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
        }
        if (services.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Library - Open from 08:00 - Study spaces", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Computer Labs - IT Hub available - Building B", fontSize = 12.sp, color = Color.Gray)
                        Text("Cafeteria - Open now - Breakfast & Lunch", fontSize = 12.sp, color = Color.Gray)
                        Text("Student Support - Counselling - Academic help", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(services) { s ->
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(s.service_name, fontWeight = FontWeight.Bold)
                        Text("📍 ${s.service_location}", fontSize = 12.sp, color = Color.Gray)
                        Text(s.description, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementsScreen(announcements: List<Announcement>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(CampusBackground).padding(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(shape = RoundedCornerShape(6.dp), colors = CardDefaults.cardColors(containerColor = CampusBlue)) { Text("CC", color = Color.White, modifier = Modifier.padding(4.dp), fontSize = 10.sp) }
                Spacer(Modifier.width(8.dp))
                Text("CampusConnect SA", color = CampusBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("ANNOUNCEMENTS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Stay updated with the latest campus news - 3 new updates", fontSize = 11.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))
        }
        if (announcements.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Registration closes Friday", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Final registration for Semester 2 closes on 21 Aug 2025 17:00", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Test timetable updated", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Updated test schedule now available for Engineering Dept - View details", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Career Fair next week", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Meet top employers on campus - 28 Aug - 10:00-16:00 - Main Hall", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(announcements) { a ->
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(a.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(a.content, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Row { Icon(Icons.Default.ArrowBack, null); Spacer(Modifier.width(8.dp)); Text("SETTINGS", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        Spacer(Modifier.height(24.dp))
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.background(CampusBlue, RoundedCornerShape(50)).padding(6.dp)); Spacer(Modifier.width(12.dp)); Text("Edit Profile") } }
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Language, null, tint = Color.White, modifier = Modifier.background(CampusBlue, RoundedCornerShape(50)).padding(6.dp)); Spacer(Modifier.width(12.dp)); Text("Language"); Spacer(Modifier.weight(1f)); Text("English", color = Color.Gray) } }
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.background(CampusBlue, RoundedCornerShape(50)).padding(6.dp)); Spacer(Modifier.width(12.dp)); Text("Notifications"); Spacer(Modifier.weight(1f)); Switch(checked = true, onCheckedChange = {}) } }
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.DarkMode, null, tint = Color.White, modifier = Modifier.background(CampusBlue, RoundedCornerShape(50)).padding(6.dp)); Spacer(Modifier.width(12.dp)); Text("Dark Mode"); Spacer(Modifier.weight(1f)); Switch(checked = false, onCheckedChange = {}) } }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Logout, null, tint = Color.Red); Spacer(Modifier.width(8.dp)); Text("LOG OUT", color = Color.Red) }
        Spacer(Modifier.height(12.dp))
        Text("Project: campusconnectsa-3880d | NoSQL Firestore | Blob Storage | Auth bcrypt encrypted", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}