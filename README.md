# CampusConnectSA - ST10172741 K LETLAPE

## ✅ REST API Status
CONNECTED TO SUPABASE REST API (visible in app UI)

## Features for POE Part 2 Final

### 1. REST API Integration
- Supabase REST API: https://YOUR-PROJECT.supabase.co
- Endpoints: /rest/v1/events, /rest/v1/users
- Visible status: `✅ CONNECTED TO SUPABASE REST API` in MainActivity
- API Key secured in local.properties

### 2. Authentication with Encrypted Storage
- Register / Login with Supabase Auth
- EncryptedSharedPreferences for password token storage
- Validation: email format, password strength

### 3. WiFi / Connectivity Check
- Router WiFi OK check before API calls
- Exponential backoff retry: 1s, 2s, 4s
- Offline banner + Room cache fallback

### 4. Offline Cache
- Room Database for events
- DAO: getAllEvents(), insertEvents()
- Sync on reconnect

### 5. Events with 2+2+2 Filter + Supabase Sync
- Filter 1: Category (Academic, Social, Sports)
- Filter 2: Date (Today, This Week, All)
- Filter 3: Campus Location
- Pull from Supabase REST + local cache
- CRUD: Create event -> POST to Supabase

## APK Download
Download APK from Releases:
https://github.com/ST10172741-K-LETLAPE/CampusConnectSA/releases/tag/v1.0

File: CampusConnectSA.apk (11.9 MB)

## How to Run
1. Clone: `git clone https://github.com/ST10172741-K-LETLAPE/CampusConnectSA.git`
2. Open in Android Studio
3. Add Supabase URL + Key in local.properties:
