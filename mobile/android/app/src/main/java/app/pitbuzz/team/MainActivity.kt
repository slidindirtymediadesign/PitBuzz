package app.pitbuzz.team

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.pitbuzz.team.network.*
import org.json.JSONObject
import java.util.concurrent.Executors

class MainActivity:AppCompatActivity(){
 private val io=Executors.newSingleThreadExecutor(); private lateinit var api:ApiClient; private var currentUser:JSONObject?=null
 private val navy=Color.rgb(7,47,82); private val red=Color.rgb(198,32,40); private val blue=Color.rgb(18,103,171); private val muted=Color.rgb(92,101,110)
 private val cache by lazy{getSharedPreferences("pitbuzz_cache",MODE_PRIVATE)}
 override fun onCreate(s:Bundle?){
  super.onCreate(s); NotificationChannels.create(this); api=ApiClient(this)
  if(android.os.Build.VERSION.SDK_INT>=33 && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),2001)
  if(api.token!=null)resumeSession() else showLogin()
 }
 override fun onNewIntent(intent:android.content.Intent){super.onNewIntent(intent);when(intent.getStringExtra("open")){"messages"->if(api.token!=null)showMessages();"announcements"->if(api.token!=null)showAnnouncements()}}

 private fun dp(n:Int)=(n*resources.displayMetrics.density).toInt()
 private fun rounded(color:Int,radius:Int=14,stroke:Int=0,strokeColor:Int=Color.TRANSPARENT)=GradientDrawable().apply{shape=GradientDrawable.RECTANGLE;cornerRadius=dp(radius).toFloat();setColor(color);if(stroke>0)setStroke(dp(stroke),strokeColor)}
 private fun gap(v:LinearLayout,h:Int=10){v.addView(Space(this),LinearLayout.LayoutParams(1,dp(h)))}
 private fun root(scroll:Boolean=true):LinearLayout{
  val body=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(8),dp(18),dp(12));setBackgroundColor(Color.rgb(248,250,252))}
  if(scroll){val sv=ScrollView(this).apply{isFillViewport=true;setBackgroundColor(Color.rgb(248,250,252))};sv.addView(body,ScrollView.LayoutParams(-1,-2));setContentView(sv)}else setContentView(body);return body
 }
 private fun txt(s:String,size:Float=16f,bold:Boolean=false)=TextView(this).apply{text=s;textSize=size;setTextColor(navy);setPadding(dp(4),dp(5),dp(4),dp(5));if(bold)setTypeface(Typeface.DEFAULT,Typeface.BOLD)}
 private fun field(h:String,password:Boolean=false)=EditText(this).apply{
  hint=h;minHeight=dp(52);setPadding(dp(14),0,dp(14),0);setTextColor(navy);setHintTextColor(Color.rgb(126,136,146));textSize=15f;background=rounded(Color.WHITE,12,1,Color.rgb(214,221,228));
  inputType=if(password)InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT
  layoutParams=LinearLayout.LayoutParams(-1,dp(54)).apply{setMargins(0,dp(5),0,dp(5))}
 }
 private fun btn(s:String,primary:Boolean=true,action:()->Unit)=Button(this).apply{
  text=s;minHeight=dp(50);isAllCaps=false;textSize=15f;setTypeface(Typeface.DEFAULT,Typeface.BOLD);stateListAnimator=null;setPadding(dp(12),0,dp(12),0)
  setTextColor(if(primary)Color.WHITE else navy);background=if(primary)rounded(red,12) else rounded(Color.WHITE,12,1,Color.rgb(207,216,224));setOnClickListener{action()}
  layoutParams=LinearLayout.LayoutParams(-1,dp(52)).apply{setMargins(0,dp(5),0,dp(5))}
 }
 private fun card():LinearLayout=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(14),dp(12),dp(14),dp(12));background=rounded(Color.WHITE,14,1,Color.rgb(223,229,235));elevation=dp(1).toFloat();layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(6),0,dp(8))}}
 private fun header(v:LinearLayout){
  val row=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(0,dp(2),0,dp(4))}
  val id=resources.getIdentifier("natva_logo","drawable",packageName);if(id!=0)row.addView(ImageView(this).apply{setImageResource(id);adjustViewBounds=true;scaleType=ImageView.ScaleType.CENTER_INSIDE},LinearLayout.LayoutParams(-1,dp(76)))
  row.addView(txt("OFFICIAL RACE COMMUNICATION",10f,true).apply{gravity=Gravity.CENTER;letterSpacing=.08f;setTextColor(navy);setPadding(0,0,0,dp(2))})
  v.addView(row)
 }
 private fun footer(v:LinearLayout){
  gap(v,12);val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.END or Gravity.CENTER_VERTICAL}
  val id=resources.getIdentifier("pitbuzz_logo","drawable",packageName);if(id!=0)row.addView(ImageView(this).apply{setImageResource(id);adjustViewBounds=true;scaleType=ImageView.ScaleType.CENTER_INSIDE},LinearLayout.LayoutParams(dp(112),dp(36)))
  else row.addView(txt("PitBuzz © 2026",11f,true).apply{setTextColor(muted)})
  v.addView(row)
 }
 private fun jsonError(body:String,fallback:String):String=try{JSONObject(body).optString("message",fallback)}catch(_:Exception){fallback}
 private fun network(block:()->ApiResponse,done:(ApiResponse)->Unit){io.execute{try{val r=block();runOnUiThread{done(r)}}catch(e:Exception){runOnUiThread{toast("Cannot reach PitBuzz server")}}}}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
 private fun showLogin(){
  val v=root();header(v);gap(v,6)
  val c=card();c.addView(txt("Welcome to PitBuzz",24f,true));c.addView(txt("Race-day communication for riders, families and crews.",14f).apply{setTextColor(muted)})
  gap(c,5);val login=field("Phone number or email");val pw=field("Password",true);c.addView(login);c.addView(pw)
  c.addView(CheckBox(this).apply{text="Show password";setTextColor(muted);buttonTintList=android.content.res.ColorStateList.valueOf(blue);setOnCheckedChangeListener{_,x->pw.inputType=InputType.TYPE_CLASS_TEXT or if(x)InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD;pw.setSelection(pw.text.length)}})
  c.addView(btn("Log In"){if(login.text.isBlank()||pw.text.isBlank()){toast("Enter your phone/email and password");return@btn};setBusy(true);network({api.post(ApiRoutes.LOGIN,JSONObject().put("login",login.text.toString().trim()).put("password",pw.text.toString()).toString())}){r->setBusy(false);if(!r.ok)toast(jsonError(r.body,"Login failed")) else {val j=JSONObject(r.body);api.token=j.getString("token");showHome(j.getJSONObject("user"))}}})
  c.addView(btn("Forgot Password?",false){showRecovery()});c.addView(btn("Create Account",false){showRegister()});v.addView(c);footer(v)
 }
 private fun showRegister(){val v=root();header(v);v.addView(txt("Create Account",25f,true));val name=field("Name");val p1=field("Phone Number");val p2=field("Confirm Phone Number");val email=field("Email");val pw=field("Password",true);val pw2=field("Confirm Password",true);listOf(name,p1,p2,email,pw,pw2).forEach(v::addView);v.addView(CheckBox(this).apply{text="Show passwords";setOnCheckedChangeListener{_,x->val t=InputType.TYPE_CLASS_TEXT or if(x)InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD;pw.inputType=t;pw2.inputType=t}});v.addView(btn("Create Account"){val a=p1.text.toString().filter(Char::isDigit).removePrefix("1");val b=p2.text.toString().filter(Char::isDigit).removePrefix("1");when{ name.text.isBlank()->toast("Name is required");a.length!=10->toast("Enter a valid 10-digit phone number");a!=b->toast("Phone numbers do not match");!email.text.contains("@")->toast("Enter a valid email");pw.text.length<8->toast("Password must be at least 8 characters");pw.text.toString()!=pw2.text.toString()->toast("Passwords do not match");else->{setBusy(true);val j=JSONObject().put("name",name.text.toString().trim()).put("phone",a).put("email",email.text.toString().trim()).put("password",pw.text.toString());network({api.post(ApiRoutes.REGISTER,j.toString())}){r->setBusy(false);if(!r.ok)toast(jsonError(r.body,"Registration failed"))else{val x=JSONObject(r.body);api.token=x.getString("token");toast(if(x.getJSONObject("user").optString("systemRole")=="super_admin")"First account created as Super Admin" else "Account created");showHome(x.getJSONObject("user"))}}}}});v.addView(btn("Back to Sign In",false){showLogin()});footer(v)}
 private fun showRecovery(){
  val v=root();header(v);v.addView(txt("Account Recovery",25f,true));v.addView(txt("Enter the phone number or email on your PitBuzz account. A 6-digit code will be sent to the account email."))
  val login=field("Phone number or email");v.addView(login)
  v.addView(btn("Send Reset Code"){
   if(login.text.isBlank()){toast("Enter your phone number or email");return@btn}
   val payload=JSONObject().put("login",login.text.toString().trim())
   network({api.post(ApiRoutes.RESET_REQUEST,payload.toString())}){r->if(r.ok){toast(jsonError(r.body,"Reset code requested"));showResetConfirm(login.text.toString().trim())}else toast(jsonError(r.body,"Recovery request failed"))}
  });v.addView(btn("Back to Sign In",false){showLogin()});footer(v)
 }
 private fun showResetConfirm(loginValue:String){
  val v=root();header(v);v.addView(txt("Enter Reset Code",25f,true));val code=field("6-digit code");val pw=field("New password",true);val pw2=field("Confirm new password",true);v.addView(code);v.addView(pw);v.addView(pw2)
  v.addView(btn("Update Password"){
   if(!code.text.toString().matches(Regex("\\d{6}"))){toast("Enter the 6-digit code");return@btn};if(pw.text.length<8){toast("Password must be at least 8 characters");return@btn};if(pw.text.toString()!=pw2.text.toString()){toast("Passwords do not match");return@btn}
   val j=JSONObject().put("login",loginValue).put("code",code.text.toString()).put("password",pw.text.toString());network({api.post(ApiRoutes.RESET_CONFIRM,j.toString())}){r->if(r.ok){toast("Password updated");showLogin()}else toast(jsonError(r.body,"Password reset failed"))}
  });v.addView(btn("Cancel",false){showLogin()});footer(v)
 }
 private fun resumeSession(){network({api.get(ApiRoutes.SESSION)}){r->if(r.ok)showHome(JSONObject(r.body).getJSONObject("user"))else{api.token=null;showLogin()}}}
 private fun raceBar(v:LinearLayout){
  val shell=card().apply{setPadding(dp(10),dp(8),dp(10),dp(8));background=rounded(Color.WHITE,14,1,Color.rgb(217,224,231))}
  val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  top.addView(txt("RACE STATUS",11f,true).apply{letterSpacing=.08f},LinearLayout.LayoutParams(0,-2,1f))
  val status=txt("CONNECTED",10f,true).apply{gravity=Gravity.END;setTextColor(Color.rgb(23,143,72))};top.addView(status)
  shell.addView(top)
  val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f;background=rounded(navy,11)}
  val values=mutableListOf<TextView>();val cached=listOf(cache.getString("race_current","—")?:"—",cache.getString("race_staging","—")?:"—",cache.getString("race_ready","—")?:"—")
  listOf("RACE #","STAGING","GET READY").forEachIndexed{i,label->row.addView(LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(dp(2),dp(7),dp(2),dp(7));addView(txt(label,9f,true).apply{gravity=Gravity.CENTER;setTextColor(Color.rgb(205,218,231));setPadding(0,0,0,0)});val value=txt(cached[i],21f,true).apply{gravity=Gravity.CENTER;setTextColor(Color.WHITE);setPadding(0,0,0,0)};values.add(value);addView(value)},LinearLayout.LayoutParams(0,dp(60),1f))}
  shell.addView(row);v.addView(shell)
  network({api.get(ApiRoutes.RACE_STATUS)}){r->if(r.ok){val j=JSONObject(r.body);val c=j.optString("currentRace","—").ifBlank{"—"};val st=j.optString("stagingRace","—").ifBlank{"—"};val gr=j.optString("getReadyRace","—").ifBlank{"—"};values[0].text=c;values[1].text=st;values[2].text=gr;cache.edit().putString("race_current",c).putString("race_staging",st).putString("race_ready",gr).putLong("race_updated",System.currentTimeMillis()).apply();status.text="CONNECTED";status.setTextColor(Color.rgb(23,143,72))}else{status.text="OFFLINE • LAST KNOWN";status.setTextColor(red)}}
 }
 private fun heartbeat(){network({api.post(ApiRoutes.HEARTBEAT,JSONObject().put("platform","android").put("version","0.1.0-beta").toString())}){}}
 private fun showHome(user:JSONObject){
  currentUser=user;heartbeat();val v=root();header(v);raceBar(v)
  val connected=card().apply{background=rounded(Color.rgb(234,250,239),12,1,Color.rgb(181,229,196));setPadding(dp(12),dp(8),dp(12),dp(8))}
  connected.addView(txt("✓  CONNECTED",14f,true).apply{setTextColor(Color.rgb(20,132,62));setPadding(0,0,0,0)});connected.addView(txt("You will receive official race announcements",12f).apply{setTextColor(Color.rgb(57,104,71));setPadding(0,0,0,0)});v.addView(connected)
  v.addView(txt("Welcome, ${user.optString("name","Rider")}",22f,true))
  val latestCard=card();val head=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL};head.addView(txt("LATEST ANNOUNCEMENT",15f,true).apply{setTextColor(red)},LinearLayout.LayoutParams(0,-2,1f));head.addView(txt("VIEW ALL ›",11f,true).apply{setTextColor(blue);setOnClickListener{showAnnouncements()}});latestCard.addView(head)
  val latest=txt("Loading…",15f).apply{setTextColor(Color.rgb(36,45,54))};latestCard.addView(latest);v.addView(latestCard)
  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->latest.text=if(r.ok){val a=JSONObject(r.body).optJSONArray("announcements");if(a!=null&&a.length()>0){val x=a.getJSONObject(0);val title=x.optString("title").ifBlank{"Official Announcement"};title.uppercase()+"
"+x.optString("body")}else"No announcements yet."}else"Announcements unavailable."}
  v.addView(btn("My Team",false){showTeam()})
  val role=user.optString("systemRole","user")
  if(role in listOf("announcer","admin","super_admin")){v.addView(txt("OFFICIAL TOOLS",12f,true).apply{setTextColor(muted);letterSpacing=.08f});v.addView(btn("Send Announcement"){showAnnouncementComposer()});v.addView(btn("Race Control",false){showRaceControl()})}
  if(role=="super_admin")v.addView(btn("Admin Roles",false){showAdminRoles()})
  v.addView(bottomNav("Home"));footer(v)
 }
 private fun bottomNav(active:String):LinearLayout{
  val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=5f;setPadding(0,dp(8),0,0);background=rounded(Color.WHITE,14,1,Color.rgb(220,226,232))}
  val icons=mapOf("Home" to "⌂","Announcements" to "!","Messages" to "✉","Audio" to "◉","Settings" to "⚙")
  listOf("Home","Announcements","Messages","Audio","Settings").forEach{n->row.addView(TextView(this).apply{text=(icons[n]?:"")+"
"+n;textSize=10f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,if(n==active)Typeface.BOLD else Typeface.NORMAL);setTextColor(if(n==active)red else navy);setPadding(dp(1),dp(6),dp(1),dp(6));setOnClickListener{when(n){"Home"->resumeSession();"Announcements"->showAnnouncements();"Messages"->showMessages();"Audio"->showAudio();"Settings"->showSettings()}}},LinearLayout.LayoutParams(0,dp(58),1f))}
  return row
 }
 private fun showAnnouncements(){
  val v=root();header(v);raceBar(v);v.addView(txt("Announcements",24f,true));val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};v.addView(box)
  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->box.removeAllViews();if(r.ok){val a=JSONObject(r.body).optJSONArray("announcements");if(a==null||a.length()==0)box.addView(card().apply{addView(txt("No announcements yet.",15f))})else for(i in 0 until a.length()){val x=a.getJSONObject(i);val c=card();c.addView(txt(x.optString("title","Official Announcement"),17f,true).apply{setTextColor(if(x.optInt("isCritical",0)==1)red else navy)});c.addView(txt(x.optString("body"),15f).apply{setTextColor(Color.rgb(47,56,65))});val meta=listOf(x.optString("senderName"),x.optString("createdAt")).filter{it.isNotBlank()}.joinToString(" • ");if(meta.isNotBlank())c.addView(txt(meta,11f).apply{setTextColor(muted)});box.addView(c)}}else box.addView(card().apply{addView(txt("Unable to load announcements."))})}
  v.addView(bottomNav("Announcements"));footer(v)
 }
 private fun showMessages(){
  val v=root();header(v);raceBar(v);val title=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL};title.addView(txt("Messages",24f,true),LinearLayout.LayoutParams(0,-2,1f));title.addView(btn("New",false){composeMessage()},LinearLayout.LayoutParams(dp(90),dp(48)));v.addView(title);v.addView(txt("Private messages never auto-play aloud.",12f).apply{setTextColor(muted)})
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};v.addView(box)
  network({api.get(ApiRoutes.MESSAGES)}){r->box.removeAllViews();if(r.ok){val a=JSONObject(r.body).optJSONArray("messages");if(a==null||a.length()==0)box.addView(card().apply{addView(txt("No private messages yet."))})else for(i in 0 until a.length()){val x=a.getJSONObject(i);val c=card();c.addView(txt(x.optString("senderName","PitBuzz User"),16f,true));c.addView(txt(x.optString("body"),14f).apply{setTextColor(Color.rgb(48,57,66))});val whenSent=x.optString("createdAt");if(whenSent.isNotBlank())c.addView(txt(whenSent,11f).apply{gravity=Gravity.END;setTextColor(muted)});box.addView(c)}}else box.addView(card().apply{addView(txt("Unable to load messages."))})}
  v.addView(bottomNav("Messages"));footer(v)
 }
 private fun composeMessage(){
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),0,dp(18),0)}
  val q=field("Recipient name or phone");val body=field("Private message");wrap.addView(q);wrap.addView(body)
  android.app.AlertDialog.Builder(this).setTitle("New Private Message").setView(wrap).setPositiveButton("Find & Send"){_,_->
   val query=q.text.toString().trim();val text=body.text.toString().trim();if(query.length<2||text.isBlank()){toast("Enter a recipient and message");return@setPositiveButton}
   network({api.get(ApiRoutes.USERS+"?q="+java.net.URLEncoder.encode(query,"UTF-8"))}){r->
    if(!r.ok){toast("Recipient search failed");return@network};val a=JSONObject(r.body).optJSONArray("users");if(a==null||a.length()==0){toast("No matching PitBuzz user");return@network}
    val u=a.getJSONObject(0);val payload=JSONObject().put("recipientUserId",u.getInt("id")).put("body",text)
    network({api.post(ApiRoutes.MESSAGES,payload.toString())}){sr->toast(if(sr.ok)"Private message sent to ${u.optString("name")}" else jsonError(sr.body,"Message failed"));if(sr.ok)showMessages()}
   }
  }.setNegativeButton("Cancel",null).show()
 }
 private fun showAudio(){
  val v=root();header(v);raceBar(v);v.addView(txt("Audio",24f,true));val c=card();val autoplay=Switch(this).apply{text="Auto-play race announcements";setTextColor(navy)};val sound=Switch(this).apply{text="Notification sound";setTextColor(navy)};c.addView(autoplay);c.addView(sound);c.addView(txt("Private messages are never spoken automatically.",12f).apply{setTextColor(muted)});v.addView(c)
  network({api.get(ApiRoutes.SETTINGS)}){r->if(r.ok){val j=JSONObject(r.body);autoplay.isChecked=j.optInt("announcementAutoplay",1)==1;sound.isChecked=j.optInt("notificationSound",1)==1}}
  v.addView(btn("Save Audio Settings"){val j=JSONObject().put("announcementAutoplay",autoplay.isChecked).put("notificationSound",sound.isChecked);network({api.post(ApiRoutes.SETTINGS,j.toString())}){r->toast(if(r.ok)"Audio settings saved" else jsonError(r.body,"Could not save settings"))}});v.addView(bottomNav("Audio"));footer(v)
 }
 private fun showSettings(){
  val v=root();header(v);raceBar(v);v.addView(txt("Settings",24f,true));val c=card();c.addView(txt("Account",15f,true));c.addView(txt(currentUser?.optString("name","PitBuzz User")?:"PitBuzz User",15f));c.addView(txt(currentUser?.optString("email","")?:"",12f).apply{setTextColor(muted)});v.addView(c);v.addView(btn("Sign Out",false){network({api.post(ApiRoutes.LOGOUT,"{}")}){_->api.token=null;showLogin()}});v.addView(bottomNav("Settings"));footer(v)
 }
 private fun showTeam(){
  val v=root(); header(v); raceBar(v); v.addView(txt("My Team",25f,true))
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}; v.addView(box)
  network({api.get(ApiRoutes.TEAM)}){r->
   box.removeAllViews()
   if(!r.ok){ box.addView(txt("Unable to load team.")); return@network }
   val j=JSONObject(r.body)
   if(j.isNull("team")){
    box.addView(txt("You are not on a team yet."))
    box.addView(btn("Create My Team"){createTeam()})
   } else {
    val team=j.getJSONObject("team")
    box.addView(txt(team.optString("name","My Team"),21f,true))
    box.addView(txt("Riders",17f,true))
    val riders=j.optJSONArray("riders")
    if(riders!=null){
     for(i in 0 until riders.length()){
      val x=riders.getJSONObject(i)
      box.addView(txt("${x.optString("name")}  #${x.optString("raceNumber")}"))
     }
    }
    box.addView(txt("Family / Crew",17f,true))
    val members=j.optJSONArray("members")
    if(members!=null){for(i in 0 until members.length()){val m=members.getJSONObject(i);box.addView(txt("${m.optString("name")} — ${m.optString("role")}"))}}
    if(j.optBoolean("canManage")){box.addView(btn("Add Rider",false){addRider()});box.addView(btn("Add Family / Crew",false){addMember()})}
   }
  }
  v.addView(btn("Back Home",false){resumeSession()}); footer(v)
 }
 private fun createTeam(){val input=field("Team name");android.app.AlertDialog.Builder(this).setTitle("Create Team").setView(input).setPositiveButton("Create"){_,_->val n=input.text.toString().trim();if(n.isNotBlank())network({api.post(ApiRoutes.TEAM,JSONObject().put("action","create_team").put("name",n).toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not create team"))}}.setNegativeButton("Cancel",null).show()}
 private fun addRider(){val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Rider name");val num=field("Race number");wrap.addView(n);wrap.addView(num);android.app.AlertDialog.Builder(this).setTitle("Add Rider").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isNotBlank())network({api.post(ApiRoutes.TEAM,JSONObject().put("action","add_rider").put("name",n.text.toString().trim()).put("raceNumber",num.text.toString().trim()).toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add rider"))}}.setNegativeButton("Cancel",null).show()}
 private fun addMember(){
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Name");val ph=field("Phone number (optional)");val role=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("Family","Crew"))};wrap.addView(n);wrap.addView(ph);wrap.addView(role)
  android.app.AlertDialog.Builder(this).setTitle("Add Family / Crew").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isBlank()){toast("Name is required");return@setPositiveButton};val j=JSONObject().put("action","add_member").put("name",n.text.toString().trim()).put("phone",ph.text.toString().trim()).put("role",if(role.selectedItemPosition==0)"family" else "crew").put("followsAllRiders",true);network({api.post(ApiRoutes.TEAM,j.toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add member"))}}.setNegativeButton("Cancel",null).show()
 }
 private fun showAnnouncementComposer(){
  val v=root();header(v);raceBar(v);v.addView(txt("Send Announcement",25f,true))
  val target=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("Everyone","Class","Rider"))}
  val title=field("Title");val body=field("Announcement text");val priority=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("Normal","Important","Urgent"))};val audio=CheckBox(this).apply{text="Audio enabled";isChecked=true}
  listOf<View>(target,title,body,priority,audio).forEach(v::addView)
  v.addView(btn("Send Announcement"){
   if(title.text.isBlank()||body.text.isBlank()){toast("Title and announcement text are required");return@btn}
   val j=JSONObject().put("targetType",listOf("all","class","rider")[target.selectedItemPosition]).put("title",title.text.toString().trim()).put("body",body.text.toString().trim()).put("priority",listOf("normal","important","urgent")[priority.selectedItemPosition]).put("audioEnabled",audio.isChecked)
   network({api.post(ApiRoutes.ANNOUNCEMENTS,j.toString())}){r->if(r.ok){toast("Announcement sent");showAnnouncements()}else toast(jsonError(r.body,"Announcement failed"))}
  });v.addView(btn("Back Home",false){resumeSession()});footer(v)
 }
 private fun showRaceControl(){
  val v=root();header(v);raceBar(v);v.addView(txt("Race Control",25f,true));v.addView(txt("Set the three live race slots. ADVANCE will be enabled when the event race program is imported.",14f))
  val cur=field("RACE #");val stage=field("STAGING");val ready=field("GET READY");listOf(cur,stage,ready).forEach(v::addView)
  v.addView(btn("ADVANCE"){
   network({api.post(ApiRoutes.RACE_STATUS,JSONObject().put("action","advance").toString())}){r->toast(if(r.ok)"Race advanced" else jsonError(r.body,"Could not advance race"));if(r.ok)showRaceControl()}
  })
  v.addView(btn("Set / Correct",false){
   val j=JSONObject().put("action","set").put("currentRace",cur.text.toString().trim().ifBlank{JSONObject.NULL}).put("stagingRace",stage.text.toString().trim().ifBlank{JSONObject.NULL}).put("getReadyRace",ready.text.toString().trim().ifBlank{JSONObject.NULL})
   network({api.post(ApiRoutes.RACE_STATUS,j.toString())}){r->toast(if(r.ok)"Race status updated" else jsonError(r.body,"Race status update failed"));if(r.ok)showRaceControl()}
  });v.addView(btn("Back Home",false){resumeSession()});footer(v)
 }
 private fun showAdminRoles(){
  val v=root();header(v);v.addView(txt("Admin Roles",25f,true));v.addView(txt("Limits: Super Admin 2 • Admin 3 • Announcer 4",14f));val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};v.addView(box)
  network({api.get(ApiRoutes.SYSTEM_ROLE)}){r->box.removeAllViews();if(!r.ok){box.addView(txt("Unable to load users."));return@network};val a=JSONObject(r.body).optJSONArray("users")?:return@network;for(i in 0 until a.length()){val u=a.getJSONObject(i);val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL};row.addView(txt(u.optString("name")+" — "+u.optString("systemRole"),14f),LinearLayout.LayoutParams(0,dp(50),1f));row.addView(btn("Change",false){changeRole(u)},LinearLayout.LayoutParams(dp(100),dp(50)));box.addView(row)}}
  v.addView(btn("Back Home",false){resumeSession()});footer(v)
 }
 private fun changeRole(u:JSONObject) {
  val roles = arrayOf("user","announcer","admin","super_admin")
  android.app.AlertDialog.Builder(this)
   .setTitle("Role for ${u.optString("name")}")
   .setItems(roles) { _, which ->
    val j = JSONObject()
     .put("userId", u.getInt("id"))
     .put("systemRole", roles[which])
    network({ api.post(ApiRoutes.SYSTEM_ROLE, j.toString()) }) { r ->
     toast(if (r.ok) "Role updated" else jsonError(r.body, "Role change failed"))
     if (r.ok) showAdminRoles()
    }
   }
   .show()
 }
 private fun setBusy(b:Boolean){/* network guard hook; controls remain usable after callbacks */}
 override fun onDestroy(){super.onDestroy();io.shutdownNow()}
}
