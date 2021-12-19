package tv.ridal.Components.View

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.widget.EdgeEffect
import androidx.core.view.ViewCompat
import androidx.core.widget.EdgeEffectCompat
import androidx.core.widget.NestedScrollView
import tv.ridal.Application.Theme
import tv.ridal.ApplicationActivity
import java.lang.reflect.Field

class NestedScrollView(context: Context) : NestedScrollView(context)
{
    constructor() : this(ApplicationActivity.instance())

    init
    {

    }

//    private fun applyEdgeEffect()
//    {
//        val effect = EdgeEffect(context).apply {
//            color = Theme.COLOR_TRANSPARENT
//        }
//
//        NestedScrollView::class.java.getDeclaredField("mEdgeGlowTop").apply {
//            isAccessible = true
//            set(this@NestedScrollView, effect)
//        }
//
//        NestedScrollView::class.java.getDeclaredField("mEdgeGlowBottom").apply {
//            isAccessible = true
//            set(this@NestedScrollView, effect)
//        }
//
//    }
}





































//