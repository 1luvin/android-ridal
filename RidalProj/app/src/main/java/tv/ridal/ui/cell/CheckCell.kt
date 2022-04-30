package tv.ridal.ui.cell

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.util.Utils
import tv.ridal.ui.view.RTextView

class CheckCell(context: Context) : FrameLayout(context)
{
    private var checkView: ImageView
    private var checkDrawable: Drawable
    private var textView: RTextView

    private var stateAnimator: ValueAnimator? = null
    private val ANIM_DURATION: Long = 190L
    private val textX: Int = Utils.dp(20)
    private val textXChecked: Int = Utils.dp(15 + 24 + 10)

    private val cellHeight: Int = Utils.dp(43)


    var checkColor: Int = Theme.color(Theme.color_main)
        set(value) {
            field = value

            checkDrawable = Theme.drawable(R.drawable.done, checkColor)
            checkView.setImageDrawable(checkDrawable)
        }

    var text: String = ""
        set(value) {
            field = value

            textView.text = text
        }
    var textColor: Int = Theme.color(Theme.color_text2)
        set(value) {
            field = value

            if (stateAnimator == null || ! stateAnimator!!.isRunning)
            {
                textView.setTextColor(textColor)
            }
        }
    var textColorChecked: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            if (stateAnimator == null || ! stateAnimator!!.isRunning)
            {
                textView.setTextColor(textColor)
            }
        }


    var isChecked: Boolean = false
        private set

    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        checkDrawable = Theme.drawable(R.drawable.done, Theme.color_main)
        checkView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(checkDrawable)

            scaleX = 0F
            scaleY = 0F
            alpha = 0F
        }
        addView(checkView, Layout.ezFrame(
            24, 24,
            Gravity.START or Gravity.CENTER_VERTICAL,
            15, 0, 0, 0
        ))

        textView = RTextView(context).apply {
            setTextColor(textColor)
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, Layout.frame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            textX, 0, textX, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( cellHeight, MeasureSpec.EXACTLY )
        )
    }


    fun setChecked(checked: Boolean, animated: Boolean = true)
    {
        if (checked == isChecked) return

        if (animated)
        {
            animateToState(checked)
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
                updateLayoutParams<FrameLayout.LayoutParams> {

                    val tx = if (checked) {
                        textXChecked
                    } else {
                        textX
                    }

                    setMargins(tx, 0, textX, 0)
                }
            }
        }

        isChecked = checked
    }

    private fun animateToState(checked: Boolean)
    {
        val from_scale = checkView.scaleX // == scaleY == alpha
        val to_scale = if (checked) {
            1F
        } else {
            0F
        }

        val from_textColor = textView.currentTextColor
        val to_textColor = if (checked) {
            textColorChecked
        } else {
            textColor
        }

        val from_textX = textView.marginLeft
        val to_textX = if (checked) {
            textXChecked
        } else {
            textX
        }

        stateAnimator?.cancel()
        stateAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            duration = ANIM_DURATION

            addUpdateListener {
                val process = it.animatedValue as Float

                val currScale = from_scale + (to_scale - from_scale) * process
                checkView.apply {
                    scaleX = currScale
                    scaleY = currScale
                    alpha = currScale
                }

                val currColor = Theme.mixColors( from_textColor, to_textColor, process )
                textView.setTextColor(currColor)

                val currX = from_textX + (to_textX - from_textX) * process
                textView.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(currX.toInt(), 0, textX, 0)
                }
            }

            start()
        }
    }

}





































//