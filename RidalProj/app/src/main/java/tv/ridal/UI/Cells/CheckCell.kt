package tv.ridal.UI.Cells

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
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
    var textColor: Int = Theme.color(Theme.color_text2)
        set(value) {
            field = value

            if (textAnimator == null || ! textAnimator!!.isRunning)
            {
                textView.setTextColor(textColor)
            }
        }
    var textColorChecked: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            if (textAnimator == null || ! textAnimator!!.isRunning)
            {
                textView.setTextColor(textColor)
            }
        }

    var isChecked = false
        private set

    private var textAnimator: AnimatorSet? = null
    private var checkAnimator: ValueAnimator? = null
    private val ANIM_DURATION: Long = 190L

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
            15, 0, 0, 0
        ))

        textView = TextView(context).apply {
            setTextColor(textColor)
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            translationX = - Utils.dp(19F + 10F)
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            20 + 19 + 10, 0, 15, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            Utils.dp(46)
        )
    }

    override fun invalidate()
    {
        super.invalidate()

        textColor = Theme.color(Theme.color_radio)
        textColorChecked = Theme.color(Theme.color_text)
    }

    fun setChecked(checked: Boolean, animated: Boolean = true)
    {
        if (checked == isChecked) return

        if (animated)
        {
            animateTextChange()
            animateCheckChange()
        }
        else
        {
            val v = if (checked) 1F else 0F
            checkView.apply {
                scaleX = v
                scaleY = v
                alpha = v
            }
            textView.apply {
                setTextColor( if (checked) textColorChecked else textColor )
                translationX = if (checked) 0F else - Utils.dp(19F + 10F)
            }
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

        val startTranslation = textView.translationX
        val endTranslation = if (isChecked) {
            - Utils.dp(19F + 10F)
        } else {
            0F
        }

        textAnimator?.cancel()
        textAnimator = AnimatorSet().apply {
            duration = ANIM_DURATION

            playTogether(
                ValueAnimator.ofInt(startColor, endColor).apply {
                    setEvaluator( ArgbEvaluator() )

                    addUpdateListener {
                        val animatedColor = it.animatedValue as Int
                        textView.setTextColor(animatedColor)
                    }
                },
                ValueAnimator.ofFloat(startTranslation, endTranslation).apply {
                    addUpdateListener {
                        val animatedTranslation = it.animatedValue as Float
                        textView.translationX = animatedTranslation
                    }
                }
            )

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

        checkAnimator?.cancel()
        checkAnimator = ValueAnimator.ofFloat(startScale, endScale).apply {
            duration = ANIM_DURATION

            addUpdateListener {
                val animatedScaleAlpha = it.animatedValue as Float

                checkView.apply {
                    scaleX = animatedScaleAlpha
                    scaleY = animatedScaleAlpha
                    alpha = animatedScaleAlpha
                }
            }

            start()
        }
    }

}





































//