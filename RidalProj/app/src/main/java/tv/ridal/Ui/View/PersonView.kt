package tv.ridal.Ui.View

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.HDRezka.Loader
import tv.ridal.HDRezka.Parser
import tv.ridal.Ui.InstantPressListener
import tv.ridal.Ui.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class PersonView(context: Context) : FrameLayout(context)
{
    private var imageView: ImageView
    private var textView: TextView

    private var imageWidthPx = 80
    private var imageHeightPx = 126

    private var photoDrawable: Drawable? = null

    private var requestQueue = ApplicationLoader.instance().requestQueue

    fun loadPersonPhoto(personUrl: String)
    {
        if (photoDrawable != null) {
            imageView.setImageDrawable(photoDrawable)
            return
        }

        val request = StringRequest(Request.Method.GET, personUrl,
            { response ->
                val photoUrl = Parser.parsePersonPhotoUrl(response)
                Loader.loadImage(photoUrl, ::onPhotoLoaded)
            },
            {
                println("ERROR!")
            })
        requestQueue.add(request)
    }

    private fun onPhotoLoaded(photo: Drawable?)
    {
        if (photo == null) return

        val rawBitmap = (photo as BitmapDrawable).bitmap

        val resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, Utils.dp(imageWidthPx), Utils.dp(imageHeightPx), false)

        photoDrawable = BitmapDrawable(resources, resizedBitmap)
        imageView.setImageDrawable(photoDrawable)
    }

    var personName: String = ""
        set(value) {
            field = value

            textView.text = personName
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        background = Theme.createRect(Theme.color_bg)

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        addView(imageView, LayoutHelper.createFrame(
            imageWidthPx, imageHeightPx,
            Gravity.START or Gravity.TOP
        ))

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 13F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(2)
            maxLines = 2
//            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.BOTTOM
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val mWidth = Utils.dp(imageWidthPx)

        textView.measure(0, 0)
        val mHeight = Utils.dp(imageHeightPx + 5) + textView.measuredHeight

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY),
        )
    }
}





































//