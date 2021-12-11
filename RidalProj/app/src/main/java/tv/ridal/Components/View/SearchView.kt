package tv.ridal.Components.View

import android.animation.ObjectAnimator
import android.content.Context
import android.text.*
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    val text: String
        get() = editText.text.toString()

    private var clearButton: ImageButton
    private var clearButtonAnimator = ObjectAnimator().apply {
        duration = 150
    }

    abstract inner class SearchListener
    {
        open fun onClear() {}

        open fun onTextChanged(text: CharSequence) {}

        open fun onSearch(text: CharSequence) {}

        open fun onFocusChange(focus: Boolean) {}
    }

    var searchListener: SearchListener? = null

    init
    {

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

            imeOptions = EditorInfo.IME_ACTION_SEARCH // чтобы вместо иконки "Enter" была иконка поиска

            hint = Locale.text(Locale.hint_search)
            setHintTextColor(Theme.color(Theme.color_text2))

            setOnFocusChangeListener { v, hasFocus ->
                searchListener?.onFocusChange(hasFocus)
            }

            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    editText.clearFocus()
                    searchListener?.onFocusChange(false)

                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                    searchListener?.onSearch(editText.text)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            keyImeChangeListener = object : EditText.KeyImeChange {
                override fun onKeyIme(keyCode: Int, event: KeyEvent) {
                    when (keyCode)
                    {
                        KeyEvent.KEYCODE_BACK ->
                        {
                            editText.clearFocus()
                            searchListener?.onFocusChange(false)
                        }
                    }
                }
            }

            addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {

                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                    if (s.isEmpty()) hideClearButton()
                    else showClearButton()

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
                editText.text.clear()

                hideClearButton()

                // если клавиатура опущена, поднимаем ее
                if ( ! editText.isFocused)
                {
                    editText.requestFocus()

                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }

                searchListener?.onClear()
            }

            // сразу кнопка спрятана
            scaleX = 0F
            scaleY = 0F
        }
        addView(clearButton, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 15, 0
        ))

        clearButtonAnimator.target = clearButton
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(50), MeasureSpec.EXACTLY)
        )
    }

    private fun showClearButton()
    {
        val currScale = clearButton.scaleX

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