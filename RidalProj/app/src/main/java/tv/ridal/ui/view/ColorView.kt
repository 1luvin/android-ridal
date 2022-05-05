package tv.ridal.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import tv.ridal.util.Theme
import tv.ridal.R
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Utils

class ColorView(context: Context) : FrameLayout(context)
{
    private val ANIM_DURATION: Long = 190

    private var imageView: ImageView

    var color: Int = 0xFF666666.toInt()
        set(value) {
            field = value

            background = Theme.rect(
                color,
                radii = FloatArray(4).apply {
                    fill( Utils.dp(15F) )
                }
            )
        }


    init
    {
        background = Theme.rect(
            color,
            radii = FloatArray(4).apply {
                fill( Utils.dp(15F) )
            }
        )

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER

            setImageDrawable( Theme.drawable( R.drawable.done_bold, Color.WHITE ) )

            alpha = 0F
            scaleX = 0F
            scaleY = 0F
        }
        addView(imageView, Layout.ezFrame(
            34, 34,
            Gravity.CENTER
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val size = Utils.dp(80)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
        )
    }


    override fun setSelected(selected: Boolean)
    {
        if (selected == isSelected) return

        super.setSelected(selected)

        animateTo(selected)
    }

    private fun animateTo(select: Boolean)
    {
        val from = imageView.alpha
        val to = if (select) 1F else 0F

        ValueAnimator.ofFloat( from, to ).apply {
            duration = ANIM_DURATION

            addUpdateListener {
                val value = it.animatedValue as Float

                imageView.apply {
                    alpha = value
                    scaleX = value
                    scaleY = value
                }
            }

            start()
        }
    }

}





































//