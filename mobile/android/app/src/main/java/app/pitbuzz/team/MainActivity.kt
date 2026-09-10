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
  val body=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   setPadding(dp(12),dp(4),dp(12),dp(8))
   setBackgroundColor(Color.rgb(250,251,253))
  }
  if(scroll){
   val sv=ScrollView(this).apply{
    isFillViewport=true
    setBackgroundColor(Color.rgb(250,251,253))
    overScrollMode=View.OVER_SCROLL_NEVER
   }
   sv.addView(body,android.view.ViewGroup.LayoutParams(-1,-2))
   setContentView(sv)
  }else setContentView(body)
  return body
 }
 private fun txt(s:String,size:Float=16f,bold:Boolean=false)=TextView(this).apply{
  text=s
  textSize=size
  setTextColor(navy)
  setPadding(dp(3),dp(3),dp(3),dp(3))
  includeFontPadding=false
  if(bold)setTypeface(Typeface.DEFAULT,Typeface.BOLD)
 }
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
 private fun card():LinearLayout=LinearLayout(this).apply{
  orientation=LinearLayout.VERTICAL
  setPadding(dp(12),dp(10),dp(12),dp(10))
  background=rounded(Color.WHITE,12,1,Color.rgb(218,225,232))
  elevation=dp(1).toFloat()
  layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(6))}
 }
 private fun header(v:LinearLayout){
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
   setPadding(0,dp(2),0,0)
  }
  row.addView(TextView(this).apply{
   text="☰"
   textSize=24f
   gravity=Gravity.CENTER
   setTextColor(navy)
  },LinearLayout.LayoutParams(dp(38),dp(58)))

  val center=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   gravity=Gravity.CENTER
  }
  val id=resources.getIdentifier("natva_logo","drawable",packageName)
  if(id!=0)center.addView(ImageView(this).apply{
   setImageResource(id)
   adjustViewBounds=true
   scaleType=ImageView.ScaleType.CENTER_INSIDE
  },LinearLayout.LayoutParams(-1,dp(48)))
  center.addView(txt("OFFICIAL RACE COMMUNICATION",8.5f,true).apply{
   gravity=Gravity.CENTER
   letterSpacing=.08f
   setTextColor(navy)
   setPadding(0,0,0,0)
  })
  row.addView(center,LinearLayout.LayoutParams(0,dp(62),1f))

  val bell=FrameLayout(this)
  bell.addView(TextView(this).apply{
   text="♟"
   textSize=20f
   gravity=Gravity.CENTER
   setTextColor(navy)
  },FrameLayout.LayoutParams(dp(38),dp(58)))
  bell.addView(TextView(this).apply{
   text="3"
   textSize=9f
   gravity=Gravity.CENTER
   setTextColor(Color.WHITE)
   background=rounded(red,20)
  },FrameLayout.LayoutParams(dp(19),dp(19),Gravity.END or Gravity.TOP).apply{
   topMargin=dp(5);rightMargin=dp(1)
  })
  row.addView(bell,LinearLayout.LayoutParams(dp(38),dp(58)))
  v.addView(row)
 }
 private fun footer(v:LinearLayout){
  gap(v,6)
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER
   setPadding(0,dp(2),0,dp(2))
  }
  val id=resources.getIdentifier("pitbuzz_logo","drawable",packageName)
  if(id!=0)row.addView(ImageView(this).apply{
   setImageResource(id)
   adjustViewBounds=true
   scaleType=ImageView.ScaleType.CENTER_INSIDE
  },LinearLayout.LayoutParams(dp(138),dp(42)))
  else row.addView(txt("PitBuzz © 2026",11f,true).apply{
   gravity=Gravity.CENTER
   setTextColor(muted)
  })
  v.addView(row)
 }
 private fun connectedCard():LinearLayout{
  val c=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
   setPadding(dp(10),dp(8),dp(10),dp(8))
   background=rounded(Color.rgb(236,250,240),10,1,Color.rgb(183,232,195))
   layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(5))}
  }
  c.addView(TextView(this).apply{
   text="✓"
   textSize=17f
   gravity=Gravity.CENTER
   setTextColor(Color.WHITE)
   background=rounded(Color.rgb(26,167,73),30)
  },LinearLayout.LayoutParams(dp(31),dp(31)))
  val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(8),0,0,0)}
  words.addView(txt("CONNECTED",12f,true).apply{setTextColor(Color.rgb(18,142,61))})
  words.addView(txt("You will receive announcements",10f).apply{setTextColor(Color.rgb(49,92,59))})
  c.addView(words,LinearLayout.LayoutParams(0,-2,1f))
  c.addView(txt("▮▮▮",17f,true).apply{
   gravity=Gravity.END
   setTextColor(Color.rgb(18,142,61))
  })
  return c
 }

 private fun sectionHeader(title:String,action:String?=null,onAction:(()->Unit)?=null):LinearLayout{
  val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  row.addView(txt(title,13f,true).apply{letterSpacing=.03f},LinearLayout.LayoutParams(0,-2,1f))
  if(action!=null && onAction!=null)row.addView(TextView(this).apply{
   text=action
   textSize=11f
   setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setTextColor(blue)
   gravity=Gravity.END
   setPadding(dp(4),dp(5),dp(4),dp(5))
   setOnClickListener{onAction()}
  })
  return row
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
  val shell=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   weightSum=3f
   background=rounded(Color.WHITE,10,1,Color.rgb(216,223,230))
   setPadding(dp(2),dp(2),dp(2),dp(2))
   layoutParams=LinearLayout.LayoutParams(-1,dp(58)).apply{setMargins(0,dp(3),0,dp(5))}
  }
  val labels=listOf("RACE #","STAGING","GET READY")
  val cached=listOf(
   cache.getString("race_current","—")?:"—",
   cache.getString("race_staging","—")?:"—",
   cache.getString("race_ready","—")?:"—"
  )
  val values=mutableListOf<TextView>()
  labels.forEachIndexed{i,label->
   val col=LinearLayout(this).apply{
    orientation=LinearLayout.VERTICAL
    gravity=Gravity.CENTER
    background=rounded(if(i==0)navy else if(i==1)blue else red,8)
    setPadding(dp(2),dp(5),dp(2),dp(5))
   }
   col.addView(txt(label,9f,true).apply{
    gravity=Gravity.CENTER
    setTextColor(Color.WHITE)
    alpha=.82f
   })
   val value=txt(cached[i],20f,true).apply{
    gravity=Gravity.CENTER
    setTextColor(Color.WHITE)
   }
   values.add(value)
   col.addView(value)
   shell.addView(col,LinearLayout.LayoutParams(0,-1,1f).apply{
    if(i<2)rightMargin=dp(2)
   })
  }
  v.addView(shell)

  network({api.get(ApiRoutes.RACE_STATUS)}){r->
   if(r.ok){
    val j=JSONObject(r.body)
    val c=j.optString("currentRace","—").ifBlank{"—"}
    val st=j.optString("stagingRace","—").ifBlank{"—"}
    val gr=j.optString("getReadyRace","—").ifBlank{"—"}
    values[0].text=c;values[1].text=st;values[2].text=gr
    cache.edit().putString("race_current",c).putString("race_staging",st).putString("race_ready",gr).putLong("race_updated",System.currentTimeMillis()).apply()
   }
  }
 }
 private fun heartbeat(){network({api.post(ApiRoutes.HEARTBEAT,JSONObject().put("platform","android").put("version","0.1.0-beta").toString())}){}}
 private fun showHome(user:JSONObject){
  currentUser=user
  val v=root()
  header(v)
  v.addView(connectedCard())
  raceBar(v)

  v.addView(sectionHeader("LATEST ANNOUNCEMENT","VIEW ALL ›"){showAnnouncements()})
  val announcement=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   setPadding(dp(10),dp(8),dp(10),dp(10))
   background=rounded(Color.WHITE,10,1,Color.rgb(206,216,225))
  }
  val ribbon=TextView(this).apply{
   text="LATEST ANNOUNCEMENT"
   textSize=12f
   setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setTextColor(Color.WHITE)
   setPadding(dp(8),dp(5),dp(8),dp(5))
   background=rounded(red,7)
  }
  announcement.addView(ribbon,LinearLayout.LayoutParams(-1,-2))
  val latest=txt("Loading latest announcement…",15f,true).apply{
   setPadding(dp(3),dp(9),dp(3),dp(5))
   setTextColor(navy)
  }
  announcement.addView(latest)
  v.addView(announcement)

  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->
   latest.text=if(r.ok){
    val a=JSONObject(r.body).optJSONArray("announcements")
    if(a!=null && a.length()>0){
     val x=a.getJSONObject(0)
     val title=x.optString("title").ifBlank{"OFFICIAL ANNOUNCEMENT"}
     title.uppercase()+"
"+x.optString("body")
    } else "No announcements yet."
   }else "Announcements unavailable."
  }

  v.addView(sectionHeader("MY TEAM"))
  val teamCard=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
   setPadding(dp(12),dp(11),dp(12),dp(11))
   background=rounded(Color.WHITE,10,1,Color.rgb(218,225,232))
   setOnClickListener{showTeam()}
  }
  teamCard.addView(txt("♟",24f,true).apply{setTextColor(blue)},LinearLayout.LayoutParams(dp(36),dp(42)))
  val teamWords=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  teamWords.addView(txt("My Team",16f,true))
  teamWords.addView(txt("Riders • Family/Crew • Notifications",11f).apply{setTextColor(muted)})
  teamCard.addView(teamWords,LinearLayout.LayoutParams(0,-2,1f))
  teamCard.addView(txt("›",24f,true).apply{gravity=Gravity.CENTER;setTextColor(navy)})
  v.addView(teamCard)

  val role=user.optString("systemRole","user")
  if(role in listOf("announcer","admin","super_admin")){
   v.addView(sectionHeader("OFFICIAL TOOLS"))
   val tools=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=2f}
   tools.addView(btn("New Announcement"){showAnnouncementComposer()},LinearLayout.LayoutParams(0,dp(52),1f).apply{rightMargin=dp(4)})
   tools.addView(btn("Race Control",false){showRaceControl()},LinearLayout.LayoutParams(0,dp(52),1f).apply{leftMargin=dp(4)})
   v.addView(tools)
  }
  if(role=="super_admin")v.addView(btn("Admin Dashboard",false){showAdminRoles()})
  v.addView(bottomNav("Home"))
  footer(v)
 }
 private fun bottomNav(active:String):LinearLayout{
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   weightSum=5f
   setPadding(0,dp(3),0,0)
   background=rounded(Color.WHITE,8,1,Color.rgb(216,223,230))
   layoutParams=LinearLayout.LayoutParams(-1,dp(54)).apply{setMargins(0,dp(7),0,0)}
  }
  val icons=mapOf("Home" to "⌂","Announcements" to "▣","Messages" to "✉","Audio" to "◉","Settings" to "⚙")
  listOf("Home","Announcements","Messages","Audio","Settings").forEach{name->
   val item=TextView(this).apply{
    text=(icons[name]?:"")+"
"+name
    textSize=9f
    gravity=Gravity.CENTER
    includeFontPadding=false
    setTypeface(Typeface.DEFAULT,if(name==active)Typeface.BOLD else Typeface.NORMAL)
    setTextColor(if(name==active)red else navy)
    setPadding(dp(1),dp(3),dp(1),dp(3))
    setOnClickListener{
     when(name){
      "Home"->resumeSession()
      "Announcements"->showAnnouncements()
      "Messages"->showMessages()
      "Audio"->showAudio()
      "Settings"->showSettings()
     }
    }
   }
   row.addView(item,LinearLayout.LayoutParams(0,-1,1f))
  }
  return row
 }
 private fun showAnnouncements(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("Announcements",20f,true).apply{gravity=Gravity.CENTER})
  val filters=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  listOf("All","Unread","Official").forEachIndexed{i,name->
   val chip=TextView(this).apply{
    text=name
    textSize=11f
    gravity=Gravity.CENTER
    setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setTextColor(if(i==0)Color.WHITE else navy)
    background=rounded(if(i==0)red else Color.rgb(239,243,247),12,1,if(i==0)red else Color.rgb(218,225,232))
   }
   filters.addView(chip,LinearLayout.LayoutParams(0,dp(36),1f).apply{if(i<2)rightMargin=dp(4)})
  }
  v.addView(filters)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->
   box.removeAllViews()
   if(r.ok){
    val a=JSONObject(r.body).optJSONArray("announcements")
    if(a==null||a.length()==0)box.addView(card().apply{addView(txt("No announcements yet.",14f))})
    else for(i in 0 until a.length()){
     val x=a.getJSONObject(i)
     val c=LinearLayout(this).apply{
      orientation=LinearLayout.HORIZONTAL
      gravity=Gravity.TOP
      setPadding(dp(8),dp(9),dp(8),dp(9))
      background=rounded(Color.WHITE,8,0)
     }
     c.addView(TextView(this).apply{
      text="!"
      textSize=19f
      gravity=Gravity.CENTER
      setTypeface(Typeface.DEFAULT,Typeface.BOLD)
      setTextColor(Color.WHITE)
      background=rounded(if(x.optInt("isCritical",0)==1)red else blue,28)
     },LinearLayout.LayoutParams(dp(38),dp(38)))
     val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(8),0,0,0)}
     words.addView(txt(x.optString("senderName","Track Official"),12f,true))
     words.addView(txt(x.optString("title","Official Announcement"),14f,true))
     words.addView(txt(x.optString("body"),12f).apply{setTextColor(Color.rgb(55,63,72))})
     val whenSent=x.optString("createdAt")
     if(whenSent.isNotBlank())words.addView(txt(whenSent,10f).apply{setTextColor(muted)})
     c.addView(words,LinearLayout.LayoutParams(0,-2,1f))
     box.addView(c,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(3))})
     val line=View(this).apply{setBackgroundColor(Color.rgb(229,233,237))}
     box.addView(line,LinearLayout.LayoutParams(-1,dp(1)))
    }
   }else box.addView(card().apply{addView(txt("Unable to load announcements."))})
  }
  v.addView(bottomNav("Announcements"))
  footer(v)
 }
 private fun showMessages(){
  val v=root()
  header(v)
  raceBar(v)
  val title=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  title.addView(txt("Messages",20f,true).apply{gravity=Gravity.CENTER},LinearLayout.LayoutParams(0,-2,1f))
  title.addView(TextView(this).apply{
   text="+"
   textSize=22f
   gravity=Gravity.CENTER
   setTextColor(Color.WHITE)
   background=rounded(red,30)
   setOnClickListener{composeMessage()}
  },LinearLayout.LayoutParams(dp(38),dp(38)))
  v.addView(title)
  val filters=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=4f}
  listOf("All","Unread","Official","Team").forEachIndexed{i,name->
   val chip=TextView(this).apply{
    text=name
    textSize=10f
    gravity=Gravity.CENTER
    setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setTextColor(if(i==0)Color.WHITE else navy)
    background=rounded(if(i==0)red else Color.rgb(239,243,247),12)
   }
   filters.addView(chip,LinearLayout.LayoutParams(0,dp(34),1f).apply{if(i<3)rightMargin=dp(3)})
  }
  v.addView(filters)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.MESSAGES)}){r->
   box.removeAllViews()
   if(r.ok){
    val a=JSONObject(r.body).optJSONArray("messages")
    if(a==null||a.length()==0)box.addView(card().apply{addView(txt("No private messages yet."))})
    else for(i in 0 until a.length()){
     val x=a.getJSONObject(i)
     val c=LinearLayout(this).apply{
      orientation=LinearLayout.HORIZONTAL
      gravity=Gravity.CENTER_VERTICAL
      setPadding(dp(7),dp(9),dp(7),dp(9))
      background=rounded(Color.WHITE,7)
     }
     c.addView(TextView(this).apply{
      text="●"
      textSize=18f
      gravity=Gravity.CENTER
      setTextColor(blue)
      background=rounded(Color.rgb(238,245,253),28)
     },LinearLayout.LayoutParams(dp(38),dp(38)))
     val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(8),0,0,0)}
     words.addView(txt(x.optString("senderName","PitBuzz User"),12f,true))
     words.addView(txt(x.optString("body"),12f).apply{setTextColor(Color.rgb(54,62,70))})
     val whenSent=x.optString("createdAt")
     if(whenSent.isNotBlank())words.addView(txt(whenSent,9.5f).apply{setTextColor(muted)})
     c.addView(words,LinearLayout.LayoutParams(0,-2,1f))
     c.addView(TextView(this).apply{
      text="●"
      textSize=9f
      gravity=Gravity.CENTER
      setTextColor(blue)
     },LinearLayout.LayoutParams(dp(18),dp(28)))
     box.addView(c)
     box.addView(View(this).apply{setBackgroundColor(Color.rgb(229,233,237))},LinearLayout.LayoutParams(-1,dp(1)))
    }
   }else box.addView(card().apply{addView(txt("Unable to load messages."))})
  }
  v.addView(bottomNav("Messages"))
  footer(v)
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
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("My Team",20f,true).apply{gravity=Gravity.CENTER})
  val tabs=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  listOf("Riders","Family/Crew","Settings").forEachIndexed{i,name->
   tabs.addView(TextView(this).apply{
    text=name
    textSize=11f
    gravity=Gravity.CENTER
    setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setTextColor(if(i==0)Color.WHITE else navy)
    background=rounded(if(i==0)red else Color.rgb(239,243,247),10)
   },LinearLayout.LayoutParams(0,dp(38),1f).apply{if(i<2)rightMargin=dp(4)})
  }
  v.addView(tabs)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.TEAM)}){r->
   box.removeAllViews()
   if(!r.ok){box.addView(txt("Unable to load team."));return@network}
   val j=JSONObject(r.body)
   if(j.isNull("team")){
    box.addView(card().apply{
     addView(txt("You are not on a team yet.",14f,true))
     addView(txt("Create a team to manage riders, family and crew.",11f).apply{setTextColor(muted)})
    })
    box.addView(btn("+  Create My Team"){createTeam()})
   }else{
    val riders=j.optJSONArray("riders")
    if(riders!=null)for(i in 0 until riders.length()){
     val x=riders.getJSONObject(i)
     val row=LinearLayout(this).apply{
      orientation=LinearLayout.HORIZONTAL
      gravity=Gravity.CENTER_VERTICAL
      setPadding(dp(8),dp(8),dp(8),dp(8))
      background=rounded(Color.WHITE,8)
     }
     row.addView(TextView(this).apply{
      text="◉"
      textSize=25f
      gravity=Gravity.CENTER
      setTextColor(listOf(red,Color.rgb(144,41,178),blue)[i%3])
     },LinearLayout.LayoutParams(dp(48),dp(48)))
     val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
     words.addView(txt("#${x.optString("raceNumber")}  ${x.optString("name")}",15f,true))
     words.addView(txt(x.optString("classes","Rider"),11f).apply{setTextColor(muted)})
     row.addView(words,LinearLayout.LayoutParams(0,-2,1f))
     row.addView(txt("›",22f,true).apply{gravity=Gravity.CENTER})
     box.addView(row)
     box.addView(View(this).apply{setBackgroundColor(Color.rgb(229,233,237))},LinearLayout.LayoutParams(-1,dp(1)))
    }
    if(j.optBoolean("canManage")){
     box.addView(btn("+  Add Rider"){addRider()})
     box.addView(btn("Manage Family/Crew",false){addMember()})
    }
   }
  }
  v.addView(bottomNav("Home"))
  footer(v)
 }
 private fun createTeam(){val input=field("Team name");android.app.AlertDialog.Builder(this).setTitle("Create Team").setView(input).setPositiveButton("Create"){_,_->val n=input.text.toString().trim();if(n.isNotBlank())network({api.post(ApiRoutes.TEAM,JSONObject().put("action","create_team").put("name",n).toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not create team"))}}.setNegativeButton("Cancel",null).show()}
 private fun addRider(){val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Rider name");val num=field("Race number");wrap.addView(n);wrap.addView(num);android.app.AlertDialog.Builder(this).setTitle("Add Rider").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isNotBlank())network({api.post(ApiRoutes.TEAM,JSONObject().put("action","add_rider").put("name",n.text.toString().trim()).put("raceNumber",num.text.toString().trim()).toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add rider"))}}.setNegativeButton("Cancel",null).show()}
 private fun addMember(){
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Name");val ph=field("Phone number (optional)");val role=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("Family","Crew"))};wrap.addView(n);wrap.addView(ph);wrap.addView(role)
  android.app.AlertDialog.Builder(this).setTitle("Add Family / Crew").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isBlank()){toast("Name is required");return@setPositiveButton};val j=JSONObject().put("action","add_member").put("name",n.text.toString().trim()).put("phone",ph.text.toString().trim()).put("role",if(role.selectedItemPosition==0)"family" else "crew").put("followsAllRiders",true);network({api.post(ApiRoutes.TEAM,j.toString())}){r->if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add member"))}}.setNegativeButton("Cancel",null).show()
 }
 private fun showAnnouncementComposer(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("New Announcement",20f,true))
  v.addView(txt("Announcement Type",12f,true))
  val types=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  listOf("General
All Users","Specific Class
Selected Classes","Private Message
Single User").forEachIndexed{i,name->
   types.addView(TextView(this).apply{
    text=name
    textSize=10f
    gravity=Gravity.CENTER
    setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setTextColor(if(i==0)Color.WHITE else navy)
    background=rounded(if(i==0)red else Color.rgb(238,243,248),9,1,Color.rgb(218,225,232))
   },LinearLayout.LayoutParams(0,dp(70),1f).apply{if(i<2)rightMargin=dp(4)})
  }
  v.addView(types)
  v.addView(txt("Message",12f,true))
  val title=field("Title")
  val body=field("Type your announcement here…")
  v.addView(title);v.addView(body)
  val audio=Switch(this).apply{text="Play Audio (Text-to-Speech)";isChecked=true;setTextColor(navy)}
  val critical=Switch(this).apply{text="Show as Critical";setTextColor(navy)}
  v.addView(txt("Options",12f,true));v.addView(audio);v.addView(critical)
  v.addView(btn("Send Announcement"){
   if(title.text.isBlank()||body.text.isBlank()){toast("Title and announcement text are required");return@btn}
   val j=JSONObject().put("targetType","all")
    .put("title",title.text.toString().trim())
    .put("body",body.text.toString().trim())
    .put("priority",if(critical.isChecked)"urgent" else "normal")
    .put("audioEnabled",audio.isChecked)
   network({api.post(ApiRoutes.ANNOUNCEMENTS,j.toString())}){r->
    if(r.ok){toast("Announcement sent");showAnnouncements()}
    else toast(jsonError(r.body,"Announcement failed"))
   }
  })
  v.addView(bottomNav("Announcements"))
  footer(v)
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
  val v=root()
  header(v)
  v.addView(txt("Admin Dashboard",20f,true).apply{gravity=Gravity.CENTER})
  val ok=connectedCard()
  (ok.getChildAt(1) as? LinearLayout)?.let{words->
   (words.getChildAt(0) as? TextView)?.text="ALL SYSTEMS ONLINE"
   (words.getChildAt(1) as? TextView)?.text="Everything is running smoothly"
  }
  v.addView(ok)
  v.addView(txt("QUICK ACTIONS",11f,true))
  val grid=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  val actions=listOf(
   "New
Announcement" to {showAnnouncementComposer()},
   "Quick
Message" to {composeMessage()},
   "Race
Control" to {showRaceControl()},
   "Announcement
History" to {showAnnouncements()},
   "Users & Staff
Manage Access" to {},
   "App Settings
Configure App" to {showSettings()}
  )
  for(r in 0..1){
   val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
   for(c in 0..2){
    val item=actions[r*3+c]
    val tile=TextView(this).apply{
     text=item.first
     textSize=11f
     gravity=Gravity.CENTER
     setTypeface(Typeface.DEFAULT,Typeface.BOLD)
     setTextColor(navy)
     background=rounded(Color.WHITE,9,1,Color.rgb(213,221,229))
     setOnClickListener{item.second()}
    }
    row.addView(tile,LinearLayout.LayoutParams(0,dp(82),1f).apply{
     if(c<2)rightMargin=dp(4)
    })
   }
   grid.addView(row,LinearLayout.LayoutParams(-1,-2).apply{if(r==0)bottomMargin=dp(4)})
  }
  v.addView(grid)
  v.addView(txt("USERS & STAFF",11f,true))
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.SYSTEM_ROLE)}){r->
   box.removeAllViews()
   if(!r.ok){box.addView(txt("Unable to load users."));return@network}
   val a=JSONObject(r.body).optJSONArray("users")?:return@network
   for(i in 0 until a.length()){
    val u=a.getJSONObject(i)
    val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
    row.addView(txt(u.optString("name")+" — "+u.optString("systemRole"),12f),LinearLayout.LayoutParams(0,dp(44),1f))
    row.addView(btn("Change",false){changeRole(u)},LinearLayout.LayoutParams(dp(88),dp(44)))
    box.addView(row)
   }
  }
  v.addView(bottomNav("Home"))
  footer(v)
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
