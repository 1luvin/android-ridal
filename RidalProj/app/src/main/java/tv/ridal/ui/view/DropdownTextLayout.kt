package tv.ridal.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class DropdownTextLayout(context: Context) : FrameLayout(context)
{
    private val DROP_DURATION: Long = 300

    lateinit var textView: TextView
    var collapseLines: Int = 0 // !
    var expandText: CharSequence = ""
        set(value) {
            field = value.toString().lowercase()
        }

    private var expandTextView: RTextView? = null

    private var startHeight: Int = 0 // !
    private var endHeight: Int = 0 // !


    init
    {

    }


    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()

        textView.let {
            addView(it, Layout.frame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            it.doOnPreDraw { view ->
                if ( it.lineCount <= collapseLines ) return@doOnPreDraw

                createExpandTextView()

                startHeight = it.paddingTop + (it.lineHeight * collapseLines) + it.paddingBottom
                endHeight = (it.paddingTop + (it.lineHeight * it.lineCount) + (it.lineSpacingExtra * (it.lineCount - 1)) + it.paddingBottom).toInt()

                updateLayoutParams {
                    height = startHeight + paddingTop + paddingBottom
                }
            }
        }
    }

    override fun onDetachedFromWindow()
    {
        super.onDetachedFromWindow()

        removeView(textView)
        removeView(expandTextView)
        expandTextView = null
    }


    private fun createExpandTextView()
    {
        expandTextView = RTextView(context).apply {
            isClickable = true
            setPadding( Utils.dp(20), 0, textView.paddingRight, textView.paddingBottom )

            background = Theme.rect( Theme.color_bg )
            setTextColor( Theme.mainColor )

            textSize = textView.textSize
            typeface = textView.typeface

            text = expandText

            setOnClickListener {
                drop()
            }
        }

        addView(expandTextView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.END
        ))
    }

    private fun drop()
    {
        ValueAnimator.ofFloat(0F, 1.05F, 1F).apply {
            duration = DROP_DURATION

            addUpdateListener {
                val process = it.animatedValue as Float

                val animHeight = (startHeight + (endHeight - startHeight) * process).toInt()
                updateLayoutParams {
                    height = animHeight + paddingTop + paddingBottom
                }
                expandTextView?.alpha = 1F - process
            }

            addListener(object : AnimatorListenerAdapter()
            {
                override fun onAnimationEnd(animation: Animator?)
                {
                    super.onAnimationEnd(animation)

                    expandTextView?.visibility = View.GONE
                }
            })

            start()
        }

    }

}


































//