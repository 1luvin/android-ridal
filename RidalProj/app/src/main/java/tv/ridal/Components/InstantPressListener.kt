package tv.ridal.Components

import android.view.MotionEvent
import android.view.View

class InstantPressListener : View.OnTouchListener
{
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN -> {
                v.background?.setHotspot(event.x, event.y)
                v.isPressed = true
            }
        }
        return false
    }
}