package app.pitbuzz.team
import android.app.*
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.*
import app.pitbuzz.team.network.*
import org.json.JSONObject
import java.util.concurrent.Executors
class PitBuzzMessagingService:FirebaseMessagingService(){
 override fun onNewToken(token:String){val io=Executors.newSingleThreadExecutor();io.execute{try{val api=ApiClient(this);if(api.token!=null)api.post("api/v1/push-subscription.php",JSONObject().put("deviceKey",token).put("token",token).toString())}catch(_:Exception){}finally{io.shutdown()}}}
 override fun onMessageReceived(m:RemoteMessage){
  val type=m.data["type"]?:"announcement";val isPrivate=type=="private_message";val channel=if(isPrivate)NotificationChannels.PRIVATE else if(type=="race_alert")NotificationChannels.RACE else NotificationChannels.ANNOUNCEMENTS
  val title=m.notification?.title?:m.data["title"]?:if(isPrivate)"Private Message" else "PitBuzz"
  val body=m.notification?.body?:m.data["body"]?:"New PitBuzz update"
  val intent=Intent(this,MainActivity::class.java).apply{flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP;putExtra("open",if(isPrivate)"messages" else if(type=="race_alert")"home" else "announcements")}
  val pi=PendingIntent.getActivity(this,(System.currentTimeMillis()%Int.MAX_VALUE).toInt(),intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val note=NotificationCompat.Builder(this,channel).setSmallIcon(R.drawable.ic_stat_pitbuzz).setContentTitle(title).setContentText(body).setStyle(NotificationCompat.BigTextStyle().bigText(body)).setAutoCancel(true).setContentIntent(pi).setPriority(NotificationCompat.PRIORITY_HIGH).build()
  getSystemService(NotificationManager::class.java).notify((System.currentTimeMillis()%Int.MAX_VALUE).toInt(),note)
 }
}
