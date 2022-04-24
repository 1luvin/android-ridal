package tv.ridal.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import tv.ridal.util.Theme
import tv.ridal.R
import tv.ridal.ui.view.EditText

// Log

fun msg(msg: String)
{
    Log.d("msg", msg)
}

// String

// Extracts first float number found in string
fun String.amount(): String = substring(indexOfFirst { it.isDigit() }, indexOfLast { it.isDigit() } + 1)
    .filter { it.isDigit() || it == '.' }

// View

fun View.setPaddings(padding: Int)
{
    setPadding(padding, padding, padding, padding)
}

fun View.setBackgroundColor(colorKey: String)
{
    setBackgroundColor( Theme.color(colorKey) )
}

fun View.measure()
{
    measure(0, 0)
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

// EditText

fun EditText.showKeyboard()
{
    post {
        ViewCompat.getWindowInsetsController(this)?.show(WindowInsetsCompat.Type.ime())
    }
}

fun EditText.hideKeyboard()
{
    post {
        ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())
    }
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