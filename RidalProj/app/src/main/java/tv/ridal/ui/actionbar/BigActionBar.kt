package tv.ridal.ui.actionbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.measure
import tv.ridal.util.Utils

class BigActionBar(context: Context) : FrameLayout(context)
{
    private val barHeight: Int = Utils.dp(90)
    private val horizontalPadding: Int = Utils.dp(20)
    private val menuIndent: Int = Utils.dp(15)

    private var titleView: TextView

    var title: String = ""
        set(value) {
            field = value

            titleView.text = title
        }
    var titleColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            titleView.setTextColor(titleColor)
        }

    var menu: BigActionBar.Menu? = null
        set(value)
        {
            menu?.let {
                removeView(it)
            }

            field = value

            menu?.let {
                addView(it, Layout.ezFrame(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    Gravity.END or Gravity.CENTER_VERTICAL
                ))
            }
        }

    init
    {
        setPadding(horizontalPadding, 0, horizontalPadding, 0)

        titleView = TextView(context).apply {
            setTextColor(titleColor)
            textSize = 36F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(titleView, Layout.frame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( paddingTop + barHeight + paddingBottom, MeasureSpec.EXACTLY )
        )

        menu?.let {
            it.measure()

            titleView.layoutParams = Layout.frame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                0, 0, menuIndent + it.measuredWidth, 0
            )
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int)
    {
        super.setPadding(horizontalPadding, top, horizontalPadding, bottom)
    }

    class Menu(context: Context) : LinearLayout(context)
    {
        init
        {

        }

        fun addItem(drawable: Drawable, onClick: (() -> Unit)? = null)
        {
            val itemView = createItemView().apply {
                setImageDrawable(drawable)

                onClick?.let {
                    setOnClickListener { _ ->
                        it.invoke()
                    }
                }
            }

            addView(itemView, Layout.ezLinear(
                40, 40,
                if (itemsCount() != 0) 13 else 0, 0, 0, 0
            ))
        }

        private fun createItemView() : ImageView
        {
            return ImageView(context).apply {
                isClickable = true
                setOnTouchListener( InstantPressListener(this) )

                background = Theme.rect(
                    Theme.overlayColor(Theme.color_bg, 0.04F),
                    radii = FloatArray(4).apply {
                        fill( Utils.dp(10F) )
                    }
                )

                scaleType = ImageView.ScaleType.CENTER
            }
        }

        private fun itemsCount(): Int = childCount
    }

}





































//