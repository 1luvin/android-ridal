package tv.ridal.Ui

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import tv.ridal.Application.Theme

// View

fun View.getAllViews(): List<View>
{
    if (this !is ViewGroup || childCount == 0) return listOf(this)

    return children
        .toList()
        .flatMap { it.getAllViews() }
        .plus(this as View)
}

// TextView

val TextView.colorKey: String
    get() = Theme.color_text