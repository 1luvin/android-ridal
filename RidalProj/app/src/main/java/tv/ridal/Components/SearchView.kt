package tv.ridal.Components

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.*
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class SearchView(context: Context) : FrameLayout(context)
{
    var maxLength = Int.MAX_VALUE
        set(value) {
            field = value

            val inputFilter = object : InputFilter.LengthFilter(maxLength) {
                override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int)
                        : CharSequence?
                {

                    if (dest.length == maxLength && source.length > dest.length) {
                        return super.filter(dest, start, end, dest, dstart, dend)
                    }

                    return super.filter(source, start, end, dest, dstart, dend)
                }
            }

            editText.filters = arrayOf(inputFilter)
        }

    private lateinit var editText: EditText
    private lateinit var clearButton: ImageButton

    abstract inner class SearchListener
    {
        open fun onClear()
        {
            editText.text.clear()
        }

        open fun onTextChanged(text: CharSequence) {}
    }

    var searchListener: SearchListener? = null

    init
    {
        layoutParams = LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, 56,
            Gravity.CENTER,
            25, 0, 25, 5
        )

        background = Theme.createOutlinedRect(Theme.alphaColor(Theme.color_main, 0.1F), FloatArray(4).apply {
            fill(10F)
        })

        editText = EditText(context).apply {
            background = null

            setTextColor(Theme.color(Theme.color_text))
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true

            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // чтобы текст не подчеркивался при наборе

            hint = Locale.text(Locale.hint_search)
            setHintTextColor(Theme.color(Theme.color_text2))

            addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {

                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                    searchListener?.onTextChanged(s)
                }
                override fun afterTextChanged(s: Editable)
                {

                }
            })
        }
        addView(editText, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            15, 0, 15 + 24 + 15, 0
        ))

        val clearDrawable = Theme.drawable(R.drawable.close).apply {
            setTint(Theme.color(Theme.color_text2))
        }
        clearButton = ImageButton(context).apply {
            setImageDrawable(clearDrawable)

            background = Theme.createCircleSelector(Theme.color(Theme.color_text2))

            setOnClickListener {
                searchListener?.onClear()
            }
        }
        addView(clearButton, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 15, 0
        ))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(56), MeasureSpec.EXACTLY)
        )
    }

}





































//