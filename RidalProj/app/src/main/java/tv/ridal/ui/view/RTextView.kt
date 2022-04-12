package tv.ridal.ui.view

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import tv.ridal.utils.Theme

class RTextView(context: Context) : TextView(context)
{
    override fun setTextSize(size: Float) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    fun setTextColor(colorKey: String)
    {
        setTextColor( Theme.color(colorKey) )
    }
}