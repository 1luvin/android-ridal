package tv.ridal.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import tv.ridal.util.Theme
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

// Drawable

fun Drawable.asBitmap() : Bitmap
{
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    draw(canvas)

    return bitmap
}

// FragmentTransaction

fun FragmentTransaction.zoom()
{
    setCustomAnimations(
        R.anim.zoom_in,
        R.anim.zoom_out,
        R.anim.zoom_pop_in,
        R.anim.zoom_pop_out
    )
}

fun FragmentTransaction.fade()
{
    setCustomAnimations(
        R.anim.fade_in,
        R.anim.fade_out,
        R.anim.fade_out,
        R.anim.fade_in
    )
}

































//