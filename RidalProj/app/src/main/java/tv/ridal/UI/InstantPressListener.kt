package tv.ridal.UI

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class InstantPressListener(private val ofView: View) : View.OnTouchListener
{
    private val ALPHA_PRESSED: Float = 0.6F

    private val alphaAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 100
        setEvaluator( ArgbEvaluator() )

        addUpdateListener {
            val animatedAlpha = it.animatedValue as Float
            ofView.alpha = animatedAlpha
        }
    }

    private val scaleAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 100

        addUpdateListener {
            val animatedScale = it.animatedValue as Float
            ofView.scaleX = animatedScale
            ofView.scaleY = animatedScale
        }
    }

    private fun cancelPress()
    {
        if ( ! wasPressed ) return
        wasPressed = false

        alphaAnimator.apply {
            cancel()
            setFloatValues(ofView.alpha, ALPHA_PRESSED, 1F)
            start()
        }

        scaleAnimator.apply {
            cancel()
            setFloatValues(ofView.scaleX, 0.98F, 1F)
            start()
        }
    }

    private var wasPressed: Boolean = false

    override fun onTouch(v: View, event: MotionEvent): Boolean
    {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                wasPressed = true

                alphaAnimator.apply {
                    cancel()
                    setFloatValues(ofView.alpha, ALPHA_PRESSED)
                    start()
                }

                scaleAnimator.apply {
                    cancel()
                    setFloatValues(1F, 0.98F)
                    start()
                }
                return true
            }
            MotionEvent.ACTION_UP ->
            {
                cancelPress()

                if (isUpInside(event)) {
                    ofView.performClick()
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
                if ( ! isUpInside(event)) {
                    cancelPress()
                }
                return true
            }
        }
        return false
    }

    private fun isUpInside(e: MotionEvent) : Boolean
    {
        val rect = Rect(
            ofView.left,
            ofView.top,
            ofView.right,
            ofView.bottom
        )

        return rect.contains(
            ofView.left + e.x.toInt(),
            ofView.top + e.y.toInt()
        )
    }
}





































//