plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android { namespace="app.pitbuzz.team"; compileSdk=35; defaultConfig { applicationId="app.pitbuzz.team"; minSdk=26; targetSdk=35; versionCode=1; versionName="0.1.0-beta" } }
dependencies { implementation("androidx.appcompat:appcompat:1.7.0"); implementation("androidx.activity:activity-ktx:1.10.0"); implementation("com.google.firebase:firebase-messaging:24.1.0") }
