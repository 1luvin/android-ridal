package tv.ridal.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import tv.ridal.App
import tv.ridal.util.Theme
import tv.ridal.hdrezka.Loader
import tv.ridal.hdrezka.Movie
import tv.ridal.hdrezka.Parser
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Utils

class PersonView(context: Context) : FrameLayout(context)
{
    private var requestQueue = App.instance().requestQueue

    fun setPerson(person: Movie.NameUrl)
    {
        loadPhoto(person.url)
        setName(person.name)
    }

    private var imageView: ImageView
    private var textView: TextView

    private var imageWidthPx = 80
    private var imageHeightPx = 126

    var photo: Drawable? = null
        private set
    private var photoDrawable: Drawable? = null

    private fun loadPhoto(personUrl: String)
    {
        photoDrawable?.let {
            imageView.setImageDrawable(it)
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
        this.photo = photo

        val rawBitmap = (photo as BitmapDrawable).bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, Utils.dp(imageWidthPx), Utils.dp(imageHeightPx), false)
        photoDrawable = RoundedBitmapDrawableFactory.create(resources, resizedBitmap).apply {
            cornerRadius = Utils.dp(6F)
        }

        imageView.setImageDrawable(photoDrawable)
    }

    private fun setName(personName: String)
    {
        textView.text = personName
    }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        background = Theme.rect(Theme.color_bg)

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        addView(imageView, Layout.ezFrame(
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
        addView(textView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.BOTTOM
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
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