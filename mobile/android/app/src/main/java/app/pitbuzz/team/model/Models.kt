package app.pitbuzz.team.model
data class RaceSlot(val raceNumber:Int?,val title:String?)
data class RaceStatus(val current:RaceSlot?,val staging:RaceSlot?,val getReady:RaceSlot?,val stale:Boolean=false)
data class Announcement(val id:Long,val title:String,val body:String,val sentAt:String,val isRead:Boolean=false)
data class PrivateMessage(val id:Long,val body:String,val sentAt:String,val isRead:Boolean=false)
data class Rider(val id:Long,val name:String,val raceNumber:String?)
