package tv.ridal.ui.listener

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class InstantPressListener(private val view: View) : View.OnTouchListener
{
    private val ANIM_DURATION: Long = 100
    private val ALPHA_PRESSED: Float = 0.6F
    private val SCALE_PRESSED: Float = 0.99F

    private var wasPressed: Boolean = false

    private val alphaAnimator: ValueAnimator = ValueAnimator().apply {
        duration = ANIM_DURATION
        setEvaluator( ArgbEvaluator() )

        addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
    }
    private val scaleAnimator: ValueAnimator = ValueAnimator().apply {
        duration = ANIM_DURATION

        addUpdateListener {
            val value = it.animatedValue as Float
            view.apply {
                scaleX = value
                scaleY = value
            }
        }
    }


    override fun onTouch(v: View, event: MotionEvent) : Boolean
    {
        when ( event.action )
        {
            MotionEvent.ACTION_DOWN ->
            {
                wasPressed = true

                alphaAnimator.apply {
                    cancel()
                    setFloatValues( view.alpha, ALPHA_PRESSED )
                    start()
                }
                scaleAnimator.apply {
                    cancel()
                    setFloatValues( view.scaleX, SCALE_PRESSED )
                    start()
                }

                return true
            }
            MotionEvent.ACTION_UP ->
            {
                cancelPress()

                if ( isUpInside(event) ) {
                    view.performClick()
                }

                return true
            }
            MotionEvent.ACTION_CANCEL ->
            {
                cancelPress()
                return true
            }
            MotionEvent.ACTION_MOVE ->
            {
                if ( ! isUpInside(event) ) {
                    cancelPress()
                }
                return true
            }
        }
        return false
    }

    private fun cancelPress()
    {
        if ( ! wasPressed ) return
        wasPressed = false

        alphaAnimator.apply {
            cancel()
            setFloatValues( view.alpha, ALPHA_PRESSED, 1F )
            start()
        }
        scaleAnimator.apply {
            cancel()
            setFloatValues( view.scaleX, SCALE_PRESSED, 1F )
            start()
        }
    }

    private fun isUpInside(e: MotionEvent) : Boolean
    {
        val rect = Rect(
            view.left,
            view.top,
            view.right,
            view.bottom
        )

        return rect.contains(
            view.left + e.x.toInt(),
            view.top + e.y.toInt()
        )
    }

}





































//