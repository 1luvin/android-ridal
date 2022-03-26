package tv.ridal.UI

import android.view.View
import androidx.fragment.app.FragmentTransaction
import tv.ridal.R

// View

fun View.setPaddings(padding: Int)
{
    setPadding(padding, padding, padding, padding)
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