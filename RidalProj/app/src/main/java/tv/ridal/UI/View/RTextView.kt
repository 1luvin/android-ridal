package tv.ridal.UI.View

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import tv.ridal.Application.Theme

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