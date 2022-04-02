package tv.ridal.UI

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import tv.ridal.Application.Theme
import tv.ridal.R

// Log

fun msg(msg: String)
{
    Log.d("msg", msg)
}

// View

fun View.setPaddings(padding: Int)
{
    setPadding(padding, padding, padding, padding)
}

fun View.setBackgroundColor(colorKey: String)
{
    setBackgroundColor( Theme.color(colorKey) )
}

// TextView

fun TextView.setTextColor(colorKey: String)
{
    setTextColor( Theme.color(colorKey) )
}

fun TextView.setTypeface(tfKey: String)
{
    typeface = Theme.typeface(tfKey)
}

// FragmentTransaction

fun FragmentTransaction.zoom()
{
    this.setCustomAnimations(
        R.anim.zoom_in,
        R.anim.zoom_out,
        R.anim.zoom_pop_in,
        R.anim.zoom_pop_out
    )
}

fun FragmentTransaction.fade()
{
    this.setCustomAnimations(
        R.anim.fade_in,
        R.anim.fade_out,
        R.anim.fade_out,
        R.anim.fade_in
    )
}