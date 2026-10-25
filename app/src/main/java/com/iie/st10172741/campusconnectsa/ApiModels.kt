package com.iie.st10172741.campusconnectsa

import org.json.JSONObject
import com.google.gson.Gson

/**
 * 6.3 API Data Format
 * The API will use JSON.
 * For example, when requesting an assignment, the API could return:
 * {
 *   "id": 15,
 *   "title": "Database POE",
 *   "module": "Database Systems",
 *   "dueDate": "2026-09-10",
 *   "priority": "High",
 *   "completed": false
 * }
 * The Android application will convert the JSON response into Kotlin data objects.
 *
 * Project: campusconnectsa-3880d
 * Student: ST10172741 Keoagile Letlape
 */

// 6.3 JSON example - Assignment
data class AssignmentJson(
    val id: Int = 0,
    val title: String = "",
    val module: String = "",
    val dueDate: String = "",
    val priority: String = "High",
    val completed: Boolean = false
) {
    // Convert JSON string -> Kotlin data object (as per POE requirement)
    companion object {
        fun fromJson(jsonString: String): AssignmentJson {
            return try {
                // Method 1: Using JSONObject (Android built-in)
                val json = JSONObject(jsonString)
                AssignmentJson(
                    id = json.getInt("id"),
                    title = json.getString("title"),
                    module = json.getString("module"),
                    dueDate = json.getString("dueDate"),
                    priority = json.getString("priority"),
                    completed = json.getBoolean("completed")
                )
            } catch (e: Exception) {
                // Method 2: Using Gson (fallback)
                Gson().fromJson(jsonString, AssignmentJson::class.java)
            }
        }

        fun fromJsonGson(jsonString: String): AssignmentJson {
            // Using Gson library - converts JSON response into Kotlin data objects
            return Gson().fromJson(jsonString, AssignmentJson::class.java)
        }

        fun toJson(obj: AssignmentJson): String {
            // Kotlin data object -> JSON string
            return Gson().toJson(obj)
        }

        // Example from POE document 6.3
        fun exampleJson(): String {
            return """
            {
              "id": 15,
              "title": "Database POE",
              "module": "Database Systems",
              "dueDate": "2026-09-10",
              "priority": "High",
              "completed": false
            }
            """.trimIndent()
        }

        fun exampleObject(): AssignmentJson {
            // Convert example JSON to Kotlin object
            val json = exampleJson()
            return fromJson(json)
        }
    }
}

// Other API Data Formats - JSON for all endpoints (6.4)

data class RegisterRequestJson(
    val first_name: String,
    val surname: String,
    val student_number: String,
    val email: String,
    val password: String
)

data class LoginRequestJson(
    val email: String,
    val password: String
)

data class LoginResponseJson(
    val token: String,
    val user_id: String,
    val email: String,
    val message: String
)

data class ProfileJson(
    val user_id: String,
    val first_name: String,
    val surname: String,
    val student_number: String,
    val email: String,
    val created_at: String
)

data class ModuleJson(
    val module_id: String,
    val module_name: String,
    val module_code: String,
    val credits: Int
)

data class ClassJson(
    val class_id: String,
    val user_id: String,
    val module_id: String,
    val module_name: String,
    val venue: String,
    val date: String,
    val start_time: String,
    val end_time: String
)

data class ExamJson(
    val exam_id: String,
    val module: String,
    val date: String,
    val venue: String,
    val duration: String,
    val days_left: Int
)

data class ResourceJson(
    val resource_id: String,
    val module_id: String,
    val title: String,
    val type: String,
    val location: String,
    val url: String // Blob Storage URL per POE
)

data class AnnouncementJson(
    val announcement_id: String,
    val title: String,
    val content: String,
    val date_created: String,
    val priority: String,
    val category: String
)

data class EventJson(
    val event_id: String,
    val title: String,
    val description: String,
    val venue: String,
    val event_date: String,
    val category: String
)

data class ServiceJson(
    val service_id: String,
    val name: String,
    val description: String,
    val location: String,
    val operating_hours: String,
    val contact: String
)

data class TransportJson(
    val route_id: String,
    val route_name: String,
    val pickup_point: String,
    val destination: String,
    val schedule: String,
    val next_shuttle: String,
    val status: String
)

data class SafetyJson(
    val safety_id: String,
    val title: String,
    val description: String,
    val contact: String,
    val location: String,
    val emergency_number: String
)

// JSON Converter Utility - Converts JSON response into Kotlin data objects
object JsonConverter {
    internal val gson = Gson()

    // Generic JSON -> Kotlin object conversion (used for all API endpoints)
    inline fun <reified T> fromJson(jsonString: String): T {
        return Gson().fromJson(jsonString, T::class.java)
    }

    inline fun <reified T> toJson(obj: T): String {
        return Gson().toJson(obj)
    }

    // Example: Convert assignment JSON (from 6.3) to Kotlin object
    fun parseAssignment(jsonString: String): AssignmentJson {
        return fromJson(jsonString)
    }

    // Example: List conversion for GET /api/announcements, /api/events, etc.
    inline fun <reified T> fromJsonArray(jsonString: String): List<T> {
        val listType = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, T::class.java).type
        return Gson().fromJson(jsonString, listType)
    }

    // Firestore Document -> JSON -> Kotlin object (NoSQL implementation)
    fun firestoreToJson(documentData: Map<String, Any>): String {
        return Gson().toJson(documentData)
    }

    // For POE demonstration: Show JSON conversion in logs
    fun demonstrate63() {
        // 6.3 Example JSON from POE document
        val jsonExample = """
        {
          "id": 15,
          "title": "Database POE",
          "module": "Database Systems",
          "dueDate": "2026-09-10",
          "priority": "High",
          "completed": false
        }
        """.trimIndent()

        // Convert JSON response into Kotlin data object (POE requirement)
        val kotlinObject = fromJson<AssignmentJson>(jsonExample)

        // Now kotlinObject is a Kotlin data class instance:
        // AssignmentJson(id=15, title="Database POE", module="Database Systems", dueDate="2026-09-10", priority="High", completed=false)
        println("Converted JSON to Kotlin: $kotlinObject")

        // Convert back to JSON if needed (for POST/PUT requests)
        val backToJson = toJson(kotlinObject)
        println("Converted Kotlin to JSON: $backToJson")
    }
}

// 6.4 API Endpoints - JSON request/response examples

object ApiEndpointsDocs {
    // POST /api/register - Register a student
    const val REGISTER_JSON = """
    {
      "first_name": "Keoagile",
      "surname": "Letlape",
      "student_number": "ST10172741",
      "email": "ST10172741@iie.com",
      "password": "bcrypt_encrypted_password"
    }
    """

    // POST /api/login - Authenticate a student
    const val LOGIN_JSON = """
    {
      "email": "ST10172741@iie.com",
      "password": "bcrypt_encrypted_password"
    }
    """

    // GET /api/announcements - Retrieve announcements (4.10 Purpose)
    const val ANNOUNCEMENTS_JSON = """
    [
      {
        "announcement_id": "ANN001",
        "title": "Registration closes Friday",
        "content": "Final registration for Semester 2 closes on 23 Aug 2025 - 17:00",
        "date_created": "2025-08-20",
        "priority": "IMPORTANT",
        "category": "Academic"
      },
      {
        "announcement_id": "ANN002",
        "title": "Test timetable updated",
        "content": "Updated test schedule now available for Engineering Dept",
        "date_created": "2025-08-19",
        "priority": "Academic Notice",
        "category": "Academic"
      }
    ]
    """

    // GET /api/transport - Retrieve transport information (4.8 Purpose)
    const val TRANSPORT_JSON = """
    [
      {
        "route_id": "R001",
        "route_name": "Route A",
        "pickup_point": "Main Gate",
        "destination": "Residence",
        "schedule": "Every 30 mins",
        "next_shuttle": "14:30",
        "status": "ACTIVE"
      }
    ]
    """
}
