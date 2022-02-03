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
                setMargins(size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin))
            }
        }
        fun createFrame(width: Int, height: Int, gravity: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(size(width), size(height), gravity)
        }
        fun createFrame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams(size(width), size(height))
        }

        /*
            Linear
         */

        fun createLinear(width: Int, height: Int): LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams(size(width), size(height))
        }
        fun createLinear(width: Int, height: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams(size(width), size(height)).apply {
                setMargins(size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin))
            }
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