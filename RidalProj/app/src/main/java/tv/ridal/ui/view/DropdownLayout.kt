package tv.ridal.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.contains
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.msg
import tv.ridal.utils.Theme
import tv.ridal.utils.Utils

class DropdownLayout(context: Context) : FrameLayout(context)
{
    private var textHeight: Int = 0 // !
    var textView: TextView? = null
        set(value)
        {
            if (value == null) return
            if (contains(value)) removeView(value)
            field = value

            addView(textView)

            expandTextView.apply {
                setPadding( Utils.dp(30), textView!!.paddingBottom, textView!!.paddingRight, textView!!.paddingBottom )

                background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf( Theme.COLOR_TRANSPARENT, Theme.color(Theme.color_bg), Theme.color(Theme.color_bg) )
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setColors(
                            intArrayOf( Theme.COLOR_TRANSPARENT, Theme.color(Theme.color_bg), Theme.color(Theme.color_bg) ),
                            floatArrayOf( 0F, 0.2F, 1F )
                        )
                    }
                    setGradientCenter( 0.001F, 0.5F )
                    mutate()
                }

                textSize = 16.5F
                typeface = textView!!.typeface

                bringToFront()
            }
        }

    private var expandTextView: RTextView

    private var startHeight: Int = 0 // !
    var collapseLines: Int = 0 // !
        set(value)
        {
            if (textView == null) return
            field = value

            textView?.let {
                startHeight = it.paddingTop + (it.lineHeight * collapseLines) + it.paddingBottom
                msg("$startHeight")
            }
        }

    init
    {
        expandTextView = RTextView(context).apply {
            setTextColor( Theme.mainColor )
            text = "развернуть"

            setOnClickListener {
                drop()
            }
        }
        addView(expandTextView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.END
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        updateLayoutParams {
            height = startHeight
        }

        textView!!.let {
            textHeight = (it.paddingTop + (it.lineHeight * it.lineCount) + (it.lineSpacingExtra * (it.lineCount - 1)) + it.paddingBottom).toInt()
        }
    }

    private fun drop()
    {
        if (textView == null) return

        val from_height = startHeight
        val to_height = textHeight

        ValueAnimator.ofFloat(0F, 1.05F, 1F).apply {
            duration = 400

            addUpdateListener {
                val process = it.animatedValue as Float

                val animHeight = (from_height + (to_height - from_height) * process).toInt()
                updateLayoutParams {
                    height = animHeight
                }
                expandTextView.alpha = 1F - process
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?)
                {
                    super.onAnimationEnd(animation)

                    expandTextView.visibility = View.GONE
                }
            })

            start()
        }

    }
}


































//