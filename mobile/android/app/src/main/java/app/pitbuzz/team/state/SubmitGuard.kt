package app.pitbuzz.team.state
class SubmitGuard{private val p=mutableSetOf<String>();fun begin(k:String)=synchronized(p){if(k in p)false else{p+=k;true}};fun finish(k:String)=synchronized(p){p.remove(k)}}
