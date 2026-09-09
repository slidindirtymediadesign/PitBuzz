package app.pitbuzz.team.ui
import android.app.Activity
enum class Screen{LOGIN,REGISTER,RECOVERY,HOME,ANNOUNCEMENTS,MESSAGES,AUDIO,SETTINGS,TEAM,RACE_CONTROL}
class PitBuzzNavigator(private val a:Activity){fun show(s:Screen){a.setContentView(when(s){Screen.LOGIN->PitBuzzScreens.login(a);Screen.REGISTER->PitBuzzScreens.register(a);Screen.RECOVERY->PitBuzzScreens.recovery(a);Screen.HOME->PitBuzzScreens.home(a);Screen.ANNOUNCEMENTS->PitBuzzScreens.announcements(a);Screen.MESSAGES->PitBuzzScreens.messages(a);Screen.AUDIO->PitBuzzScreens.audio(a);Screen.SETTINGS->PitBuzzScreens.settings(a);Screen.TEAM->PitBuzzScreens.team(a);Screen.RACE_CONTROL->PitBuzzScreens.raceControl(a)})}}
