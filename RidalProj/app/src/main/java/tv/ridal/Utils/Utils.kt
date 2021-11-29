package tv.ridal.Utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import tv.ridal.Application.ApplicationLoader
import kotlin.math.ceil

class Utils
{
    companion object
    {
        private var density: Float = 1F //

        fun checkDisplaySize(context: Context)
        {
            val metrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(metrics)

            density = metrics.density
        }

        fun dp(value: Int) : Int {
            if (value == 0) {
                return 0
            }
            return ceil(density * value).toInt()
        }

        fun dp(value: Float) : Float{
            if (value == 0F) {
                return 0F
            }
            return ceil(density * value)
        }
    }
}