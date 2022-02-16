package tv.ridal

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import tv.ridal.HDRezka.Streams.StreamData
import tv.ridal.Ui.ActionBar.ActionBar
import tv.ridal.Utils.Utils
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
    private var streamData: StreamData? = null


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
    private lateinit var actionBar: ActionBar
    private lateinit var scroll: NestedScrollView
    private lateinit var scrollLayout: LinearLayout
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
        scrollLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(scrollLayout)

        headerView = HeaderView()
        scrollLayout.apply {
            addView(headerView)
        }

        createActionBar()

        rootFrame.apply {
            addView(scroll, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            ))

            addView(actionBar, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))
        }
    }

    private fun createActionBar()
    {
        actionBar = ActionBar(context).apply {
            setPadding(0, Utils.dp(25), 0, 0)

            setBackgrounds(
                arrayOf(
                    Theme.createRect(
                        Theme.Fill( intArrayOf(Theme.alphaColor(Theme.COLOR_BLACK, 0.5F), Theme.COLOR_TRANSPARENT), GradientDrawable.Orientation.TOP_BOTTOM )
                    ),
                    Theme.createRect( Theme.color_bg ),
                )
            )

            actionButtonIcon = Theme.drawable(R.drawable.back, Theme.COLOR_WHITE)
            onActionButtonClick {
                finish()
            }

            title = movie.name
            hideTitle(false)
        }
    }

    private fun createScroll()
    {
        scroll = NestedScrollView(context).apply {
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                val limitHeight = headerView.height - actionBar.height

                if (scrollY > limitHeight) {
                    actionBar.apply {
                        showBackground(1)
                        showTitle()
                    }
                } else {
                    actionBar.apply {
                        showBackground(0)
                        hideTitle()
                    }
                }
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
        private lateinit var nameView: TextView

        private var bottomGradPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

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

            val view = View(context).apply {
                background = Theme.createRect(
                    Theme.Fill(
                        intArrayOf( Theme.alphaColor(Theme.COLOR_BLACK, 0.5F), Theme.COLOR_TRANSPARENT ),
                        GradientDrawable.Orientation.BOTTOM_TOP
                    )
                )
            }
            addView(view, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 150,
                Gravity.BOTTOM
            ))

            nameView = TextView(context).apply {
                setTextColor( Theme.COLOR_WHITE )
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32F)
                typeface = Theme.typeface(Theme.tf_bold)
                setLines(1)
                maxLines = 1
                isSingleLine = true

                ellipsize = TextUtils.TruncateAt.END

                text = movie.name
            }
            addView(nameView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM,
                20, 0, 0, 20
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


        }

    }

}





































//