package app.pitbuzz.team.ui
import android.app.Activity
enum class Screen{LOGIN,REGISTER,RECOVERY,HOME,ANNOUNCEMENTS,MESSAGES,AUDIO,SETTINGS,TEAM,RACE_CONTROL}
class PitBuzzNavigator(private val a:Activity){fun show(s:Screen){a.setContentView(PitBuzzScreens.render(a,this,s))}}
