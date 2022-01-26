package tv.ridal.Components.Cells

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.Components.InstantPressListener
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class CheckCell(context: Context) : FrameLayout(context)
{

    private var checkView: ImageView

    private var textView: TextView

    var text: String = ""
        set(value) {
            field = value

            textView.text = text
        }
    private val textColor: Int = Theme.color(Theme.color_radio)
    private val textColorChecked: Int = Theme.color(Theme.color_text)

    var isChecked = false
        private set

    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        setOnClickListener {
            setChecked( ! isChecked )
        }

        val checkDrawable = Theme.drawable(R.drawable.done, Theme.color_main)
        checkView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            scaleX = 0F
            scaleY = 0F
            alpha = 0F

            setImageDrawable(checkDrawable)
        }
        addView(checkView, LayoutHelper.createFrame(
            24, 24,
            Gravity.START or Gravity.CENTER_VERTICAL,
            20, 0, 0, 0
        ))

        textView = TextView(context).apply {
            setTextColor(textColor)
            textSize = 16F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            translationX = - Utils.dp(24F + 15F)
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            20 + 24 + 15, 0, 15, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            Utils.dp(50)
        )
    }

    fun setChecked(checked: Boolean, animated: Boolean = true)
    {
        if (checked == isChecked) return

        if (animated)
        {
            animateCheckChange()
            animateTextChange()
        }
        else
        {
            checkView.apply {
                scaleX = if (isChecked) 0F else 1F
                scaleY = if (isChecked) 0F else 1F
                alpha = if (isChecked) 0F else 1F
            }
            textView.setTextColor( if (isChecked) textColorChecked else textColor )
        }

        isChecked = checked
    }

    private fun animateTextChange()
    {
        val startColor = textView.currentTextColor
        val endColor = if (isChecked) {
            textColor
        } else {
            textColorChecked
        }

        ValueAnimator.ofInt(startColor, endColor).apply {
            duration = 200L
            setEvaluator(ArgbEvaluator())

            addUpdateListener {
                val animatedColor = it.animatedValue as Int
                textView.setTextColor(animatedColor)
            }

            start()
        }

        val startTranslation = textView.translationX
        val endTranslation = if (isChecked) {
            - Utils.dp(24F + 15F)
        } else {
            0F
        }

        ValueAnimator.ofFloat(startTranslation, endTranslation).apply {
            duration = 200L

            addUpdateListener {
                val animatedTranslation = it.animatedValue as Float
                textView.translationX = animatedTranslation
            }

            start()
        }
    }

    private fun animateCheckChange()
    {
        val startScale = checkView.scaleX
        val endScale = if (isChecked) {
            0F
        } else {
            1F
        }

        ValueAnimator.ofFloat(startScale, endScale).apply {
            duration = 200L

            addUpdateListener {
                val animatedScale = it.animatedValue as Float

                checkView.apply {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    alpha = animatedScale
                }
            }

            start()
        }
    }

}





































//