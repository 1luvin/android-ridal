package tv.ridal.UI.Cells

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Application.Utils

class EmptyFolderCell(context: Context) : FrameLayout(context)
{
    private var folderNameView: TextView
    var folderName: String = ""
        set(value) {
            field = value

            folderNameView.text = folderName
        }

    private var folderSubtextView: TextView

    private var pointerImage: ImageView


    private var deleteListener: (() -> Unit)? = null
    fun onDelete(l: (() -> Unit))
    {
        deleteListener = l
    }

    init
    {

        folderNameView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 21F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(folderNameView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            25, 7, 59, 0)
        )

        folderSubtextView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            textSize = 14F
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            text = Locale.text(Locale.text_empty)
        }
        addView(folderSubtextView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            25, 29, 59, 0))

        val pointerDrawable = Theme.drawable(R.drawable.remove, Theme.color_negative)
        pointerImage = ImageView(context).apply {
            setImageDrawable(pointerDrawable)

            background = Theme.rect(Theme.color_bigActionBar_item_bg, radii = FloatArray(4).apply {
                fill(Utils.dp(10F))
            })

            scaleType = ImageView.ScaleType.CENTER

            setOnClickListener {
                deleteListener?.invoke()
            }
        }
        addView(pointerImage, LayoutHelper.createFrame(
            40, 40,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 20, 0))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(56), MeasureSpec.EXACTLY)
        )
    }
}