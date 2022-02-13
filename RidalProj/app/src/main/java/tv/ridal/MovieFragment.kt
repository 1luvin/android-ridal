package tv.ridal

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transition.TransitionTarget
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.Ui.Layout.LayoutHelper
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Parser
import kotlin.random.Random

class MovieFragment : BaseFragment()
{
    override val stableTag: String
        get() = "Movie${Random.nextInt()}"

    companion object {
        fun newInstance(movie: Movie) = MovieFragment().apply {
            this.movie = movie
        }
    }

    private lateinit var movie: Movie
    private lateinit var movieInfo: Movie.Info

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        loadMovieInfo()

        createUi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onResume()
    {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop()
    {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private lateinit var rootFrame: FrameLayout
//    private lateinit var actionBar: ActionBar
    private lateinit var scroll: NestedScrollView
    private lateinit var headerView: HeaderView

    private fun createUi()
    {
        rootFrame = FrameLayout(context).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )

            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        createScroll()
        rootFrame.addView(scroll)

        headerView = HeaderView()
        rootFrame.addView(headerView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
        ))
    }

    private fun createScroll()
    {
        scroll = NestedScrollView(context).apply {
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                println(scrollX)
            }
        }
    }


    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue
    private val requestTag: String = "requestTag"

    private fun loadMovieInfo()
    {
        val request = StringRequest(Request.Method.GET, movie.url,
            { response ->
                movieInfo = Parser.parseMovieInfo(response)
                fillMovieInfo()
            },
            {
                println("ERROR!")
            }
        ).apply {
            tag = requestTag
        }

        requestQueue.add(request)
    }

    private fun fillMovieInfo()
    {
        val posterUrl = movieInfo.hdPosterUrl
        if (posterUrl != null)
        {
            val request = ImageRequest.Builder(context)
                .data(posterUrl)
                .crossfade(200)
                .target(object : TransitionTarget {
                    override val drawable: Drawable?
                        get() = headerView.posterView.drawable
                    override val view: View
                        get() = headerView.posterView
                    override fun onSuccess(result: Drawable) {
                        headerView.setPoster(result)
                        (result as Animatable).start()
                    }

                })
                .build()

            context.imageLoader.enqueue(request)
        }

        //
    }


    inner class HeaderView : FrameLayout(ApplicationActivity.instance())
    {
        var posterView: ImageView

        private var topGradientPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun setPoster(drawable: Drawable)
        {
            posterView.setImageDrawable(drawable)
            invalidate()
        }

        init
        {
            background = Theme.createRect(Theme.color_main)

            posterView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            addView(posterView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            val wms = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)

            super.onMeasure(wms, wms)
        }

        override fun dispatchDraw(canvas: Canvas)
        {
            super.dispatchDraw(canvas)

            topGradientPaint.shader = LinearGradient(
                0F, 0F, 0F, height / 4F,
                Theme.alphaColor(Theme.COLOR_BLACK, 0.3F),
                Theme.COLOR_TRANSPARENT,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(0F, 0F, width + 0F, height / 4F, topGradientPaint)
        }

    }

}





































//