package tv.ridal.ui.layout

import android.widget.FrameLayout
import android.widget.LinearLayout
import tv.ridal.util.Utils

class Layout
{
    companion object
    {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2

        private fun size(size: Int): Int
        {
            return if (size < 0) size else Utils.dp(size)
        }

        /*
            FrameLayout.LayoutParams
         */

        fun ezFrame(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( size(width), size(height), gravity ).apply {
                setMargins( size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin) )
            }
        }
        fun ezFrame(width: Int, height: Int, gravity: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( size(width), size(height), gravity )
        }
        fun ezFrame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( size(width), size(height) )
        }

        fun frame(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( width, height, gravity ).apply {
                setMargins( leftMargin, topMargin, rightMargin, bottomMargin )
            }
        }
        fun frame(width: Int, height: Int, gravity: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( width, height, gravity )
        }
        fun frame(width: Int, height: Int) : FrameLayout.LayoutParams
        {
            return FrameLayout.LayoutParams( width, height )
        }


        /*
            LinearLayout.LayoutParams
         */

        fun ezLinear(width: Int, height: Int, weight: Float, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height), weight ).apply {
                this.gravity = gravity
                setMargins( size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin) )
            }
        }
        fun ezLinear(width: Int, height: Int, weight: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height), weight ).apply {
                setMargins( size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin) )
            }
        }
        fun ezLinear(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height) ).apply {
                this.gravity = gravity
                setMargins( size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin) )
            }
        }
        fun ezLinear(width: Int, height: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height) ).apply {
                setMargins( size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin) )
            }
        }
        fun ezLinear(width: Int, height: Int, weight: Float, gravity: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height), weight ).apply {
                this.gravity = gravity
            }
        }
        fun ezLinear(width: Int, height: Int, weight: Float) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height), weight )
        }
        fun ezLinear(width: Int, height: Int, gravity: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height) ).apply {
                this.gravity = gravity
            }
        }
        fun ezLinear(width: Int, height: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( size(width), size(height) )
        }

        fun linear(width: Int, height: Int, weight: Float, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height, weight ).apply {
                this.gravity = gravity
                setMargins( leftMargin, topMargin, rightMargin, bottomMargin )
            }
        }
        fun linear(width: Int, height: Int, weight: Float, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height, weight ).apply {
                setMargins( leftMargin, topMargin, rightMargin, bottomMargin )
            }
        }
        fun linear(width: Int, height: Int, gravity: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height ).apply {
                this.gravity = gravity
                setMargins( leftMargin, topMargin, rightMargin, bottomMargin )
            }
        }
        fun linear(width: Int, height: Int, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height ).apply {
                setMargins( leftMargin, topMargin, rightMargin, bottomMargin )
            }
        }
        fun linear(width: Int, height: Int, weight: Float, gravity: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height, weight ).apply {
                this.gravity = gravity
            }
        }
        fun linear(width: Int, height: Int, weight: Float) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height, weight )
        }
        fun linear(width: Int, height: Int, gravity: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height ).apply {
                this.gravity = gravity
            }
        }
        fun linear(width: Int, height: Int) : LinearLayout.LayoutParams
        {
            return LinearLayout.LayoutParams( width, height )
        }

    }
}






































//