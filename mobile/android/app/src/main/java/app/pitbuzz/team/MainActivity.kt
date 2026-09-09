package app.pitbuzz.team
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.pitbuzz.team.ui.*
class MainActivity:AppCompatActivity(){override fun onCreate(s:Bundle?){super.onCreate(s);NotificationChannels.create(this);PitBuzzNavigator(this).show(Screen.LOGIN)}}
