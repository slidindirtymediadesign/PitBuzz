package app.pitbuzz.team
import android.app.*
import android.content.Context
import android.media.AudioAttributes
object NotificationChannels{
 const val ANNOUNCEMENTS="pitbuzz_announcements"; const val PRIVATE="pitbuzz_private_messages"; const val RACE="pitbuzz_race_alerts"
 fun create(c:Context){if(android.os.Build.VERSION.SDK_INT>=26){val nm=c.getSystemService(NotificationManager::class.java);val a=NotificationChannel(ANNOUNCEMENTS,"Announcements",NotificationManager.IMPORTANCE_HIGH);a.description="PitBuzz race-day announcements";val p=NotificationChannel(PRIVATE,"Private Messages",NotificationManager.IMPORTANCE_HIGH);p.description="Private PitBuzz messages; message text is never spoken automatically";val r=NotificationChannel(RACE,"Race Alerts",NotificationManager.IMPORTANCE_HIGH);r.description="Race, staging and get-ready alerts";nm.createNotificationChannels(listOf(a,p,r))}}
}
