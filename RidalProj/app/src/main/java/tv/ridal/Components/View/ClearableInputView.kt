package tv.ridal.Components.View

import android.animation.ObjectAnimator
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.R

class ClearableInputView(context: Context) : FrameLayout(context)
{

    var editText: EditText

    private var onTextChangeListener: (() -> Unit)? = null
    fun onTextChange(l: () -> Unit)
    {
        onTextChangeListener = l
    }

    private var clearButton: ImageView
    private var clearButtonAnimator = ObjectAnimator().apply {
        duration = 150
    }

    private var onTextClearListener: (() -> Unit)? = null
    fun onTextClear(l: () -> Unit)
    {
        onTextClearListener = l
    }

    init
    {
        editText = EditText(context).apply {
            background = null
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            setTextColor(Theme.color(Theme.color_text))
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                    if (s.isEmpty()) hideClearButton()
                    else showClearButton()

                    onTextChangeListener?.invoke()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        this.addView(editText, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            15, 0, 15 + 24 + 15, 0
        ))

        val clearDrawable = Theme.drawable(R.drawable.close, Theme.color_actionBar_back)
        clearButton = ImageView(context).apply {
            background = Theme.createCircleSelector(Theme.color(Theme.color_actionBar_back))

            setImageDrawable(clearDrawable)

//            сразу кнопка спрятана
            scaleX = 0F
            scaleY = 0F

            setOnClickListener {
                editText.text.clear()

                hideClearButton()

                onTextClearListener?.invoke()
            }
        }
        this.addView(clearButton, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 15, 0
        ))

        clearButtonAnimator.target = clearButton
    }

    private fun showClearButton()
    {
        val currScale = clearButton.scaleX
        if (currScale == 1F) return

        clearButtonAnimator.cancel()
        clearButtonAnimator.apply {
            setFloatValues(currScale, 1F)

            addUpdateListener {
                val animatedScale = it.animatedValue as Float
                (target as View).apply {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
            }

            start()
        }
    }

    private fun hideClearButton()
    {
        val currScale = clearButton.scaleX

        clearButtonAnimator.cancel()
        clearButtonAnimator.apply {
            setFloatValues(currScale, 0F)

            addUpdateListener {
                val animatedScale = it.animatedValue as Float
                (target as View).apply {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
            }

            start()
        }
    }

}





































//