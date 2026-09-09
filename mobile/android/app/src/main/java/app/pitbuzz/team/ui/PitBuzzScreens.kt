package app.pitbuzz.team.ui
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
object PitBuzzScreens{
 private fun page(c:Context)=LinearLayout(c).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(Color.WHITE)}
 private fun txt(c:Context,s:String)=TextView(c).apply{text=s;textSize=18f;setPadding(18,12,18,12)}
 private fun btn(c:Context,s:String)=Button(c).apply{text=s;minHeight=52}
 private fun header(c:Context)=page(c).apply{gravity=Gravity.CENTER;addView(txt(c,"NATVA"));addView(txt(c,"RACE DAY COMMUNICATIONS"))}
 private fun race(c:Context)=txt(c,"RACE #  —     STAGING  —     GET READY  —").apply{setTextColor(Color.WHITE);setBackgroundColor(0xFF073763.toInt())}
 private fun nav(c:Context)=LinearLayout(c).apply{orientation=LinearLayout.HORIZONTAL;BottomNavSpec.items.forEach{addView(btn(c,it),LinearLayout.LayoutParams(0,56,1f))}}
 fun login(c:Context)=page(c).apply{addView(header(c));addView(txt(c,"Sign In"));addView(EditText(c).apply{hint="Phone number or email"});addView(EditText(c).apply{hint="Password"});addView(btn(c,"Log In"));addView(btn(c,"Forgot Password?"));addView(btn(c,"Create Account"))}
 fun register(c:Context)=page(c).apply{addView(header(c));addView(txt(c,"Create Account"));listOf("Name","Phone Number","Confirm Phone Number","Email","Password","Confirm Password").forEach{addView(EditText(c).apply{hint=it})};addView(btn(c,"Create Account"))}
 fun recovery(c:Context)=page(c).apply{addView(header(c));addView(txt(c,"Account Recovery"));addView(EditText(c).apply{hint="Registered Phone Number"});addView(btn(c,"Send Verification Code"))}
 fun home(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(txt(c,"✓ CONNECTED"));addView(txt(c,"LATEST ANNOUNCEMENT\nNo announcements yet."));addView(btn(c,"Open My Team"));addView(nav(c))}
 fun announcements(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(txt(c,"Announcements"));addView(nav(c))}
 fun messages(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(txt(c,"Private Messages"));addView(nav(c))}
 fun audio(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(CheckBox(c).apply{text="Announcement audio";isChecked=true});addView(CheckBox(c).apply{text="Race alert audio";isChecked=true});addView(txt(c,"Private messages never auto-play."));addView(nav(c))}
 fun settings(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(txt(c,"Settings"));addView(btn(c,"Sign Out"));addView(nav(c))}
 fun team(c:Context)=page(c).apply{addView(header(c));addView(txt(c,"My Team"));addView(btn(c,"Add Rider"));addView(btn(c,"Invite Family / Crew"));addView(btn(c,"Manage Rider Routing"))}
 fun raceControl(c:Context)=page(c).apply{addView(header(c));addView(race(c));addView(txt(c,"Authorized officials only"));addView(btn(c,"ADVANCE"));addView(btn(c,"SET / CORRECT"))}
}