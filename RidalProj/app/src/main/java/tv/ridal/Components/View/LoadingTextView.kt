package tv.ridal.Components.View

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Handler
import android.view.animation.Animation
import android.widget.TextView
import tv.ridal.Application.Theme

class LoadingTextView(context: Context) : TextView(context)
{
    var color: Int = Theme.color(Theme.color_text)
    var loadColor: Int = Theme.color(Theme.color_main)

    private var animator = ValueAnimator.ofFloat(0.1F, 0.9F).apply {
        duration = 1000
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.RESTART
    }

    fun startLoading()
    {
        if (this.measuredWidth == 0 || this.measuredHeight == 0) return
        // если текст уже анимируется

        animator.apply {
            addUpdateListener {

                val currPos = it.animatedValue as Float

                val shader = LinearGradient(
                    0F, 0F, this@LoadingTextView.measuredWidth + 0F, this@LoadingTextView.measuredHeight + 0F,
                    intArrayOf(color, loadColor, color),
                    floatArrayOf(0F, currPos, 1F),
                    Shader.TileMode.CLAMP
                )

                this@LoadingTextView.paint.shader = shader
                this@LoadingTextView.requestLayout()
            }
        }

        kotlin.run {
            animator.start()
        }

    }
}





































//