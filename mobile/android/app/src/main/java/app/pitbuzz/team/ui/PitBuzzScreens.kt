package app.pitbuzz.team.ui

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Legacy navigator compatibility shim.
 *
 * The live beta UI and API flow are owned by MainActivity.
 * This object remains only so older navigator references compile cleanly.
 */
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
