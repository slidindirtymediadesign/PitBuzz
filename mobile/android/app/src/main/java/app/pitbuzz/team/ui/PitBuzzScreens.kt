package app.pitbuzz.team.ui

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

object PitBuzzScreens {
    fun render(a: Activity, n: PitBuzzNavigator, s: Screen): View =
        LinearLayout(a).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(TextView(a).apply {
                text = "PitBuzz"
                gravity = Gravity.CENTER
            })
        }
}
