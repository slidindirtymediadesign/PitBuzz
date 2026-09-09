package app.pitbuzz.team
import com.google.firebase.messaging.*
class PitBuzzMessagingService:FirebaseMessagingService(){override fun onNewToken(token:String){};override fun onMessageReceived(message:RemoteMessage){}}
