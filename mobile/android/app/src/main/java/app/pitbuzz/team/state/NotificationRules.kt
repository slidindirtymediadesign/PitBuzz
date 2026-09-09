package app.pitbuzz.team.state
enum class PushType{ANNOUNCEMENT,PRIVATE_MESSAGE,RACE_GET_READY,RACE_STAGING}
object NotificationRules{fun route(t:PushType)=if(t==PushType.PRIVATE_MESSAGE)"Messages" else if(t==PushType.ANNOUNCEMENT)"Announcements" else "Home";fun mayAutoSpeak(t:PushType)=t!=PushType.PRIVATE_MESSAGE}
