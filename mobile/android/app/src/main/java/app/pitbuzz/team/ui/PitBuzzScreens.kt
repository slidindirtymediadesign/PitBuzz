package app.pitbuzz.team.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import app.pitbuzz.team.R

object PitBuzzScreens {
    private const val NAVY = 0xFF071A2B.toInt()
    private const val BLUE = 0xFF0878D1.toInt()
    private const val RED = 0xFFFF1428.toInt()
    private const val LIGHT = 0xFFF4F6F8.toInt()
    private const val GREEN_BG = 0xFFE8F8ED.toInt()
    private const val GREEN = 0xFF119447.toInt()

    private fun dp(c:Context,n:Int)=(n*c.resources.displayMetrics.density).toInt()
    private fun page(c:Context)=LinearLayout(c).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(Color.WHITE) }
    private fun text(c:Context,s:String,size:Float=16f,bold:Boolean=false,color:Int=NAVY)=TextView(c).apply {
        text=s; textSize=size; setTextColor(color); setPadding(dp(c,14),dp(c,8),dp(c,14),dp(c,8)); if(bold)setTypeface(typeface,Typeface.BOLD)
    }
    private fun card(c:Context)=LinearLayout(c).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(c,12),dp(c,10),dp(c,12),dp(c,10)); setBackgroundColor(LIGHT) }
    private fun header(c:Context)=LinearLayout(c).apply {
        orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(0,dp(c,8),0,dp(c,6)); setBackgroundColor(Color.WHITE)
        addView(ImageView(c).apply { setImageResource(R.drawable.natva_logo); adjustViewBounds=true; scaleType=ImageView.ScaleType.CENTER_INSIDE }, LinearLayout.LayoutParams(-1,dp(c,66)))
        addView(text(c,"OFFICIAL RACE COMMUNICATION",11f,true,NAVY).apply{gravity=Gravity.CENTER})
    }
    private fun race(c:Context)=LinearLayout(c).apply {
        orientation=LinearLayout.HORIZONTAL; setBackgroundColor(NAVY)
        listOf("RACE #\n—","STAGING\n—","GET READY\n—").forEach { label -> addView(text(c,label,13f,true,Color.WHITE).apply{gravity=Gravity.CENTER},LinearLayout.LayoutParams(0,dp(c,58),1f)) }
    }
    private fun primary(c:Context,s:String)=Button(c).apply { text=s; minHeight=dp(c,52); setTextColor(Color.WHITE); setBackgroundColor(RED); setTypeface(typeface,Typeface.BOLD) }
    private fun secondary(c:Context,s:String)=Button(c).apply { text=s; minHeight=dp(c,48); setTextColor(NAVY) }
    private fun field(c:Context,h:String,password:Boolean=false)=EditText(c).apply { hint=h; textSize=17f; setPadding(dp(c,14),dp(c,12),dp(c,14),dp(c,12)); inputType=if(password) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT }
    private fun nav(c:Context)=LinearLayout(c).apply {
        orientation=LinearLayout.HORIZONTAL; setBackgroundColor(Color.WHITE)
        listOf("⌂\nHome","▤\nAnnouncements","▣\nMessages","♪\nAudio","⚙\nSettings").forEach { addView(text(c,it,10f,true,NAVY).apply{gravity=Gravity.CENTER},LinearLayout.LayoutParams(0,dp(c,58),1f)) }
    }
    private fun scroll(c:Context,body:View)=ScrollView(c).apply{addView(body)}

    fun login(c:Context)=page(c).apply {
        addView(header(c)); val body=card(c).apply {
            addView(text(c,"Welcome to PitBuzz",25f,true)); addView(text(c,"Race-day communication for riders, families and crews.",14f,false,Color.DKGRAY))
            addView(field(c,"Phone number or email")); addView(field(c,"Password",true)); addView(primary(c,"LOG IN")); addView(secondary(c,"Forgot Password?")); addView(secondary(c,"Create Account"))
        }; addView(body,LinearLayout.LayoutParams(-1,-2).apply{setMargins(dp(c,14),dp(c,18),dp(c,14),0)})
    }
    fun register(c:Context)=page(c).apply { addView(header(c)); addView(scroll(c,card(c).apply { addView(text(c,"Create Account",24f,true)); listOf("Name","Phone Number","Confirm Phone Number","Email").forEach{addView(field(c,it))}; addView(field(c,"Password",true));addView(field(c,"Confirm Password",true));addView(primary(c,"CREATE ACCOUNT")) })) }
    fun recovery(c:Context)=page(c).apply { addView(header(c));addView(card(c).apply{addView(text(c,"Account Recovery",24f,true));addView(text(c,"Enter the phone number registered to your PitBuzz account.",14f));addView(field(c,"Registered Phone Number"));addView(primary(c,"SEND VERIFICATION CODE"))}) }
    fun home(c:Context)=page(c).apply {
        addView(header(c));addView(race(c));
        addView(LinearLayout(c).apply{orientation=LinearLayout.HORIZONTAL;setBackgroundColor(GREEN_BG);addView(text(c,"●  CONNECTED\nYou will receive announcements",14f,true,GREEN),LinearLayout.LayoutParams(0,-2,1f));addView(text(c,"▮▮▮",20f,true,GREEN))})
        addView(text(c,"LATEST ANNOUNCEMENT",17f,true,Color.WHITE).apply{setBackgroundColor(RED)})
        addView(card(c).apply{addView(text(c,"No announcements yet",21f,true));addView(text(c,"Official race-day announcements will appear here.",14f,false,Color.DKGRAY));addView(primary(c,"PLAY AUDIO"))})
        addView(text(c,"MY TEAM",17f,true));addView(card(c).apply{addView(text(c,"Riders, family & crew",18f,true));addView(secondary(c,"Open My Team  ›"))})
        addView(Space(c),LinearLayout.LayoutParams(1,0,1f));addView(nav(c))
    }
    fun announcements(c:Context)=listPage(c,"Announcements","Official and class announcements appear here.")
    fun messages(c:Context)=listPage(c,"Messages","Private messages appear here. They never auto-play.")
    private fun listPage(c:Context,title:String,empty:String)=page(c).apply{addView(header(c));addView(race(c));addView(text(c,title,22f,true));addView(card(c).apply{addView(text(c,empty,15f))});addView(Space(c),LinearLayout.LayoutParams(1,0,1f));addView(nav(c))}
    fun audio(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(text(c,"Audio",22f,true));addView(card(c).apply{addView(CheckBox(c).apply{text="Announcement audio";isChecked=true});addView(CheckBox(c).apply{text="Race alert audio";isChecked=true});addView(text(c,"Private messages never auto-play.",14f))});addView(Space(c),LinearLayout.LayoutParams(1,0,1f));addView(nav(c))}
    fun settings(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(text(c,"Settings",22f,true));addView(card(c).apply{addView(secondary(c,"Notification Settings"));addView(secondary(c,"Account"));addView(secondary(c,"Sign Out"))});addView(Space(c),LinearLayout.LayoutParams(1,0,1f));addView(nav(c))}
    fun team(c:Context)=page(c).apply{addView(header(c));addView(text(c,"My Team",22f,true));addView(card(c).apply{addView(primary(c,"+ ADD RIDER"));addView(secondary(c,"Manage Family / Crew"));addView(secondary(c,"Class Subscriptions"));addView(secondary(c,"Notification Settings"))})}
    fun raceControl(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(text(c,"Race Control",22f,true));addView(text(c,"Authorized officials only",14f));addView(primary(c,"ADVANCE"));addView(secondary(c,"SET / CORRECT"))}
}
