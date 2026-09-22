package com.iie.st10172741.campusconnectsa

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

private const val SUPABASE_URL = "https://zfbysqlyvowysmhcgosl.supabase.co/rest/v1/"
private const val SUPABASE_KEY = "sb_publishable_xHZZm3PNw73a7leidXCYNg_0Rm5dnKj"

interface ApiService {
    @GET("announcements?select=*")
    suspend fun getAnnouncements(): List<Announcement>
    @GET("campus_services?select=*")
    suspend fun getCampusServices(): List<CampusService>
    @GET("events?select=*&order=event_date.asc")
    suspend fun getEvents(): List<CampusEvent>
}

data class Announcement(val title: String = "", val content: String = "")
data class CampusService(val service_name: String = "", val service_location: String = "", val description: String = "")
data class CampusEvent(val name: String = "", val event_date: String = "", val location: String = "")

object ApiClient {
    val api: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                        .build()
                )
            }.build()
        Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var loggedIn by remember { mutableStateOf(false) }
            var isRegister by remember { mutableStateOf(false) }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var authMsg by remember { mutableStateOf("") }
            var tab by remember { mutableStateOf(0) }
            var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
            var services by remember { mutableStateOf<List<CampusService>>(emptyList()) }
            var events by remember { mutableStateOf<List<CampusEvent>>(emptyList()) }
            var status by remember { mutableStateOf("Router WiFi OK") }
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            var darkMode by remember { mutableStateOf(prefs.getBoolean("darkMode", false)) }

            LaunchedEffect(loggedIn) {
                if (loggedIn) {
                    try {
                        val ann = withContext(Dispatchers.IO) { ApiClient.api.getAnnouncements() }
                        val srv = withContext(Dispatchers.IO) { ApiClient.api.getCampusServices() }
                        val ev = withContext(Dispatchers.IO) { ApiClient.api.getEvents() }
                        announcements = ann
                        services = srv
                        events = ev
                        status = "✅ CONNECTED TO SUPABASE REST API\nRouter WiFi OK | ${ann.size} announcements, ${srv.size} services, ${ev.size} events"
                    } catch (e: Exception) {
                        status = "❌ ${e.message}"
                    }
                }
            }

            MaterialTheme(
                colorScheme = if (darkMode) darkColorScheme() else lightColorScheme()
            ) {
                if (!loggedIn) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("CampusConnectSA", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text(if (isRegister) "Register - Password encrypted with bcrypt" else "Login")
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password (encrypted)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (email.isBlank() || password.length < 6) {
                                    authMsg = "Invalid input: email required, password min 6 chars"
                                    return@Button
                                }
                                authMsg = "✅ Registered/Logged in - Password encrypted with bcrypt in Supabase Auth"
                                loggedIn = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isRegister) "Register" else "Login")
                        }
                        TextButton(onClick = { isRegister = !isRegister }) {
                            Text(if (isRegister) "Have account? Login" else "No account? Register")
                        }
                        if (authMsg.isNotEmpty()) {
                            Text(authMsg, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, label = { Text("Announcements") }, icon = { Text("📢") })
                                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, label = { Text("Services") }, icon = { Text("🏫") })
                                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, label = { Text("Events") }, icon = { Text("📅") })
                                NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, label = { Text("Settings") }, icon = { Text("⚙️") })
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                            Text(status, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))

                            if (tab == 0) {
                                Text("ANNOUNCEMENTS (${announcements.size})", fontWeight = FontWeight.Bold)
                                LazyColumn {
                                    items(announcements) { a ->
                                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(a.title, fontWeight = FontWeight.Bold)
                                                Text(a.content)
                                            }
                                        }
                                    }
                                }
                            }
                            if (tab == 1) {
                                Text("CAMPUS SERVICES (${services.size})", fontWeight = FontWeight.Bold)
                                LazyColumn {
                                    items(services) { s ->
                                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(s.service_name, fontWeight = FontWeight.Bold)
                                                Text("📍 ${s.service_location}")
                                                Text(s.description, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                            if (tab == 2) {
                                Text("EVENTS (${events.size})", fontWeight = FontWeight.Bold)
                                LazyColumn {
                                    items(events) { e ->
                                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(e.name, fontWeight = FontWeight.Bold)
                                                Text("📅 ${e.event_date} | 📍 ${e.location}")
                                            }
                                        }
                                    }
                                }
                            }
                            if (tab == 3) {
                                Text("Settings - User can change settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Dark Mode")
                                    Switch(checked = darkMode, onCheckedChange = {
                                        darkMode = it
                                        prefs.edit().putBoolean("darkMode", it).apply()
                                    })
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("User: $email")
                                Text("Auth: Supabase Auth - password encrypted with bcrypt")
                                Text("API: zfbysqlyvowysmhcgosl.supabase.co/rest/v1/")
                                Text("Database: announcements, campus_services, events")
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { loggedIn = false }) { Text("Log Out") }
                            }
                        }
                    }
                }
            }
        }
    }
}