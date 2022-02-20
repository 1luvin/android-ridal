package tv.ridal.Utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tv.ridal.Application.ApplicationLoader
import kotlin.math.ceil

class Utils
{
    companion object
    {
        var density: Float = 1F //

        var displayHeight: Int = 0 //

        fun checkDisplaySize(context: Context)
        {
            val metrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)

            density = metrics.density

            displayHeight = metrics.heightPixels
        }

        fun dp(value: Int) : Int
        {
            if (value == 0) {
                return 0
            }
            return ceil(density * value).toInt()
        }

        fun dp(value: Float) : Float
        {
            if (value == 0F) {
                return 0F
            }
            return ceil(density * value)
        }

        fun px(value: Int) : Int
        {
            if (value == 0) {
                return 0
            }
            return ceil(value / density).toInt()
        }


        fun enableDarkStatusBar(window: Window, enable: Boolean)
        {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = enable
        }

        fun hideStatusBar(activity: Activity)
        {
            WindowInsetsControllerCompat(activity.window, activity.window.decorView).hide(WindowInsetsCompat.Type.statusBars())
        }
    }
}





































//