package app.pitbuzz.team.network
import android.content.Context
import java.net.HttpURLConnection
import java.net.URL

data class ApiResponse(val code:Int,val body:String){ val ok:Boolean get()=code in 200..299 }
class ApiClient(ctx:Context, private val base:String="https://team.pitbuzz.app/") {
 private val sessions=SessionStore(ctx)
 var token: String?
  get() = sessions.load()
  set(value) { sessions.save(value) }
 fun get(route:String)=request("GET",route,null)
 fun post(route:String,json:String)=request("POST",route,json)
 private fun request(method:String,route:String,json:String?):ApiResponse{
  val c=URL(base+route).openConnection() as HttpURLConnection
  c.requestMethod=method;c.connectTimeout=10000;c.readTimeout=15000;c.setRequestProperty("Accept","application/json")
  c.setRequestProperty("X-PitBuzz-Platform","android");c.setRequestProperty("X-PitBuzz-Version","0.1.0-beta")
  token?.let{c.setRequestProperty("Authorization","Bearer $it")}
  if(json!=null){c.doOutput=true;c.setRequestProperty("Content-Type","application/json; charset=utf-8");c.outputStream.use{it.write(json.toByteArray())}}
  return try{val code=c.responseCode;val s=if(code in 200..299)c.inputStream else c.errorStream;ApiResponse(code,s?.bufferedReader()?.use{it.readText()}?:"")}finally{c.disconnect()}
 }
}
