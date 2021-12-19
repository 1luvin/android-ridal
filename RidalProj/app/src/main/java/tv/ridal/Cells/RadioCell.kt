package tv.ridal.Cells

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.ApplicationActivity
import tv.ridal.Components.InstantPressListener
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.View.RadioButton
import tv.ridal.Utils.Utils

class RadioCell : FrameLayout(ApplicationActivity.instance())
{
    private var textView: TextView
    private var radioButton: RadioButton

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
        setOnTouchListener(InstantPressListener())

        background = Theme.createRectSelector(
            Theme.color_bg
        )

        setOnClickListener {
            setChecked( ! isChecked )
        }

        textView = TextView(context).apply {
            setTextColor(textColor)
            textSize = 16F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            20 + 22 + 15, 0, 15, 0
        ))

        radioButton = RadioButton().apply {
            size = Utils.dp(20)
            color = Theme.color(Theme.color_radio)
            checkedColor = Theme.color(Theme.color_main)
        }
        addView(radioButton, LayoutHelper.createFrame(
            22, 22,
            Gravity.START or Gravity.CENTER_VERTICAL,
            20, 0, 0, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            Utils.dp(50)
        )
    }

    fun setChecked(checked: Boolean) {
        if (checked == isChecked) {
            return
        }
        radioButton.setChecked(checked)
        animateCheckChange()

        isChecked = checked
    }

    private fun animateCheckChange()
    {
        val startColor: Int
        val endColor: Int

        if (isChecked) {
            startColor = textColorChecked
            endColor = textColor
        } else {
            startColor = textColor
            endColor = textColorChecked
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
    }
}





































//