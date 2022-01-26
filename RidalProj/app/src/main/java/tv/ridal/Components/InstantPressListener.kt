package tv.ridal.Components

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import tv.ridal.Application.Theme

class InstantPressListener(private val ofView: View) : View.OnTouchListener
{
    private val ALPHA_PRESSED: Float = 0.6F

    private val alphaAnimator: ValueAnimator = ValueAnimator().apply {
        setEvaluator( ArgbEvaluator() )
        setDuration(100)

        addUpdateListener {
            val animatedAlpha = it.animatedValue as Float
            ofView.alpha = animatedAlpha
        }
    }

    private val scaleAnimator: ValueAnimator = ValueAnimator().apply {
        setDuration(100)

        addUpdateListener {
            val animatedScale = it.animatedValue as Float
            ofView.scaleX = animatedScale
            ofView.scaleY = animatedScale
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean
    {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
//                v.background?.setHotspot(event.x, event.y)
//                v.isPressed = true
                alphaAnimator.cancel()
                alphaAnimator.setFloatValues(ofView.alpha, ALPHA_PRESSED)
                alphaAnimator.start()

                scaleAnimator.apply {
                    cancel()
                    setFloatValues(1F, 0.98F)
                    start()
                }
                return true
            }
            MotionEvent.ACTION_UP ->
            {
                alphaAnimator.apply {
                    cancel()
                    setFloatValues(ofView.alpha, 1F)
                    start()
                }

                scaleAnimator.apply {
                    cancel()
                    setFloatValues(ofView.scaleX, 1F)
                    start()
                }
//                pressAnimator.reverse()
                if (isUpInside(event)) {
                    ofView.performClick()
                }
                return true
            }
            MotionEvent.ACTION_CANCEL ->
            {
                alphaAnimator.apply {
                    cancel()
                    setFloatValues(ofView.alpha, 1F)
                    start()
                }

                scaleAnimator.apply {
                    cancel()
                    setFloatValues(ofView.scaleX, 1F)
                    start()
                }
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