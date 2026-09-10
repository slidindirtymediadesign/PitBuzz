package app.pitbuzz.team

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.pitbuzz.team.network.*
import app.pitbuzz.team.state.SubmitGuard
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.Locale

class MainActivity:AppCompatActivity(){
 private var tts:TextToSpeech?=null
 private var ttsReady=false

 private val submitGuard=SubmitGuard()

 private val io=Executors.newSingleThreadExecutor(); private lateinit var api:ApiClient; private var currentUser:JSONObject?=null
 private val navy=Color.rgb(8,35,64); private val red=Color.rgb(205,34,44); private val blue=Color.rgb(10,105,186); private val muted=Color.rgb(92,101,110); private val surface=Color.rgb(247,249,252); private val line=Color.rgb(214,222,231)
 private val cache by lazy{getSharedPreferences("pitbuzz_cache",MODE_PRIVATE)}
 override fun onCreate(s:Bundle?){
  super.onCreate(s); NotificationChannels.create(this); api=ApiClient(this)
  tts=TextToSpeech(this){status->
   ttsReady=status==TextToSpeech.SUCCESS
   if(ttsReady)tts?.language=Locale.US
  }
  if(android.os.Build.VERSION.SDK_INT>=33 && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),2001)
  if(api.token!=null)resumeSession(intent.getStringExtra("open")) else showLogin()
 }
 override fun onNewIntent(intent:android.content.Intent){
  super.onNewIntent(intent)
  val open=intent.getStringExtra("open")
  if(api.token!=null){
   when(open){
    "messages"->showMessages()
    "announcements"->showAnnouncements()
    else->resumeSession()
   }
  }
 }

 private fun dp(n:Int)=(n*resources.displayMetrics.density).toInt()
 private fun rounded(color:Int,radius:Int=14,stroke:Int=0,strokeColor:Int=Color.TRANSPARENT)=GradientDrawable().apply{shape=GradientDrawable.RECTANGLE;cornerRadius=dp(radius).toFloat();setColor(color);if(stroke>0)setStroke(dp(stroke),strokeColor)}
 private fun gap(v:LinearLayout,h:Int=10){v.addView(Space(this),LinearLayout.LayoutParams(1,dp(h)))}
 private fun root(scroll:Boolean=true):LinearLayout{
  val body=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   setPadding(dp(12),dp(14),dp(12),dp(14))
   setBackgroundColor(surface)
   isFocusableInTouchMode=true
  }
  if(scroll){
   val sv=ScrollView(this).apply{
    isFillViewport=false
    isVerticalScrollBarEnabled=true
    scrollBarStyle=View.SCROLLBARS_INSIDE_OVERLAY
    setBackgroundColor(surface)
    overScrollMode=View.OVER_SCROLL_IF_CONTENT_SCROLLS
    descendantFocusability=android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS
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
  setTextColor(if(primary)Color.WHITE else navy);background=if(primary)rounded(red,10) else rounded(Color.WHITE,10,1,line);setOnClickListener{action()}
  layoutParams=LinearLayout.LayoutParams(-1,dp(52)).apply{setMargins(0,dp(5),0,dp(5))}
 }
 private fun card():LinearLayout=LinearLayout(this).apply{
  orientation=LinearLayout.VERTICAL
  setPadding(dp(12),dp(10),dp(12),dp(10))
  background=rounded(Color.WHITE,10,1,line)
  elevation=dp(2).toFloat()
  layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(6))}
 }
 private fun header(v:LinearLayout){
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
   setPadding(0,dp(8),0,dp(3))
  }
  row.addView(ImageView(this).apply{
   setImageResource(R.drawable.ic_menu)
   imageTintList=android.content.res.ColorStateList.valueOf(navy)
   contentDescription="Menu"
   setPadding(dp(7),dp(17),dp(7),dp(17))
  },LinearLayout.LayoutParams(dp(38),dp(58)))

  val center=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   gravity=Gravity.CENTER
  }
  val id=resources.getIdentifier("natva_logo","drawable",packageName)
  if(id!=0)center.addView(ImageView(this).apply{
   setImageResource(id)
   contentDescription="NATVA"
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
  bell.addView(ImageView(this).apply{
   setImageResource(R.drawable.ic_bell)
   imageTintList=android.content.res.ColorStateList.valueOf(navy)
   contentDescription="Notifications"
   setPadding(dp(8),dp(17),dp(8),dp(17))
  },FrameLayout.LayoutParams(dp(38),dp(58)))
  val badge=TextView(this).apply{
   text=""
   textSize=9f
   gravity=Gravity.CENTER
   setTextColor(Color.WHITE)
   background=rounded(red,20)
   visibility=View.GONE
  }
  bell.addView(badge,FrameLayout.LayoutParams(dp(19),dp(19),Gravity.END or Gravity.TOP).apply{
   topMargin=dp(5);rightMargin=dp(1)
  })
  bell.setOnClickListener{if(api.token!=null)showAnnouncements()}
  row.addView(bell,LinearLayout.LayoutParams(dp(38),dp(58)))
  v.addView(row)
  val accent=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  accent.addView(View(this).apply{setBackgroundColor(navy)},LinearLayout.LayoutParams(0,dp(3),2f))
  accent.addView(View(this).apply{setBackgroundColor(red)},LinearLayout.LayoutParams(0,dp(3),1f))
  v.addView(accent)

  if(api.token!=null){
   network({api.get(ApiRoutes.UNREAD)}){r->
    if(r.ok){
     val j=JSONObject(r.body)
     val total=j.optInt("announcements",0)+j.optInt("messages",0)+j.optInt("raceAlerts",0)
     if(total>0){
      badge.text=if(total>99)"99+" else total.toString()
      badge.visibility=View.VISIBLE
     }else badge.visibility=View.GONE
    }
   }
  }
 }
 private fun footer(v:LinearLayout){
  gap(v,10)
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
  },LinearLayout.LayoutParams(dp(156),dp(48)))
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

 private fun pill(label:String,color:Int):TextView=TextView(this).apply{
  text=label
  textSize=9.5f
  gravity=Gravity.CENTER
  setTypeface(Typeface.DEFAULT,Typeface.BOLD)
  setTextColor(Color.WHITE)
  background=rounded(color,16)
  setPadding(dp(8),dp(4),dp(8),dp(4))
 }

 private fun emptyState(title:String,body:String,actionLabel:String?=null,action:(()->Unit)?=null):LinearLayout{
  return card().apply{
   gravity=Gravity.CENTER_HORIZONTAL
   addView(txt(title,15f,true).apply{gravity=Gravity.CENTER})
   addView(txt(body,11f).apply{gravity=Gravity.CENTER;setTextColor(muted)})
   if(actionLabel!=null && action!=null)addView(btn(actionLabel,false){action()})
  }
 }

 private fun jsonError(body:String,fallback:String):String=try{JSONObject(body).optString("message",fallback)}catch(_:Exception){fallback}
 private fun network(block:()->ApiResponse,done:(ApiResponse)->Unit){io.execute{try{val r=block();runOnUiThread{done(r)}}catch(e:Exception){runOnUiThread{toast("Cannot reach PitBuzz server")}}}}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
 private fun authHero(title:String,subtitle:String):LinearLayout{
  return LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   gravity=Gravity.CENTER_HORIZONTAL
   setPadding(dp(14),dp(14),dp(14),dp(14))
   background=rounded(navy,12)
   addView(txt(title,22f,true).apply{gravity=Gravity.CENTER;setTextColor(Color.WHITE)})
   addView(txt(subtitle,11f).apply{gravity=Gravity.CENTER;setTextColor(Color.rgb(190,217,239))})
   addView(View(this@MainActivity).apply{setBackgroundColor(red)},LinearLayout.LayoutParams(dp(74),dp(3)).apply{topMargin=dp(10)})
  }
 }

 private fun showLogin(){
  val v=root()
  header(v)
  gap(v,8)
  v.addView(authHero("Welcome to PitBuzz","Official NATVA race-day communication"))

  val c=card()
  c.addView(txt("SIGN IN",12f,true).apply{setTextColor(red);letterSpacing=.08f})
  c.addView(txt("Stay connected to staging, announcements and your team.",12f).apply{setTextColor(muted)})
  val login=field("Phone number or email")
  val pw=field("Password",true)
  c.addView(login);c.addView(pw)
  c.addView(CheckBox(this).apply{
   text="Show password"
   setTextColor(muted)
   buttonTintList=android.content.res.ColorStateList.valueOf(blue)
   setOnCheckedChangeListener{_,x->
    pw.inputType=InputType.TYPE_CLASS_TEXT or if(x)InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
    pw.setSelection(pw.text.length)
   }
  })
  c.addView(btn("Log In"){
   if(login.text.isBlank()||pw.text.isBlank()){toast("Enter your phone/email and password");return@btn}
   if(!submitGuard.begin("login"))return@btn
   setBusy(true)
   network({api.post(ApiRoutes.LOGIN,JSONObject().put("login",login.text.toString().trim()).put("password",pw.text.toString()).toString())}){r->
    submitGuard.finish("login")
    setBusy(false)
    if(!r.ok)toast(jsonError(r.body,"Login failed"))
    else{
     val j=JSONObject(r.body)
     api.token=j.getString("token")
     syncPushToken()
     showHome(j.getJSONObject("user"))
    }
   }
  })
  val links=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=2f}
  links.addView(btn("Forgot Password?",false){showRecovery()},LinearLayout.LayoutParams(0,dp(50),1f).apply{rightMargin=dp(4)})
  links.addView(btn("Create Account",false){showRegister()},LinearLayout.LayoutParams(0,dp(50),1f).apply{leftMargin=dp(4)})
  c.addView(links)
  v.addView(c)
  footer(v)
 }
 private fun showRegister(){
  val v=root()
  header(v)
  gap(v,6)
  v.addView(authHero("Create Account","One PitBuzz login for race-day communication"))
  val c=card()
  c.addView(txt("ACCOUNT INFORMATION",11f,true).apply{setTextColor(red)})
  val name=field("Name")
  val p1=field("Phone Number")
  val p2=field("Confirm Phone Number")
  val email=field("Email")
  val pw=field("Password",true)
  val pw2=field("Confirm Password",true)
  listOf(name,p1,p2,email,pw,pw2).forEach(c::addView)
  c.addView(CheckBox(this).apply{
   text="Show passwords"
   setTextColor(muted)
   buttonTintList=android.content.res.ColorStateList.valueOf(blue)
   setOnCheckedChangeListener{_,x->
    val t=InputType.TYPE_CLASS_TEXT or if(x)InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD
    pw.inputType=t;pw2.inputType=t
   }
  })
  c.addView(txt("Phone number is required for PitBuzz login and account matching.",10.5f).apply{setTextColor(muted)})
  c.addView(btn("Create Account"){
   val a=p1.text.toString().filter(Char::isDigit).removePrefix("1")
   val b=p2.text.toString().filter(Char::isDigit).removePrefix("1")
   when{
    name.text.isBlank()->toast("Name is required")
    a.length!=10->toast("Enter a valid 10-digit phone number")
    a!=b->toast("Phone numbers do not match")
    !email.text.contains("@")->toast("Enter a valid email")
    pw.text.length<8->toast("Password must be at least 8 characters")
    pw.text.toString()!=pw2.text.toString()->toast("Passwords do not match")
    else->{
     if(!submitGuard.begin("register"))return@btn
     setBusy(true)
     val j=JSONObject().put("name",name.text.toString().trim()).put("phone",a).put("email",email.text.toString().trim()).put("password",pw.text.toString())
     network({api.post(ApiRoutes.REGISTER,j.toString())}){r->
      submitGuard.finish("register")
      setBusy(false)
      if(!r.ok)toast(jsonError(r.body,"Registration failed"))
      else{
       val x=JSONObject(r.body)
       api.token=x.getString("token")
       syncPushToken()
       toast("Account created")
       showHome(x.getJSONObject("user"))
      }
     }
    }
   }
  })
  c.addView(btn("Back to Sign In",false){showLogin()})
  v.addView(c)
  footer(v)
 }
 private fun showRecovery(){
  val v=root()
  header(v)
  gap(v,6)
  v.addView(authHero("Account Recovery","Recover your PitBuzz access securely"))
  val c=card()
  c.addView(txt("RESET PASSWORD",11f,true).apply{setTextColor(red)})
  c.addView(txt("Enter the phone number or email on your PitBuzz account. A 6-digit code will be sent to the account email.",12f).apply{setTextColor(muted)})
  val login=field("Phone number or email")
  c.addView(login)
  c.addView(btn("Send Reset Code"){
   if(login.text.isBlank()){toast("Enter your phone number or email");return@btn}
   if(!submitGuard.begin("reset-request"))return@btn
   val payload=JSONObject().put("login",login.text.toString().trim())
   network({api.post(ApiRoutes.RESET_REQUEST,payload.toString())}){r->
    submitGuard.finish("reset-request")
    if(r.ok){toast(jsonError(r.body,"Reset code requested"));showResetConfirm(login.text.toString().trim())}
    else toast(jsonError(r.body,"Recovery request failed"))
   }
  })
  c.addView(btn("Back to Sign In",false){showLogin()})
  v.addView(c)
  footer(v)
 }
 private fun showResetConfirm(loginValue:String){
  val v=root();header(v);v.addView(txt("Enter Reset Code",25f,true));val code=field("6-digit code");val pw=field("New password",true);val pw2=field("Confirm new password",true);v.addView(code);v.addView(pw);v.addView(pw2)
  v.addView(btn("Update Password"){
   if(!code.text.toString().matches(Regex("\\d{6}"))){toast("Enter the 6-digit code");return@btn};if(pw.text.length<8){toast("Password must be at least 8 characters");return@btn};if(pw.text.toString()!=pw2.text.toString()){toast("Passwords do not match");return@btn}
   val j=JSONObject().put("login",loginValue).put("code",code.text.toString()).put("password",pw.text.toString());network({api.post(ApiRoutes.RESET_CONFIRM,j.toString())}){r->if(r.ok){toast("Password updated");showLogin()}else toast(jsonError(r.body,"Password reset failed"))}
  });v.addView(btn("Cancel",false){showLogin()});footer(v)
 }
 private fun resumeSession(open:String?=null){
  network({api.get(ApiRoutes.SESSION)}){r->
   if(r.ok){
    syncPushToken()
    val user=JSONObject(r.body).getJSONObject("user")
    currentUser=user
    when(open){
     "messages"->showMessages()
     "announcements"->showAnnouncements()
     "home"->showHome(user)
     else->showHome(user)
    }
   }else{
    api.token=null
    showLogin()
   }
  }
 }
 private fun syncPushToken(){
  if(api.token==null)return
  try{
   com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener{task->
    if(!task.isSuccessful){
     cache.edit().putBoolean("push_registered",false).apply()
     return@addOnCompleteListener
    }
    val token=task.result?:return@addOnCompleteListener
    network({
     api.post(ApiRoutes.PUSH_SUBSCRIPTION,JSONObject()
      .put("deviceKey",token)
      .put("token",token)
      .put("platform","android")
      .put("provider","fcm")
      .toString())
    }){r->
     cache.edit().putBoolean("push_registered",r.ok).apply()
    }
   }
  }catch(_:Exception){
   cache.edit().putBoolean("push_registered",false).apply()
  }
 }

 private fun raceBar(v:LinearLayout){
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  val shell=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   weightSum=3f
   background=rounded(Color.WHITE,10,1,line)
   setPadding(dp(2),dp(2),dp(2),dp(2))
   layoutParams=LinearLayout.LayoutParams(-1,dp(58)).apply{setMargins(0,dp(3),0,0)}
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
   col.addView(txt(label,9f,true).apply{gravity=Gravity.CENTER;setTextColor(Color.WHITE);alpha=.82f})
   val value=txt(cached[i],20f,true).apply{gravity=Gravity.CENTER;setTextColor(Color.WHITE)}
   values.add(value);col.addView(value)
   shell.addView(col,LinearLayout.LayoutParams(0,-1,1f).apply{if(i<2)rightMargin=dp(2)})
  }
  wrap.addView(shell)
  val stale=txt("",9f,true).apply{gravity=Gravity.END;setTextColor(muted);visibility=View.GONE}
  wrap.addView(stale)
  v.addView(wrap)

  val cachedAt=cache.getLong("race_updated",0L)
  if(cachedAt>0L && System.currentTimeMillis()-cachedAt>120000L){
   stale.text="STALE RACE STATUS"
   stale.visibility=View.VISIBLE
  }

  network({api.get(ApiRoutes.RACE_STATUS)}){r->
   if(r.ok){
    val j=JSONObject(r.body)
    val c=j.optString("currentRace","").ifBlank{"—"}
    val st=j.optString("stagingRace","").ifBlank{"—"}
    val gr=j.optString("getReadyRace","").ifBlank{"—"}
    values[0].text=c;values[1].text=st;values[2].text=gr
    cache.edit().putString("race_current",c).putString("race_staging",st).putString("race_ready",gr).putLong("race_updated",System.currentTimeMillis()).apply()
    stale.visibility=View.GONE
   }else{
    stale.text="OFFLINE • LAST KNOWN RACE STATUS"
    stale.visibility=View.VISIBLE
   }
  }
 }
 private fun heartbeat(){network({api.post(ApiRoutes.HEARTBEAT,JSONObject().put("platform","android").put("version","0.1.0-beta").toString())}){}}
 private fun showHome(user:JSONObject){
  currentUser=user
  val role=user.optString("systemRole","user")
  val v=root()
  header(v)

  val userStrip=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  userStrip.addView(txt("Welcome, ${user.optString("name","PitBuzz User")}",12f,true),LinearLayout.LayoutParams(0,-2,1f))
  userStrip.addView(pill(role.replace("_"," ").uppercase(),if(role=="super_admin")red else if(role in listOf("admin","announcer"))blue else navy))
  v.addView(userStrip)

  val connection=card().apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
  }
  val dot=TextView(this).apply{
   text="✓";textSize=18f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setTextColor(Color.WHITE);background=rounded(Color.rgb(28,169,79),30)
  }
  connection.addView(dot,LinearLayout.LayoutParams(dp(40),dp(40)))
  val cWords=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(9),0,0,0)}
  val cTitle=txt("CHECKING CONNECTION",12f,true).apply{setTextColor(navy)}
  val cSub=txt("Checking PitBuzz server…",10.5f).apply{setTextColor(muted)}
  cWords.addView(cTitle);cWords.addView(cSub)
  connection.addView(cWords,LinearLayout.LayoutParams(0,-2,1f))
  val bars=txt("▂▄▆",18f,true).apply{setTextColor(Color.rgb(28,169,79));gravity=Gravity.CENTER}
  connection.addView(bars,LinearLayout.LayoutParams(dp(48),dp(40)))
  v.addView(connection)

  network({api.post(ApiRoutes.HEARTBEAT,JSONObject().put("platform","android").put("version","0.1.0-beta").toString())}){r->
   if(r.ok){
    cTitle.text="CONNECTED"
    cTitle.setTextColor(Color.rgb(22,139,65))
    cSub.text="You will receive announcements"
    dot.text="✓"
    dot.background=rounded(Color.rgb(28,169,79),30)
    bars.setTextColor(Color.rgb(28,169,79))
   }else{
    cTitle.text="NOT CONNECTED"
    cTitle.setTextColor(red)
    cSub.text="Showing last known race information"
    dot.text="!"
    dot.background=rounded(red,30)
    bars.setTextColor(red)
   }
  }

  raceBar(v)

  v.addView(sectionHeader("LATEST ANNOUNCEMENT","VIEW ALL ›"){showAnnouncements()})
  val announcement=card()
  val ribbonRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  val ribbon=txt("LATEST ANNOUNCEMENT",11f,true).apply{
   setTextColor(Color.WHITE);background=rounded(red,5);setPadding(dp(8),dp(5),dp(8),dp(5))
  }
  ribbonRow.addView(ribbon,LinearLayout.LayoutParams(0,-2,1f))
  val timeLabel=txt("",10f,true).apply{gravity=Gravity.END;setTextColor(muted)}
  ribbonRow.addView(timeLabel)
  announcement.addView(ribbonRow)

  val criticalBadge=pill("CRITICAL",red).apply{visibility=View.GONE}
  announcement.addView(criticalBadge,LinearLayout.LayoutParams(-2,dp(28)).apply{topMargin=dp(6)})
  var latestSpokenText=""
  val latestTitle=txt("Loading announcement…",22f,true).apply{setTextColor(navy);setPadding(dp(3),dp(7),dp(3),dp(2))}
  val latestBody=txt("",12f).apply{setTextColor(Color.rgb(48,57,66))}
  val latestBy=txt("",10f,true).apply{setTextColor(blue)}
  announcement.addView(latestTitle)
  announcement.addView(latestBody)
  announcement.addView(latestBy)

  val audioRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=2f}
  val play=btn("🔊  PLAY AUDIO"){speakAnnouncement(latestSpokenText)}
  val replay=btn("↻  REPLAY",false){speakAnnouncement(latestSpokenText)}
  audioRow.addView(play,LinearLayout.LayoutParams(0,dp(48),1f).apply{rightMargin=dp(4)})
  audioRow.addView(replay,LinearLayout.LayoutParams(0,dp(48),1f).apply{leftMargin=dp(4)})
  audioRow.visibility=View.GONE
  announcement.addView(audioRow)
  v.addView(announcement)

  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->
   if(r.ok){
    val a=JSONObject(r.body).optJSONArray("announcements")
    if(a!=null&&a.length()>0){
     val x=a.getJSONObject(0)
     latestTitle.text=x.optString("title","Official Announcement").uppercase()
     latestBody.text=x.optString("body")
     latestSpokenText=x.optString("body")
     latestBy.text="By: ${x.optString("senderName","NATVA Official")}"
     timeLabel.text=x.optString("createdAt")
     val urgent=x.optString("priority").equals("urgent",true)
     criticalBadge.visibility=if(urgent)View.VISIBLE else View.GONE
     audioRow.visibility=if(x.optBoolean("audioEnabled",false))View.VISIBLE else View.GONE
    }else{
     latestTitle.text="NO ANNOUNCEMENTS YET"
     latestBody.text="Official NATVA race-day updates will appear here."
     latestBy.text=""
    }
   }else{
    latestTitle.text="ANNOUNCEMENTS UNAVAILABLE"
    latestBody.text="Check your connection and try again."
    latestBy.text=""
   }
  }

  v.addView(sectionHeader("MY TEAM"))
  val teamCard=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL
   setPadding(dp(12),dp(11),dp(12),dp(11));background=rounded(Color.WHITE,10,1,line)
   setOnClickListener{showTeam()}
  }
  teamCard.addView(ImageView(this).apply{
   setImageResource(R.drawable.ic_home);imageTintList=android.content.res.ColorStateList.valueOf(blue)
   contentDescription="My Team";setPadding(dp(5),dp(8),dp(5),dp(8))
  },LinearLayout.LayoutParams(dp(38),dp(42)))
  val teamWords=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  teamWords.addView(txt("My Team",16f,true))
  teamWords.addView(txt("Riders • Family/Crew • Notifications",11f).apply{setTextColor(muted)})
  teamCard.addView(teamWords,LinearLayout.LayoutParams(0,-2,1f))
  teamCard.addView(txt("›",24f,true).apply{gravity=Gravity.CENTER})
  v.addView(teamCard)

  if(role in listOf("announcer","admin","super_admin")){
   v.addView(sectionHeader("RACE-DAY TOOLS"))
   val tools=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
   tools.addView(btn("Announcement"){showAnnouncementComposer()},LinearLayout.LayoutParams(0,dp(50),1f).apply{rightMargin=dp(3)})
   tools.addView(btn("Message",false){composeMessage()},LinearLayout.LayoutParams(0,dp(50),1f).apply{leftMargin=dp(2);rightMargin=dp(2)})
   tools.addView(btn("Control",false){showRaceControl()},LinearLayout.LayoutParams(0,dp(50),1f).apply{leftMargin=dp(3)})
   v.addView(tools)
  }
  if(role in listOf("admin","super_admin"))v.addView(btn("Open Admin Dashboard",false){showAdminRoles()})

  v.addView(bottomNav("Home"))
  footer(v)
 }
 private fun navItem(iconRes:Int,label:String,active:Boolean,onTap:()->Unit):LinearLayout{
  return LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   gravity=Gravity.CENTER
   setPadding(dp(2),dp(3),dp(2),dp(3))
   if(active)background=rounded(Color.rgb(255,244,245),8)
   addView(ImageView(this@MainActivity).apply{
    setImageResource(iconRes)
    imageTintList=android.content.res.ColorStateList.valueOf(if(active)red else navy)
    contentDescription=label
   },LinearLayout.LayoutParams(dp(22),dp(22)))
   addView(txt(label,8.8f,active).apply{
    gravity=Gravity.CENTER
    setTextColor(if(active)red else navy)
    includeFontPadding=false
   })
   setOnClickListener{onTap()}
  }
 }

 private fun bottomNav(active:String):LinearLayout{
  val row=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   weightSum=5f
   setPadding(0,dp(3),0,0)
   background=rounded(Color.WHITE,8,1,Color.rgb(216,223,230))
   layoutParams=LinearLayout.LayoutParams(-1,dp(58)).apply{setMargins(0,dp(7),0,0)}
  }
  val items=listOf(
   Triple("Home",R.drawable.ic_home){resumeSession()},
   Triple("Announcements",R.drawable.ic_announcement){showAnnouncements()},
   Triple("Messages",R.drawable.ic_message){showMessages()},
   Triple("Audio",R.drawable.ic_audio){showAudio()},
   Triple("Settings",R.drawable.ic_settings){showSettings()}
  )
  items.forEach{item->
   row.addView(navItem(item.second,item.first,item.first==active,item.third),LinearLayout.LayoutParams(0,-1,1f))
  }
  return row
 }
 private fun showAnnouncements(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("Announcements",20f,true).apply{gravity=Gravity.CENTER})
  v.addView(txt("Official NATVA race-day communication",10.5f).apply{gravity=Gravity.CENTER;setTextColor(muted)})

  var filter="All"
  var loaded:org.json.JSONArray?=null
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}

  fun render(){
   box.removeAllViews()
   val a=loaded
   if(a==null){box.addView(emptyState("Loading announcements","Please wait."));return}
   var shown=0
   for(i in 0 until a.length()){
    val x=a.getJSONObject(i)
    val unread=!x.optBoolean("isRead",false) && x.optInt("isRead",0)==0
    val official=x.optString("senderRole","").lowercase() in listOf("announcer","admin","super_admin") || x.optBoolean("official",true)
    val include=when(filter){
     "Unread"->unread
     "Official"->official
     else->true
    }
    if(!include)continue
    shown++
    val c=LinearLayout(this).apply{
     orientation=LinearLayout.HORIZONTAL
     gravity=Gravity.TOP
     setPadding(dp(8),dp(9),dp(8),dp(9))
     background=rounded(Color.WHITE,8,1,line)
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
    val meta=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
    meta.addView(txt(x.optString("senderName","Track Official"),12f,true),LinearLayout.LayoutParams(0,-2,1f))
    meta.addView(pill(if(x.optInt("isCritical",0)==1)"CRITICAL" else "OFFICIAL",if(x.optInt("isCritical",0)==1)red else blue))
    words.addView(meta)
    words.addView(txt(x.optString("title","Official Announcement"),14f,true))
    words.addView(txt(x.optString("body"),12f).apply{setTextColor(Color.rgb(55,63,72))})
    val whenSent=x.optString("createdAt")
    if(whenSent.isNotBlank())words.addView(txt(whenSent,10f).apply{setTextColor(muted)})
    c.addView(words,LinearLayout.LayoutParams(0,-2,1f))
    val announcementId=x.optInt("id",0)
    c.setOnClickListener{
     if(announcementId>0){
      val payload=JSONObject().put("announcementId",announcementId)
      network({api.post(ApiRoutes.ANNOUNCEMENT_READ,payload.toString())}){mark->
       if(mark.ok){toast("Marked read");x.put("isRead",true);render()}
      }
     }
    }
    box.addView(c,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(3))})
   }
   if(shown==0){
    val msg=if(filter=="Unread")"No unread announcements" else if(filter=="Official")"No official announcements" else "No announcements yet"
    box.addView(emptyState(msg,"Official NATVA messages will appear here."))
   }
  }

  val filters=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  val chips=mutableListOf<TextView>()
  listOf("All","Unread","Official").forEachIndexed{i,name->
   val chip=TextView(this).apply{
    text=name
    textSize=11f
    gravity=Gravity.CENTER
    setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setOnClickListener{
     filter=name
     chips.forEach{c->
      val active=c.text.toString()==filter
      c.setTextColor(if(active)Color.WHITE else navy)
      c.background=rounded(if(active)red else Color.rgb(239,243,247),12,1,if(active)red else line)
     }
     render()
    }
   }
   chips.add(chip)
   filters.addView(chip,LinearLayout.LayoutParams(0,dp(36),1f).apply{if(i<2)rightMargin=dp(4)})
  }
  v.addView(filters)
  v.addView(box)
  chips.first().performClick()

  network({api.get(ApiRoutes.ANNOUNCEMENTS)}){r->
   loaded=if(r.ok)JSONObject(r.body).optJSONArray("announcements") else org.json.JSONArray()
   if(!r.ok)toast(jsonError(r.body,"Unable to load announcements"))
   render()
  }
  v.addView(bottomNav("Announcements"))
  footer(v)
 }
 private fun showMessages(){
  val v=root()
  header(v)
  raceBar(v)
  val title=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  val messageTitle=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  messageTitle.addView(txt("Messages",20f,true))
  messageTitle.addView(txt("Private PitBuzz communication",10.5f).apply{setTextColor(muted)})
  title.addView(messageTitle,LinearLayout.LayoutParams(0,-2,1f))
  title.addView(TextView(this).apply{
   text="+"
   textSize=22f
   gravity=Gravity.CENTER
   setTextColor(Color.WHITE)
   background=rounded(red,30)
   setOnClickListener{composeMessage()}
  },LinearLayout.LayoutParams(dp(38),dp(38)))
  v.addView(title)

  var filter="All"
  var loaded:org.json.JSONArray?=null
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}

  fun render(){
   box.removeAllViews()
   val a=loaded
   if(a==null){box.addView(emptyState("Loading messages","Please wait."));return}
   var shown=0
   for(i in 0 until a.length()){
    val x=a.getJSONObject(i)
    val unread=x.isNull("readAt") || x.optString("readAt").isBlank()
    val category=x.optString("category",x.optString("type","")).lowercase()
    val include=when(filter){
     "Unread"->unread
     "Official"->category=="official" || x.optString("senderRole","").lowercase() in listOf("announcer","admin","super_admin")
     "Team"->category=="team" || x.optBoolean("teamMessage",false)
     else->true
    }
    if(!include)continue
    shown++
    val c=LinearLayout(this).apply{
     orientation=LinearLayout.HORIZONTAL
     gravity=Gravity.CENTER_VERTICAL
     setPadding(dp(7),dp(9),dp(7),dp(9))
     background=rounded(Color.WHITE,7,1,line)
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
    c.setOnClickListener{
     x.put("readAt",java.time.LocalDateTime.now().toString())
     val messageId=x.optInt("id",0)
     if(messageId>0)network({api.post(ApiRoutes.MESSAGE_READ,JSONObject().put("messageId",messageId).toString())}){_->}
     val sender=x.optString("senderName","Private Message")
     android.app.AlertDialog.Builder(this)
      .setTitle(sender)
      .setMessage(x.optString("body"))
      .setPositiveButton("Close",null)
      .setNeutralButton("Mark Unread"){_,_->
       markMessageUnread(messageId){showMessages()}
      }
      .setNegativeButton("Delete"){_,_->
       deleteMessage(messageId,sender){showMessages()}
      }
      .show()
     render()
    }
    if(unread)c.addView(TextView(this).apply{text="●";textSize=9f;gravity=Gravity.CENTER;setTextColor(blue)},LinearLayout.LayoutParams(dp(18),dp(28)))
    box.addView(c,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(2),0,dp(2))})
   }
   if(shown==0)box.addView(emptyState("No $filter messages","Messages matching this filter will appear here.","New Message"){composeMessage()})
  }

  val filters=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=4f}
  val chips=mutableListOf<TextView>()
  listOf("All","Unread","Official","Team").forEachIndexed{i,name->
   val chip=TextView(this).apply{
    text=name;textSize=10f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setOnClickListener{
     filter=name
     chips.forEach{c->
      val active=c.text.toString()==filter
      c.setTextColor(if(active)Color.WHITE else navy)
      c.background=rounded(if(active)red else Color.rgb(239,243,247),12)
     }
     render()
    }
   }
   chips.add(chip)
   filters.addView(chip,LinearLayout.LayoutParams(0,dp(34),1f).apply{if(i<3)rightMargin=dp(3)})
  }
  v.addView(filters)
  v.addView(box)
  chips.first().performClick()

  network({api.get(ApiRoutes.MESSAGES)}){r->
   loaded=if(r.ok)JSONObject(r.body).optJSONArray("messages") else org.json.JSONArray()
   if(!r.ok)toast(jsonError(r.body,"Unable to load messages"))
   render()
  }
  v.addView(bottomNav("Messages"))
  footer(v)
 }
 private fun markMessageUnread(messageId:Int,onDone:()->Unit){
  if(messageId<=0)return
  network({api.post(ApiRoutes.MESSAGE_READ,JSONObject().put("messageId",messageId).put("action","unread").toString())}){r->
   if(r.ok){toast("Marked unread");onDone()}
   else toast(jsonError(r.body,"Could not mark unread"))
  }
 }

 private fun deleteMessage(messageId:Int,senderName:String,onDeleted:()->Unit){
  android.app.AlertDialog.Builder(this)
   .setTitle("Delete Message?")
   .setMessage("Delete this private message from PitBuzz? During beta this removes the message for both participants.")
   .setPositiveButton("Delete"){_,_->
    val key="delete-message-$messageId"
    if(!submitGuard.begin(key))return@setPositiveButton
    network({api.post(ApiRoutes.MESSAGE_DELETE,JSONObject().put("messageId",messageId).toString())}){r->
     submitGuard.finish(key)
     if(r.ok){toast("Message deleted");onDeleted()}
     else toast(jsonError(r.body,"Could not delete message"))
    }
   }
   .setNegativeButton("Cancel",null)
   .show()
 }

 private fun composeMessage(){
  val wrap=LinearLayout(this).apply{
   orientation=LinearLayout.VERTICAL
   setPadding(dp(18),dp(8),dp(18),0)
  }
  val q=field("Recipient name, number or phone")
  val body=field("Private message").apply{minLines=3;gravity=Gravity.TOP}
  wrap.addView(q);wrap.addView(body)

  val dialog=android.app.AlertDialog.Builder(this)
   .setTitle("New Private Message")
   .setView(wrap)
   .setPositiveButton("Find Recipient",null)
   .setNegativeButton("Cancel",null)
   .create()

  dialog.setOnShowListener{
   dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener{
    val query=q.text.toString().trim()
    val message=body.text.toString().trim()
    if(query.length<2){toast("Enter at least 2 characters for the recipient");return@setOnClickListener}
    if(message.isBlank()){toast("Enter a private message");return@setOnClickListener}

    network({api.get(ApiRoutes.USERS+"?q="+java.net.URLEncoder.encode(query,"UTF-8"))}){r->
     if(!r.ok){toast(jsonError(r.body,"Recipient search failed"));return@network}
     val a=JSONObject(r.body).optJSONArray("users")
     if(a==null||a.length()==0){toast("No matching PitBuzz user");return@network}

     val labels=Array(a.length()){""}
     for(i in 0 until a.length()){
      val u=a.getJSONObject(i)
      val number=u.optString("raceNumber")
      val phone=u.optString("phone")
      labels[i]=buildString{
       append(u.optString("name","PitBuzz User"))
       if(number.isNotBlank())append("  #").append(number)
       else if(phone.isNotBlank())append("  ").append(phone)
      }
     }

     android.app.AlertDialog.Builder(this)
      .setTitle("Choose Recipient")
      .setItems(labels){_,which->
       val u=a.getJSONObject(which)
       if(!submitGuard.begin("private-message"))return@setItems
       val payload=JSONObject()
        .put("recipientUserId",u.getInt("id"))
        .put("body",message)
       network({api.post(ApiRoutes.MESSAGES,payload.toString())}){sr->
        submitGuard.finish("private-message")
        if(sr.ok){
         dialog.dismiss()
         toast("Private message sent to ${u.optString("name","user")}")
         showMessages()
        }else toast(jsonError(sr.body,"Message failed"))
       }
      }
      .setNegativeButton("Cancel",null)
      .show()
    }
   }
  }
  dialog.show()
 }
 private fun speakAnnouncement(text:String){
  if(text.isBlank()){toast("Nothing to play");return}
  if(!ttsReady){toast("Audio is still getting ready");return}
  tts?.stop()
  tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"pitbuzz-announcement")
 }

 private fun notificationsAllowed():Boolean{
  if(android.os.Build.VERSION.SDK_INT<33)return true
  return checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)==android.content.pm.PackageManager.PERMISSION_GRANTED
 }

 private fun openNotificationSettings(){
  val intent=android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply{
   putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,packageName)
  }
  startActivity(intent)
 }

 private fun sendLocalTestNotification(){
  if(!notificationsAllowed()){
   if(android.os.Build.VERSION.SDK_INT>=33)requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),2001)
   toast("Allow notifications, then try the test again")
   return
  }
  val nm=getSystemService(android.app.NotificationManager::class.java)
  val note=androidx.core.app.NotificationCompat.Builder(this,NotificationChannels.ANNOUNCEMENTS)
   .setSmallIcon(R.drawable.ic_stat_pitbuzz)
   .setContentTitle("PitBuzz Test")
   .setContentText("Visual notifications are working on this device.")
   .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText("Visual notifications are working on this device. This is a local device test, not a Firebase delivery test."))
   .setAutoCancel(true)
   .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
   .build()
  nm.notify(2701,note)
  toast("Test notification sent")
 }

 private fun showAudio(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("Audio",22f,true))
  v.addView(txt("Spoken announcements and race alerts",10.5f).apply{setTextColor(muted)})

  val status=card()
  status.addView(txt("DEVICE READINESS",11f,true).apply{setTextColor(red)})
  val allowed=notificationsAllowed()
  status.addView(txt(if(allowed)"Notifications allowed" else "Notifications need permission",14f,true))
  status.addView(txt(if(allowed)"This phone can display PitBuzz notifications." else "PitBuzz cannot show race-day notifications until Android permission is enabled.",11f).apply{setTextColor(muted)})
  val pushRegistered=cache.getBoolean("push_registered",false)
  status.addView(txt(if(pushRegistered)"Push device registered with PitBuzz" else "Push device registration not yet confirmed",10.5f,true).apply{setTextColor(if(pushRegistered)Color.rgb(27,155,72) else muted)})
  if(!allowed)status.addView(btn("Allow Notifications",false){
   if(android.os.Build.VERSION.SDK_INT>=33)requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),2001)
  })
  status.addView(btn("Send Test Notification",false){sendLocalTestNotification()})
  v.addView(status)

  val prefs=card()
  prefs.addView(txt("PITBUZZ AUDIO PREFERENCES",11f,true).apply{setTextColor(red)})
  val autoplay=Switch(this).apply{text="Auto-play official announcements";setTextColor(navy)}
  val sound=Switch(this).apply{text="Notification sound";setTextColor(navy)}
  prefs.addView(autoplay)
  prefs.addView(sound)
  prefs.addView(txt("Private messages never auto-play aloud.",10.5f,true).apply{setTextColor(red)})
  val save=btn("Save Audio Preferences"){
   if(!submitGuard.begin("audio-settings"))return@btn
   val payload=JSONObject()
    .put("announcementAutoplay",autoplay.isChecked)
    .put("notificationSound",sound.isChecked)
   network({api.post(ApiRoutes.SETTINGS,payload.toString())}){r->
    submitGuard.finish("audio-settings")
    if(r.ok)toast("Audio preferences saved")
    else toast(jsonError(r.body,"Could not save preferences"))
   }
  }
  prefs.addView(save)
  v.addView(prefs)

  network({api.get(ApiRoutes.SETTINGS)}){r->
   if(r.ok){
    val j=JSONObject(r.body)
    autoplay.isChecked=j.optInt("announcementAutoplay",1)==1 || j.optBoolean("announcementAutoplay",false)
    sound.isChecked=j.optInt("notificationSound",1)==1 || j.optBoolean("notificationSound",false)
   }
  }

  val behavior=card()
  behavior.addView(txt("RACE ALERTS",11f,true).apply{setTextColor(muted)})
  behavior.addView(txt("Race, staging and get-ready alerts use Android notification settings.",12f))
  behavior.addView(btn("Android Notification Settings",false){openNotificationSettings()})
  v.addView(behavior)

  val warning=card()
  warning.addView(txt("BETA NOTE",10.5f,true).apply{setTextColor(red)})
  warning.addView(txt("The local test verifies this phone can display notifications. It does not prove Firebase push delivery from the PitBuzz server.",10.5f).apply{setTextColor(muted)})
  v.addView(warning)

  v.addView(bottomNav("Audio"))
  footer(v)
 }
 private fun showSettings(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("Settings",22f,true))

  val account=card()
  account.addView(txt("ACCOUNT",11f,true).apply{setTextColor(muted)})
  account.addView(txt(currentUser?.optString("name","PitBuzz User")?:"PitBuzz User",16f,true))
  val email=currentUser?.optString("email","")?:""
  val phone=currentUser?.optString("phone","")?:""
  if(email.isNotBlank())account.addView(txt(email,12f).apply{setTextColor(muted)})
  if(phone.isNotBlank())account.addView(txt(phone,12f).apply{setTextColor(muted)})
  v.addView(account)

  val notify=card()
  notify.addView(txt("NOTIFICATIONS & AUDIO",11f,true).apply{setTextColor(muted)})
  val notificationReady=notificationsAllowed()
  notify.addView(txt(if(notificationReady)"DEVICE READY FOR NOTIFICATIONS" else "NOTIFICATIONS NEED ATTENTION",12f,true).apply{setTextColor(if(notificationReady)Color.rgb(27,155,72) else red)})
  notify.addView(txt("Announcement auto-play and notification sound preferences are saved to your PitBuzz account. Private messages never auto-play aloud.",12f))
  notify.addView(btn("Open Audio & Notification Controls",false){showAudio()})
  v.addView(notify)

  val about=card()
  about.addView(txt("PITBUZZ BETA",11f,true).apply{setTextColor(muted)})
  about.addView(txt("NATVA race-day communications beta",13f,true))
  about.addView(txt("Server: team.pitbuzz.app",11f).apply{setTextColor(muted)})
  v.addView(about)

  v.addView(btn("Sign Out",false){
   network({api.post(ApiRoutes.LOGOUT,"{}")}){_->
    api.token=null
    currentUser=null
    showLogin()
   }
  })
  v.addView(bottomNav("Settings"))
  footer(v)
 }
 private fun showTeam(){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("My Team",20f,true).apply{gravity=Gravity.CENTER})
  v.addView(txt("Riders • Family/Crew • Team notifications",10.5f).apply{gravity=Gravity.CENTER;setTextColor(muted)})

  val tabs=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  val chips=mutableListOf<TextView>()
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  var teamData:JSONObject?=null
  var selected="Riders"

  fun render(){
   box.removeAllViews()
   val j=teamData
   if(j==null){box.addView(emptyState("Loading team","Please wait."));return}
   if(j.isNull("team")){
    box.addView(emptyState("You are not on a team yet","Create a team to manage riders, family and crew.","Create My Team"){createTeam()})
    return
   }
   val canManage=j.optBoolean("canManage")
   when(selected){
    "Riders"->{
     val riders=j.optJSONArray("riders")
     val count=riders?.length()?:0
     box.addView(txt("$count rider${if(count==1)"" else "s"} on this team",11f,true).apply{setTextColor(muted)})
     if(riders!=null)for(i in 0 until riders.length()){
      val x=riders.getJSONObject(i)
      val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(8),dp(8),dp(8),dp(8));background=rounded(Color.WHITE,8,1,line)}
      val num=x.optString("raceNumber","—")
      row.addView(TextView(this).apply{text=num;textSize=15f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD);setTextColor(Color.WHITE);background=rounded(listOf(red,Color.rgb(144,41,178),blue)[i%3],26)},LinearLayout.LayoutParams(dp(48),dp(48)))
      val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(8),0,0,0)}
      words.addView(txt(x.optString("name","Rider"),15f,true))
      words.addView(txt(x.optString("classes","Rider"),11f).apply{setTextColor(muted)})
      row.addView(words,LinearLayout.LayoutParams(0,-2,1f))
      if(canManage){
       row.addView(txt("Classes ›",10.5f,true).apply{setTextColor(blue)})
       row.setOnClickListener{manageRiderClasses(x.optInt("id"),x.optString("name","Rider"))}
       row.setOnLongClickListener{removeRider(x.optInt("id"),x.optString("name","Rider"));true}
      }
      box.addView(row,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(2),0,dp(2))})
     }
     if(canManage){
      box.addView(txt("Tap a rider to manage classes • Long-press a managed rider to remove",9.5f).apply{setTextColor(muted)})
      box.addView(btn("+ Add Rider"){addRider()})
     }
    }
    "Family/Crew"->{
     val members=j.optJSONArray("members")
     val count=members?.length()?:0
     box.addView(txt("$count family/crew member${if(count==1)"" else "s"}",11f,true).apply{setTextColor(muted)})
     if(members!=null)for(i in 0 until members.length()){
      val m=members.getJSONObject(i)
      val row=card()
      row.addView(txt(m.optString("name",m.optString("phone","Crew Member")),14f,true))
      val p=m.optString("phone")
      if(p.isNotBlank())row.addView(txt(p,11f).apply{setTextColor(muted)})
      if(canManage){
       row.addView(txt("Tap to choose which riders this person follows",10f,true).apply{setTextColor(blue)})
       row.setOnClickListener{manageMemberRiders(m.optInt("id"),m.optString("name","Crew Member"))}
       if(m.optString("role")!="owner")row.setOnLongClickListener{removeMember(m.optInt("id"),m.optString("name","Crew Member"));true}
      }
      box.addView(row)
     }
     if(canManage){
      box.addView(txt("Tap to choose followed riders • Long-press to remove",9.5f).apply{setTextColor(muted)})
      box.addView(btn("+ Add Family/Crew"){addMember()})
     }
    }
    "Settings"->{
     val team=j.optJSONObject("team")
     val c=card()
     c.addView(txt("TEAM SETTINGS",11f,true).apply{setTextColor(red)})
     c.addView(txt(team?.optString("name","My Team")?:"My Team",16f,true))
     c.addView(txt(if(canManage)"Team Owner/Admin controls are available below." else "Team settings are managed by the Team Owner/Admin.",11f).apply{setTextColor(muted)})
     box.addView(c)
     val rows=listOf(
      "Manage Family/Crew" to {addMember()},
      "Class Subscriptions" to {selected="Riders";chips.first().performClick()},
      "Notification Settings" to {showAudio()}
     )
     rows.forEach{item->
      val row=LinearLayout(this).apply{
       orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL
       setPadding(dp(10),dp(9),dp(10),dp(9));background=rounded(Color.WHITE,8,1,line)
       setOnClickListener{item.second()}
      }
      row.addView(txt(item.first,12f,true),LinearLayout.LayoutParams(0,-2,1f))
      row.addView(txt("›",20f,true))
      box.addView(row,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(2),0,dp(2))})
     }
    }
   }
  }

  listOf("Riders","Family/Crew","Settings").forEachIndexed{i,name->
   val chip=TextView(this).apply{
    text=name;textSize=11f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setOnClickListener{
     selected=name
     chips.forEach{c->
      val active=c.text.toString()==selected
      c.setTextColor(if(active)Color.WHITE else navy)
      c.background=rounded(if(active)red else Color.rgb(239,243,247),10)
     }
     render()
    }
   }
   chips.add(chip)
   tabs.addView(chip,LinearLayout.LayoutParams(0,dp(38),1f).apply{if(i<2)rightMargin=dp(4)})
  }
  v.addView(tabs)
  v.addView(box)
  chips.first().performClick()

  network({api.get(ApiRoutes.TEAM)}){r->
   teamData=if(r.ok)JSONObject(r.body) else JSONObject().put("team",JSONObject.NULL)
   if(!r.ok)toast(jsonError(r.body,"Unable to load team"))
   render()
  }
  v.addView(bottomNav("Home"))
  footer(v)
 }
 private fun removeRider(riderId:Int,riderName:String){
  android.app.AlertDialog.Builder(this)
   .setTitle("Remove Rider?")
   .setMessage("Remove $riderName from this team? This only applies to managed/dependent rider profiles.")
   .setPositiveButton("Remove"){_,_->
    if(!submitGuard.begin("remove-rider-$riderId"))return@setPositiveButton
    val payload=JSONObject().put("action","remove_rider").put("riderId",riderId)
    network({api.post(ApiRoutes.TEAM_ACTIONS,payload.toString())}){r->
     submitGuard.finish("remove-rider-$riderId")
     if(r.ok){toast("Rider removed");showTeam()}
     else toast(jsonError(r.body,"Could not remove rider"))
    }
   }
   .setNegativeButton("Cancel",null)
   .show()
 }

 private fun removeMember(memberId:Int,memberName:String){
  android.app.AlertDialog.Builder(this)
   .setTitle("Remove Family/Crew?")
   .setMessage("Remove $memberName from this team?")
   .setPositiveButton("Remove"){_,_->
    if(!submitGuard.begin("remove-member-$memberId"))return@setPositiveButton
    val payload=JSONObject().put("action","remove_member").put("memberId",memberId)
    network({api.post(ApiRoutes.TEAM_ACTIONS,payload.toString())}){r->
     submitGuard.finish("remove-member-$memberId")
     if(r.ok){toast("Member removed");showTeam()}
     else toast(jsonError(r.body,"Could not remove member"))
    }
   }
   .setNegativeButton("Cancel",null)
   .show()
 }

 private fun createTeam(){val input=field("Team name");android.app.AlertDialog.Builder(this).setTitle("Create Team").setView(input).setPositiveButton("Create"){_,_->val n=input.text.toString().trim();if(n.isBlank()){toast("Team name is required");return@setPositiveButton};if(!submitGuard.begin("team-create"))return@setPositiveButton;network({api.post(ApiRoutes.TEAM,JSONObject().put("action","create_team").put("name",n).toString())}){r->submitGuard.finish("team-create");if(r.ok)showTeam()else toast(jsonError(r.body,"Could not create team"))}}.setNegativeButton("Cancel",null).show()}
 private fun manageRiderClasses(riderId:Int,riderName:String){
  network({api.get(ApiRoutes.RIDER_CLASSES+"?riderId="+riderId)}){current->
   if(!current.ok){toast(jsonError(current.body,"Unable to load rider classes"));return@network}
   network({api.get(ApiRoutes.CLASSES)}){all->
    if(!all.ok){toast(jsonError(all.body,"Unable to load classes"));return@network}
    val selectedIds=mutableSetOf<Int>()
    val currentArray=JSONObject(current.body).optJSONArray("classes")
    if(currentArray!=null)for(i in 0 until currentArray.length())selectedIds.add(currentArray.getJSONObject(i).optInt("id"))

    val classes=JSONObject(all.body).optJSONArray("classes")
    if(classes==null||classes.length()==0){toast("No classes configured");return@network}
    val ids=IntArray(classes.length())
    val names=Array(classes.length()){""}
    val checked=BooleanArray(classes.length())
    for(i in 0 until classes.length()){
     val c=classes.getJSONObject(i)
     ids[i]=c.optInt("id")
     names[i]=c.optString("name")
     checked[i]=ids[i] in selectedIds
    }

    android.app.AlertDialog.Builder(this)
     .setTitle("Classes • $riderName")
     .setMultiChoiceItems(names,checked){_,which,isChecked->checked[which]=isChecked}
     .setPositiveButton("Save"){_,_->
      if(!submitGuard.begin("rider-classes-$riderId"))return@setPositiveButton
      val selected=org.json.JSONArray()
      for(i in ids.indices)if(checked[i])selected.put(ids[i])
      val payload=JSONObject().put("action","replace").put("riderId",riderId).put("classIds",selected)
      network({api.post(ApiRoutes.RIDER_CLASSES,payload.toString())}){r->
       submitGuard.finish("rider-classes-$riderId")
       if(r.ok){toast("Rider classes updated");showTeam()}
       else toast(jsonError(r.body,"Could not update rider classes"))
      }
     }
     .setNegativeButton("Cancel",null)
     .show()
   }
  }
 }

 private fun manageMemberRiders(memberId:Int,memberName:String){
  network({api.get(ApiRoutes.TEAM)}){r->
   if(!r.ok){toast(jsonError(r.body,"Unable to load team"));return@network}
   val j=JSONObject(r.body)
   val riders=j.optJSONArray("riders")
   if(riders==null||riders.length()==0){toast("Add a rider first");return@network}
   val ids=IntArray(riders.length())
   val names=Array(riders.length()){""}
   val checked=BooleanArray(riders.length())
   for(i in 0 until riders.length()){
    val x=riders.getJSONObject(i);ids[i]=x.optInt("id");names[i]="#${x.optString("raceNumber","—")} ${x.optString("name","Rider")}"
   }
   android.app.AlertDialog.Builder(this)
    .setTitle("Riders for $memberName")
    .setMultiChoiceItems(names,checked){_,which,isChecked->checked[which]=isChecked}
    .setPositiveButton("Save"){_,_->
     if(!submitGuard.begin("member-riders-$memberId"))return@setPositiveButton
     val selected=org.json.JSONArray()
     for(i in ids.indices)if(checked[i])selected.put(ids[i])
     val payload=JSONObject().put("memberId",memberId).put("riderIds",selected).put("followsAllRiders",selected.length()==0)
     network({api.post(ApiRoutes.MEMBER_RIDERS,payload.toString())}){save->
      submitGuard.finish("member-riders-$memberId")
      if(save.ok){toast("Crew rider access updated");showTeam()}
      else toast(jsonError(save.body,"Could not update crew riders"))
     }
    }
    .setNegativeButton("Cancel",null)
    .show()
  }
 }

 private fun addRider(){val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Rider name");val num=field("Race number");wrap.addView(n);wrap.addView(num);android.app.AlertDialog.Builder(this).setTitle("Add Rider").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isBlank()){toast("Rider name is required");return@setPositiveButton};if(!submitGuard.begin("team-rider"))return@setPositiveButton;network({api.post(ApiRoutes.TEAM,JSONObject().put("action","add_rider").put("name",n.text.toString().trim()).put("raceNumber",num.text.toString().trim()).toString())}){r->submitGuard.finish("team-rider");if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add rider"))}}.setNegativeButton("Cancel",null).show()}
 private fun addMember(){
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),0,dp(20),0)};val n=field("Name");val ph=field("Phone number (optional)");val role=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,listOf("Family","Crew"))};wrap.addView(n);wrap.addView(ph);wrap.addView(role)
  android.app.AlertDialog.Builder(this).setTitle("Add Family / Crew").setView(wrap).setPositiveButton("Add"){_,_->if(n.text.isBlank()){toast("Name is required");return@setPositiveButton};if(!submitGuard.begin("team-member"))return@setPositiveButton;val j=JSONObject().put("action","add_member").put("name",n.text.toString().trim()).put("phone",ph.text.toString().trim()).put("role",if(role.selectedItemPosition==0)"family" else "crew").put("followsAllRiders",true);network({api.post(ApiRoutes.TEAM,j.toString())}){r->submitGuard.finish("team-member");if(r.ok)showTeam()else toast(jsonError(r.body,"Could not add member"))}}.setNegativeButton("Cancel",null).show()
 }
 private fun sendPreset(title:String, body:String, priority:String="normal"){
  val payload=JSONObject()
   .put("targetType","all")
   .put("title",title)
   .put("body",body)
   .put("priority",priority)
   .put("audioEnabled",true)
  network({api.post(ApiRoutes.ANNOUNCEMENTS,payload.toString())}){r->
   if(r.ok){toast("Announcement sent");showAnnouncements()}
   else toast(jsonError(r.body,"Announcement failed"))
  }
 }

 private fun chooseClass(onChosen:(Int,String)->Unit){
  network({api.get(ApiRoutes.CLASSES)}){r->
   if(!r.ok){toast(jsonError(r.body,"Unable to load classes"));return@network}
   val a=JSONObject(r.body).optJSONArray("classes")
   if(a==null || a.length()==0){toast("No classes are configured yet");return@network}
   val ids=IntArray(a.length())
   val names=Array(a.length()){""}
   for(i in 0 until a.length()){
    val x=a.getJSONObject(i)
    ids[i]=x.optInt("id")
    names[i]=x.optString("name")
   }
   android.app.AlertDialog.Builder(this)
    .setTitle("Choose Class")
    .setItems(names){_,which->onChosen(ids[which],names[which])}
    .setNegativeButton("Cancel",null)
    .show()
  }
 }

 private fun chooseRider(onChosen:(Int,String,String)->Unit){
  val input=field("Rider name or race number")
  val wrap=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(8),dp(18),0);addView(input)}
  val dialog=android.app.AlertDialog.Builder(this)
   .setTitle("Find Rider")
   .setView(wrap)
   .setPositiveButton("Search",null)
   .setNegativeButton("Cancel",null)
   .create()
  dialog.setOnShowListener{
   dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener{
    val q=input.text.toString().trim()
    if(q.isBlank()){toast("Enter a rider name or race number");return@setOnClickListener}
    network({api.get(ApiRoutes.RIDERS+"?q="+java.net.URLEncoder.encode(q,"UTF-8"))}){r->
     if(!r.ok){toast(jsonError(r.body,"Rider search failed"));return@network}
     val a=JSONObject(r.body).optJSONArray("riders")
     if(a==null||a.length()==0){toast("No matching riders");return@network}
     val labels=Array(a.length()){""}
     for(i in 0 until a.length()){
      val x=a.getJSONObject(i)
      labels[i]="#${x.optString("raceNumber","—")}  ${x.optString("name","Rider")}  •  ${x.optString("teamName","")}"
     }
     android.app.AlertDialog.Builder(this)
      .setTitle("Choose Rider")
      .setItems(labels){_,which->
       val x=a.getJSONObject(which)
       dialog.dismiss()
       onChosen(x.getInt("id"),x.optString("name","Rider"),x.optString("raceNumber",""))
      }
      .setNegativeButton("Cancel",null)
      .show()
    }
   }
  }
  dialog.show()
 }

 private fun showAnnouncementComposer(
  presetTitle:String="",
  presetBody:String=""
 ){
  val v=root()
  header(v)
  raceBar(v)
  v.addView(txt("New Announcement",20f,true))
  v.addView(txt("Send an official PitBuzz race-day update",10.5f).apply{setTextColor(muted)})
  v.addView(txt("TARGET",11f,true).apply{setTextColor(red)})

  var targetType="all"
  var classId:Int?=null
  var riderId:Int?=null
  var targetLabel="Everyone"

  val targetRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  val everyone=TextView(this)
  val classBtn=TextView(this)
  val riderBtn=TextView(this)
  val targetButtons=listOf(everyone,classBtn,riderBtn)

  fun paintTargets(){
   targetButtons.forEachIndexed{i,t->
    val active=(i==0&&targetType=="all")||(i==1&&targetType=="class")||(i==2&&targetType=="rider")
    t.setTextColor(if(active)Color.WHITE else navy)
    t.background=rounded(if(active)(if(i==0)red else blue) else Color.WHITE,9,1,if(active)(if(i==0)red else blue) else line)
   }
  }

  everyone.apply{
   text="Everyone";textSize=11f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setOnClickListener{targetType="all";classId=null;riderId=null;targetLabel="Everyone";paintTargets()}
  }
  classBtn.apply{
   text="Class";textSize=11f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setOnClickListener{
    chooseClass{id,name->
     targetType="class";classId=id;riderId=null;targetLabel=name;text="Class\n$name";paintTargets()
    }
   }
  }
  riderBtn.apply{
   text="Rider";textSize=11f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
   setOnClickListener{
    chooseRider{id,name,number->
     targetType="rider";riderId=id;classId=null;targetLabel=name;text="Rider\n#$number $name";paintTargets()
    }
   }
  }
  targetRow.addView(everyone,LinearLayout.LayoutParams(0,dp(56),1f).apply{rightMargin=dp(3)})
  targetRow.addView(classBtn,LinearLayout.LayoutParams(0,dp(56),1f).apply{leftMargin=dp(2);rightMargin=dp(2)})
  targetRow.addView(riderBtn,LinearLayout.LayoutParams(0,dp(56),1f).apply{leftMargin=dp(3)})
  paintTargets()
  v.addView(targetRow)

  v.addView(txt("MESSAGE",11f,true).apply{setTextColor(red)})
  val title=field("Title").apply{setText(presetTitle)}
  val body=field("Announcement text").apply{setText(presetBody);minLines=3;gravity=Gravity.TOP}
  v.addView(title);v.addView(body)

  v.addView(txt("PRIORITY",11f,true).apply{setTextColor(red)})
  var priority="normal"
  val priorityRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  val priorityButtons=mutableListOf<TextView>()
  listOf("Normal" to "normal","Important" to "important","Urgent" to "urgent").forEachIndexed{i,p->
   val chip=TextView(this).apply{
    text=p.first;textSize=10.5f;gravity=Gravity.CENTER;setTypeface(Typeface.DEFAULT,Typeface.BOLD)
    setOnClickListener{
     priority=p.second
     priorityButtons.forEachIndexed{idx,c->
      val active=idx==i
      c.setTextColor(if(active)Color.WHITE else navy)
      c.background=rounded(if(active)(if(i==2)red else blue) else Color.WHITE,9,1,if(active)(if(i==2)red else blue) else line)
     }
    }
   }
   priorityButtons.add(chip)
   priorityRow.addView(chip,LinearLayout.LayoutParams(0,dp(42),1f).apply{if(i<2)rightMargin=dp(3)})
  }
  v.addView(priorityRow)
  priorityButtons.first().performClick()

  val audio=Switch(this).apply{text="Play Audio (Text-to-Speech)";isChecked=true;setTextColor(navy)}
  v.addView(audio)
  v.addView(txt("Private messages are handled separately and never auto-play aloud.",10f).apply{setTextColor(muted)})

  v.addView(btn("Send Announcement"){
   if(title.text.isBlank()||body.text.isBlank()){toast("Title and announcement text are required");return@btn}
   if(targetType=="class"&&classId==null){toast("Choose a class");return@btn}
   if(targetType=="rider"&&riderId==null){toast("Choose a rider");return@btn}
   if(!submitGuard.begin("announcement-send"))return@btn
   val payload=JSONObject()
    .put("targetType",targetType)
    .put("title",title.text.toString().trim())
    .put("body",body.text.toString().trim())
    .put("priority",priority)
    .put("audioEnabled",audio.isChecked)
   if(classId!=null)payload.put("classId",classId)
   if(riderId!=null)payload.put("riderId",riderId)
   network({api.post(ApiRoutes.ANNOUNCEMENTS,payload.toString())}){r->
    submitGuard.finish("announcement-send")
    if(r.ok){
     toast("Announcement sent to $targetLabel")
     showAnnouncements()
    }else toast(jsonError(r.body,"Announcement failed"))
   }
  })
  v.addView(bottomNav("Announcements"))
  footer(v)
 }
 private fun showRaceControl(){
  val v=root()
  header(v)
  raceBar(v)
  val titleRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
  titleRow.addView(txt("Race Control",22f,true),LinearLayout.LayoutParams(0,-2,1f))
  titleRow.addView(pill("AUTHORIZED",red))
  v.addView(titleRow)
  v.addView(txt("ADVANCE follows the official imported race order. Set / Correct is only for authorized corrections.",11f).apply{setTextColor(muted)})
  v.addView(txt("LIVE RACE STATUS",11f,true).apply{setTextColor(muted)})

  val cur=field("RACE #")
  val stage=field("STAGING")
  val ready=field("GET READY")
  listOf(cur,stage,ready).forEach(v::addView)

  val labelsCard=card()
  val labelsText=txt("Loading race labels…",10.5f).apply{setTextColor(muted)}
  labelsCard.addView(labelsText)
  v.addView(labelsCard)

  network({api.get(ApiRoutes.RACE_STATUS)}){r->
   if(r.ok){
    val j=JSONObject(r.body)
    cur.setText(j.optString("currentRace",""))
    stage.setText(j.optString("stagingRace",""))
    ready.setText(j.optString("getReadyRace",""))
    val parts=mutableListOf<String>()
    if(j.optString("currentLabel").isNotBlank())parts.add("RACE: ${j.optString("currentLabel")}")
    if(j.optString("stagingLabel").isNotBlank())parts.add("STAGING: ${j.optString("stagingLabel")}")
    if(j.optString("getReadyLabel").isNotBlank())parts.add("GET READY: ${j.optString("getReadyLabel")}")
    labelsText.text=if(parts.isEmpty())"No race labels loaded." else parts.joinToString("\n")
   }else labelsText.text="Race status unavailable."
  }

  v.addView(btn("ADVANCE"){
   android.app.AlertDialog.Builder(this)
    .setTitle("Advance Race?")
    .setMessage("This moves PitBuzz to the next race in the official race program.")
    .setPositiveButton("Advance"){_,_->
     if(!submitGuard.begin("race-advance"))return@setPositiveButton
     network({api.post(ApiRoutes.RACE_STATUS,JSONObject().put("action","advance").toString())}){r->
      submitGuard.finish("race-advance")
      if(r.ok){toast("Race advanced");showRaceControl()}
      else toast(jsonError(r.body,"Could not advance race"))
     }
    }
    .setNegativeButton("Cancel",null)
    .show()
  })

  v.addView(btn("Set / Correct",false){
   android.app.AlertDialog.Builder(this)
    .setTitle("Set / Correct Race Status?")
    .setMessage("Use this only when the displayed race status needs an authorized correction.")
    .setPositiveButton("Save Correction"){_,_->
     if(!submitGuard.begin("race-correct"))return@setPositiveButton
     val j=JSONObject()
      .put("action","set")
      .put("currentRace",cur.text.toString().trim().ifBlank{JSONObject.NULL})
      .put("stagingRace",stage.text.toString().trim().ifBlank{JSONObject.NULL})
      .put("getReadyRace",ready.text.toString().trim().ifBlank{JSONObject.NULL})
     network({api.post(ApiRoutes.RACE_STATUS,j.toString())}){r->
      submitGuard.finish("race-correct")
      if(r.ok){toast("Race status updated");showRaceControl()}
      else toast(jsonError(r.body,"Race status update failed"))
     }
    }
    .setNegativeButton("Cancel",null)
    .show()
  })
  v.addView(btn("Back Home",false){resumeSession()})
  footer(v)
 }
 private fun showAdminRoles(){
  val v=root()
  header(v)
  v.addView(txt("Admin Dashboard",20f,true).apply{gravity=Gravity.CENTER})
  v.addView(txt("PitBuzz race-day administration",10.5f).apply{gravity=Gravity.CENTER;setTextColor(muted)})
  val adminHero=LinearLayout(this).apply{
   orientation=LinearLayout.HORIZONTAL
   gravity=Gravity.CENTER_VERTICAL
   setPadding(dp(12),dp(10),dp(12),dp(10))
   background=rounded(navy,10)
  }
  val adminWords=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  adminWords.addView(txt("NATVA CONTROL",11f,true).apply{setTextColor(Color.rgb(174,215,247))})
  adminWords.addView(txt("Race-day operations are live",14f,true).apply{setTextColor(Color.WHITE)})
  adminHero.addView(adminWords,LinearLayout.LayoutParams(0,-2,1f))
  adminHero.addView(txt("ADMIN",11f,true).apply{setTextColor(Color.WHITE);background=rounded(red,18);setPadding(dp(10),dp(6),dp(10),dp(6))})
  v.addView(adminHero)
  val ok=connectedCard()
  (ok.getChildAt(1) as? LinearLayout)?.let{words->
   (words.getChildAt(0) as? TextView)?.text="ALL SYSTEMS ONLINE"
   (words.getChildAt(1) as? TextView)?.text="Everything is running smoothly"
  }
  v.addView(ok)
  v.addView(txt("QUICK ACTIONS",11f,true))
  v.addView(txt("Common race-day controls",10f).apply{setTextColor(muted)})
  val grid=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  val actions=listOf(
   "New\nAnnouncement" to {showAnnouncementComposer()},
   "Quick\nMessage" to {composeMessage()},
   "Race\nControl" to {showRaceControl()},
   "Announcement\nHistory" to {showAnnouncements()},
   "Users & Staff\nManage Access" to {showUserAccess()},
   "App Settings\nConfigure App" to {showSettings()}
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
     setTextColor(if((r*3+c)==0)Color.WHITE else navy)
     background=rounded(if((r*3+c)==0)red else Color.WHITE,9,1,if((r*3+c)==0)red else line)
     setOnClickListener{item.second()}
    }
    row.addView(tile,LinearLayout.LayoutParams(0,dp(82),1f).apply{
     if(c<2)rightMargin=dp(4)
    })
   }
   grid.addView(row,LinearLayout.LayoutParams(-1,-2).apply{if(r==0)bottomMargin=dp(4)})
  }
  v.addView(grid)
  v.addView(txt("SYSTEM STATUS",11f,true))
  val system=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(10),dp(8),dp(10),dp(8));background=rounded(Color.WHITE,9,1,line)}
  val statusPill=pill("CHECKING",blue)
  val statusText=txt("  Checking PitBuzz server…",11f,true)
  system.addView(statusPill)
  system.addView(statusText,LinearLayout.LayoutParams(0,-2,1f))
  v.addView(system)
  network({api.post(ApiRoutes.HEARTBEAT,JSONObject().put("platform","android").put("version","0.1.0-beta").toString())}){health->
   if(health.ok){
    statusPill.text="ONLINE"
    statusPill.background=rounded(Color.rgb(27,155,72),16)
    statusText.text="  API + NAVTADB connected"
   }else{
    statusPill.text="OFFLINE"
    statusPill.background=rounded(red,16)
    statusText.text="  PitBuzz server check failed"
   }
  }

  v.addView(txt("BETA DIAGNOSTICS",11f,true))
  val diagnostics=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  diagnostics.addView(txt("Loading beta diagnostics…",11f).apply{setTextColor(muted)})
  v.addView(diagnostics)
  network({api.get(ApiRoutes.ADMIN_DASHBOARD)}){d->
   diagnostics.removeAllViews()
   if(d.ok){
    val j=JSONObject(d.body)
    val row1=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
    listOf(
     "${j.optInt("users",0)}\nUSERS",
     "${j.optInt("teams",0)}\nTEAMS",
     "${j.optInt("riders",0)}\nRIDERS"
    ).forEachIndexed{i,label->
     row1.addView(txt(label,14f,true).apply{gravity=Gravity.CENTER;background=rounded(Color.WHITE,9,1,line)},LinearLayout.LayoutParams(0,dp(60),1f).apply{if(i<2)rightMargin=dp(3)})
    }
    diagnostics.addView(row1)
    val row2=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
    listOf(
     "${j.optInt("connectedDevices",0)}\nDEVICES",
     "${j.optInt("androidDevices",0)}\nANDROID",
     "${j.optInt("iosDevices",0)}\nIPHONE"
    ).forEachIndexed{i,label->
     row2.addView(txt(label,14f,true).apply{gravity=Gravity.CENTER;background=rounded(Color.WHITE,9,1,line)},LinearLayout.LayoutParams(0,dp(60),1f).apply{if(i<2)rightMargin=dp(3)})
    }
    diagnostics.addView(row2)
    if(!j.optBoolean("deliveryMetricsAvailable",false)){
     diagnostics.addView(txt("Pending/failed notification delivery receipts are not enabled on the server yet.",10f).apply{setTextColor(muted)})
    }
   }else diagnostics.addView(txt("Beta diagnostics unavailable.",11f).apply{setTextColor(muted)})
  }

  v.addView(txt("USERS & STAFF",11f,true))
  val countStrip=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;weightSum=3f}
  val totalUsers=txt("—\nUSERS",15f,true).apply{gravity=Gravity.CENTER;background=rounded(Color.WHITE,9,1,line)}
  val admins=txt("—\nADMINS",15f,true).apply{gravity=Gravity.CENTER;background=rounded(Color.WHITE,9,1,line)}
  val announcers=txt("—\nANNOUNCERS",15f,true).apply{gravity=Gravity.CENTER;background=rounded(Color.WHITE,9,1,line)}
  countStrip.addView(totalUsers,LinearLayout.LayoutParams(0,dp(60),1f).apply{rightMargin=dp(3)})
  countStrip.addView(admins,LinearLayout.LayoutParams(0,dp(60),1f).apply{leftMargin=dp(2);rightMargin=dp(2)})
  countStrip.addView(announcers,LinearLayout.LayoutParams(0,dp(60),1f).apply{leftMargin=dp(3)})
  v.addView(countStrip)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.SYSTEM_ROLE)}){r->
   box.removeAllViews()
   if(!r.ok){box.addView(txt("Unable to load users."));return@network}
   val a=JSONObject(r.body).optJSONArray("users")?:return@network
   var adminCount=0
   var announcerCount=0
   for(i in 0 until a.length()){
    when(a.getJSONObject(i).optString("systemRole","user")){
     "admin","super_admin"->adminCount++
     "announcer"->announcerCount++
    }
   }
   totalUsers.text="${a.length()}\nUSERS"
   admins.text="$adminCount\nADMINS"
   announcers.text="$announcerCount\nANNOUNCERS"
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
 private fun showUserAccess(){
  val v=root()
  header(v)
  v.addView(txt("Users & Staff",20f,true).apply{gravity=Gravity.CENTER})
  v.addView(txt("Manage PitBuzz system access. Team membership does not grant system admin privileges.",11f).apply{setTextColor(muted)})
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  v.addView(box)
  network({api.get(ApiRoutes.SYSTEM_ROLE)}){r->
   box.removeAllViews()
   if(!r.ok){box.addView(card().apply{addView(txt("Unable to load users."))});return@network}
   val root=JSONObject(r.body)
   val a=root.optJSONArray("users")
   val limits=root.optJSONObject("limits")
   if(limits!=null){
    val limitsCard=card()
    limitsCard.addView(txt("SYSTEM ROLE LIMITS",10.5f,true).apply{setTextColor(red)})
    limitsCard.addView(txt("Super Admin ${limits.optInt("super_admin",2)}  •  Admin ${limits.optInt("admin",3)}  •  Announcer ${limits.optInt("announcer",4)}",11f,true))
    box.addView(limitsCard)
   }
   if(a==null || a.length()==0){box.addView(card().apply{addView(txt("No users found."))});return@network}
   var superCount=0;var adminCount=0;var announcerCount=0
   for(i in 0 until a.length()){
    when(a.getJSONObject(i).optString("systemRole","user")){
     "super_admin"->superCount++
     "admin"->adminCount++
     "announcer"->announcerCount++
    }
   }
   val counts=card()
   counts.addView(txt("CURRENT ACCESS",10.5f,true).apply{setTextColor(red)})
   counts.addView(txt("$superCount Super Admin  •  $adminCount Admin  •  $announcerCount Announcer",11f,true))
   box.addView(counts)
   for(i in 0 until a.length()){
    val u=a.getJSONObject(i)
    val row=LinearLayout(this).apply{
     orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL
     setPadding(dp(8),dp(6),dp(8),dp(6))
     background=rounded(Color.WHITE,8,1,Color.rgb(218,225,232))
    }
    val words=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
    val isMe=u.optInt("id",0)==(currentUser?.optInt("id",0)?:0)
    words.addView(txt(u.optString("name","PitBuzz User")+(if(isMe)"  (You)" else ""),13f,true))
    val contact=u.optString("email").ifBlank{u.optString("phone")}
    if(contact.isNotBlank())words.addView(txt(contact,10.5f).apply{setTextColor(muted)})
    words.addView(pill(u.optString("systemRole","user").replace("_"," ").uppercase(),blue))
    row.addView(words,LinearLayout.LayoutParams(0,-2,1f))
    row.addView(btn("Change",false){changeRole(u)},LinearLayout.LayoutParams(dp(92),dp(44)))
    box.addView(row,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(3))})
   }
  }
  v.addView(btn("Back to Dashboard",false){showAdminRoles()})
  footer(v)
 }

 private fun changeRole(u:JSONObject) {
  val currentRole=u.optString("systemRole","user")
  val targetUserId=u.optInt("id",0)
  val myId=currentUser?.optInt("id",0)?:0
  val roles=arrayOf("user","announcer","admin","super_admin")
  android.app.AlertDialog.Builder(this)
   .setTitle("Role for ${u.optString("name")}")
   .setSingleChoiceItems(roles,roles.indexOf(currentRole).coerceAtLeast(0),null)
   .setPositiveButton("Continue"){dialog,_->
    val list=(dialog as android.app.AlertDialog).listView
    val which=list.checkedItemPosition
    if(which<0)return@setPositiveButton
    val chosen=roles[which]
    if(chosen==currentRole){toast("Role unchanged");return@setPositiveButton}
    if(targetUserId==myId && currentRole=="super_admin" && chosen!="super_admin"){
     android.app.AlertDialog.Builder(this)
      .setTitle("Change Your Own Access?")
      .setMessage("You are changing your own Super Admin access. Make sure another Super Admin remains before continuing.")
      .setPositiveButton("Continue"){_,_->submitRoleChange(u,chosen)}
      .setNegativeButton("Cancel",null)
      .show()
    }else submitRoleChange(u,chosen)
   }
   .setNegativeButton("Cancel",null)
   .show()
 }

 private fun submitRoleChange(u:JSONObject,chosen:String){
  android.app.AlertDialog.Builder(this)
   .setTitle("Confirm Role Change")
   .setMessage("Set ${u.optString("name")} to ${chosen.replace("_"," ")}?")
   .setPositiveButton("Change Role"){_,_->
    val key="role-${u.optInt("id")}"
    if(!submitGuard.begin(key))return@setPositiveButton
    val j=JSONObject().put("userId",u.getInt("id")).put("systemRole",chosen)
    network({api.post(ApiRoutes.SYSTEM_ROLE,j.toString())}){r->
     submitGuard.finish(key)
     toast(if(r.ok)"Role updated" else jsonError(r.body,"Role change failed"))
     if(r.ok)showUserAccess()
    }
   }
   .setNegativeButton("Cancel",null)
   .show()
 }
 private fun setBusy(b:Boolean){/* network guard hook; controls remain usable after callbacks */}
 override fun onDestroy(){tts?.stop();tts?.shutdown();io.shutdownNow();super.onDestroy()}
}
