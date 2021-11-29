package tv.ridal.Components.Layout

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import tv.ridal.Utils.Utils

class LayoutHelper
{
    companion object
    {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2

        private fun size(size: Int): Int
        {
            return if (size < 0) size else Utils.dp(size)
        }

        fun createFrame(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(size(width), size(height), gravity).apply {
                setMargins(Utils.dp(leftMargin), Utils.dp(topMargin), Utils.dp(rightMargin), Utils.dp(bottomMargin))
            }
        }

        fun createFrame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(size(width), size(height))
        }

        /*
            Linear
         */

        fun createLinear(width: Int, height: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams(size(width), size(height))
        }

        /*
            Scroll
         */

        fun createScroll(width: Int, height: Int) : ViewGroup.LayoutParams
        {
            return ViewGroup.LayoutParams(size(width), size(height))
        }


    }
}






































//