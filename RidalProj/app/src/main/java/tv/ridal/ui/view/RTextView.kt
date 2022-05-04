package tv.ridal.ui.view

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import tv.ridal.util.Theme
import tv.ridal.util.Utils

/*
    The same as TextView, but textSize property stored in density pixels
 */

class RTextView(context: Context) : TextView(context)
{
    override fun setTextSize(size: Float) = super.setTextSize( TypedValue.COMPLEX_UNIT_DIP, size )

    override fun getTextSize(): Float = Utils.px( super.getTextSize() )
}