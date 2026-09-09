package app.pitbuzz.team
import android.app.*;import android.content.Context
object NotificationChannels{fun create(c:Context){if(android.os.Build.VERSION.SDK_INT>=26)c.getSystemService(NotificationManager::class.java).createNotificationChannels(listOf(NotificationChannel("pitbuzz_announcements","Announcements",4),NotificationChannel("pitbuzz_private_messages","Private Messages",4),NotificationChannel("pitbuzz_race_alerts","Race Alerts",4)))}}
