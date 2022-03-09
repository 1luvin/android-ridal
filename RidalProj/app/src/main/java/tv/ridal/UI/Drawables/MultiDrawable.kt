package tv.ridal.UI.Drawables

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable

class MultiDrawable(private val drawables: Array<out Drawable>, show: Int = 0) : LayerDrawable(drawables)
{
    private var currentIndex: Int = show
    var crossfadeDuration: Long = 200

    init
    {
        for (i in drawables.indices)
        {
            if (i != currentIndex) drawables[i].alpha = 0
        }
    }

    fun show(index: Int, animated: Boolean = true)
    {
        if (index == currentIndex) return

        val startAlpha: Int = drawables[index].alpha

        if (animated)
        {
            ValueAnimator.ofInt(startAlpha, 255).apply {
                duration = crossfadeDuration

                addUpdateListener {
                    val animatedAlpha = it.animatedValue as Int

                    drawables[index].alpha = animatedAlpha

                    for (i in drawables.indices)
                    {
                        if (i != index) drawables[i].alpha = 255 - animatedAlpha
                    }
                }

                start()
            }
        }
        else
        {
            drawables[index].alpha = 255

            for (i in drawables.indices)
            {
                if (i != index) drawables[i].alpha = 0
            }
        }

        currentIndex = index
    }
}






































//