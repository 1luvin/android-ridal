package tv.ridal

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transition.TransitionTarget
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.HDRezka.HDRezka
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Parser
import tv.ridal.HDRezka.Streams.StreamData
import tv.ridal.UI.ActionBar.ActionBar
import tv.ridal.UI.Adapters.PeopleAdapter
import tv.ridal.UI.Cells.PointerCell
import tv.ridal.UI.SpacingItemDecoration
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
    private var actorsView: RecyclerView? = null

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
                    Theme.createRect( Theme.lightenColor(Theme.color_bg) )
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

                val limitHeight = headerView.movieNameHeightIndicator

                if (scrollY > limitHeight && oldScrollY <= limitHeight) {
                    actionBar.apply {
                        showBackground(1)
                        showTitle()
                    }
                } else if (scrollY <= limitHeight && oldScrollY > limitHeight) {
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
        // постер
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

        headerView.apply {
            setData(movieInfo.releaseDate, movieInfo.countries, movieInfo.genres)
            setActors(movieInfo.actors)
            setDuration(movieInfo.duration)
        }

        // рейтинги
        if ( movieInfo.hasRatings() )
        {
            headerView.setRatings(movieInfo.ratings!!)
        }

        // описание
        if ( movieInfo.hasDescription() )
        {
            val tv = TextView(context).apply {
                setPadding(Utils.dp(20), 0, Utils.dp(20), 0)

                setTextColor( Theme.color(Theme.color_text) )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16F

                text = movieInfo.description!!.text!!
            }
            scrollLayout.addView(tv)
        }

        // входит в списки
        if ( movieInfo.hasInLists() )
        {
            scrollLayout.addView( createSectionNameView(Locale.text(Locale.text_inLists)) )

            val lists = movieInfo.inLists!!
            for (list in lists)
            {
                val listCell = PointerCell(context).apply {
                    text = list.name!!

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = list.name!!
                            url = list.url!!

                            hasSorting = false
                        }
                        startFragment( MoviesFragment.newInstance(args) )
                    }
                }
                scrollLayout.addView(listCell)
            }
        }

        // страна
        if ( movieInfo.hasCountries() )
        {
            scrollLayout.addView( createSectionNameView(Locale.text(Locale.text_country)) )

            val countries = movieInfo.countries!!
            for (country in countries)
            {
                val countryCell = PointerCell(context).apply {
                    text = country.name!!

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = country.name!!
                            url = country.url!!
                        }
                        startFragment( MoviesFragment.newInstance(args) )
                    }
                }
                scrollLayout.addView(countryCell)
            }
        }

        // жанры
        if ( movieInfo.hasGenres() )
        {
            scrollLayout.addView( createSectionNameView(Locale.text(Locale.text_genre)) )

            val genres = movieInfo.genres!!
            for (genre in genres)
            {
                val genreCell = PointerCell(context).apply {
                    text = genre.name!!

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = HDRezka.getSectionNameByMovieType(movie.type.ruType)
                            url = genre.url!!

                            applyGenre = genre.name!!
                        }
                        startFragment( MoviesFragment.newInstance(args) )
                    }
                }
                scrollLayout.addView(genreCell)
            }
        }

        // актеры
        if ( movieInfo.hasActors() )
        {
            scrollLayout.addView( createSectionNameView(Locale.text(Locale.text_actors)) )
            createActorsView()
            scrollLayout.addView(actorsView)
        }
    }

    private fun createActorsView()
    {
        actorsView = RecyclerView(context).apply {
            setPadding(Utils.dp(8), 0, Utils.dp(8), 0)
            clipToPadding = false

            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration( SpacingItemDecoration(Utils.dp(12), Utils.dp(5), Utils.dp(10)) )

            adapter = PeopleAdapter( movieInfo.actors!! )
        }
    }


    inner class HeaderView : FrameLayout(ApplicationActivity.instance())
    {
        var posterView: ImageView
        private var gradientView: View
        private var nameView: TextView
        private var ratingsLayout: LinearLayout? = null
        private var dataText: TextView? = null
        private var actorsText: TextView? = null
        private var durationView: LinearLayout? = null

        var movieNameHeightIndicator: Int = 0 // !


        fun setPoster(drawable: Drawable)
        {
            posterView.setImageDrawable(drawable)
            invalidate()
        }

        fun setData(date: Movie.ReleaseDate?, countries: ArrayList<Movie.Country>?, genres: ArrayList<Movie.Genre>?)
        {
            var dataStr = ""

            if (date != null)
            {
                val regex = "[0-9]{4}".toRegex()
                val year = regex.find(date.date)

                if (year != null)
                {
                    dataStr += year.value
                    if (countries != null) {
                        dataStr += ", "
                    }
                }
            }

            if (countries != null)
            {
                dataStr += countries[0].name
                if (countries.size > 1) {
                    dataStr += ", "
                    dataStr += countries[1].name
                }

                if (genres != null) {
                    dataStr += ", "
                }
            }

            if (genres != null)
            {
                dataStr += genres[0].name
                if (genres.size > 1) {
                    dataStr += ", "
                    dataStr += genres[1].name
                }
            }

            dataText = TextView(context).apply {
                setTextColor( Theme.color(Theme.color_text2) )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16F
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = dataStr
            }
            addView(dataText, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT,  LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        fun setActors(actors: ArrayList<Movie.Person>?)
        {
            if (actors == null) return

            var str = "${Locale.text(Locale.text_actors)}: "

            for (i in actors.indices)
            {
                if (i == 3) break

                str += actors[i].name
                if (i != actors.lastIndex) {
                    str += ", "
                }
            }

            actorsText = TextView(context).apply {
                setTextColor( Theme.color(Theme.color_text2) )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16F
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = str
            }
            addView(actorsText, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        fun setDuration(duration: String?)
        {
            if (duration == null) return

            durationView = DurationView(context, duration)
            addView(durationView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        fun setRatings(ratings: ArrayList<Movie.Rating>)
        {
            ratingsLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL

                setPadding(Utils.dp(20), 0, 0, Utils.dp(15))
            }

            for (rating in ratings)
            {
                val ratingView = RatingView(context, rating)

                ratingsLayout!!.addView(ratingView, LayoutHelper.createLinear(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    if (ratingsLayout!!.childCount > 0) 10 else 0, 0, 0, 0
                ))
            }

            addView(ratingsLayout, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
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

            gradientView = View(context).apply {
                background = Theme.createRect(
                    Theme.Fill(
                        intArrayOf( Theme.color(Theme.color_bg), Theme.COLOR_TRANSPARENT ),
                        GradientDrawable.Orientation.BOTTOM_TOP
                    )
                )
            }
            addView(gradientView, FrameLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM
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
                Gravity.START or Gravity.BOTTOM
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            val wms = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
            super.onMeasure(wms, wms)

            var bottomPadding = 0
            if (ratingsLayout != null)
            {
                ratingsLayout!!.measure(0, 0)
                bottomPadding = ratingsLayout!!.measuredHeight
            }

            if (durationView != null)
            {
                durationView!!.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(0, 0, 0, bottomPadding)
                }
                durationView!!.measure(0, 0)
                bottomPadding += durationView!!.measuredHeight
            }

            if (actorsText != null)
            {
                var padding = Utils.dp(15)
                if (durationView != null) padding = Utils.dp(10)

                actorsText!!.setPadding(Utils.dp(20), 0, Utils.dp(15), padding)
                actorsText!!.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(0, 0, 0, bottomPadding)
                }
                actorsText!!.measure(0, 0)
                bottomPadding += actorsText!!.measuredHeight
            }

            if (dataText != null)
            {
                var padding = Utils.dp(15)
                if (actorsText != null) padding = Utils.dp(5)

                dataText!!.setPadding(Utils.dp(20), 0, Utils.dp(15), padding)
                dataText!!.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(0, 0, 0, bottomPadding)
                }

                dataText!!.measure(0, 0)
                bottomPadding += dataText!!.measuredHeight
            }

            bottomPadding += Utils.dp(3)
            nameView.updateLayoutParams<FrameLayout.LayoutParams> {
                setMargins(Utils.dp(20), 0, 0, bottomPadding)
            }

            gradientView.updateLayoutParams<FrameLayout.LayoutParams> {
                height = this@HeaderView.measuredHeight - Utils.dp(25 + 56)
            }

            actionBar.measure(0, 0)
            movieNameHeightIndicator = measuredHeight - bottomPadding - actionBar.measuredHeight
        }

        inner class RatingView(context: Context, private val rating: Movie.Rating) : LinearLayout(context)
        {
            private val COLOR_IMDB: Int = 0xFFF3C31B.toInt()
            private val COLOR_KP: Int = 0xFFFB5504.toInt()

            init
            {
                createUI()
            }

            private fun createUI()
            {
                setPadding(Utils.dp(7), Utils.dp(5), Utils.dp(7), Utils.dp(5))

                var drawable: Drawable? = null
                if (rating.whose!! == HDRezka.IMDB)
                {
                    background = Theme.createRect(COLOR_IMDB, radii = FloatArray(4).apply {
                        fill(Utils.dp(19F))
                    })

                    drawable = Theme.drawable(R.drawable.imdb)
                }
                else if (rating.whose!! == HDRezka.KP)
                {
                    background = Theme.createRect(COLOR_KP, radii = FloatArray(4).apply {
                        fill(Utils.dp(19F))
                    })

                    drawable = Theme.drawable(R.drawable.kp)
                }

                if (drawable == null) return

                val iv = ImageView(context).apply {
                    setImageDrawable(drawable)
                }
                addView(iv, LayoutHelper.createLinear(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                ))

                val tv = TextView(context).apply {
                    setTextColor( Theme.COLOR_WHITE )
                    textSize = 17F
                    typeface = Theme.typeface(Theme.tf_bold)

                    gravity = Gravity.CENTER_VERTICAL

                    text = rating.value!!
                }
                addView(tv, LayoutHelper.createLinear(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL,
                    5, 0, 0, 0
                ))
            }

        }

        inner class DurationView(context: Context, private val duration: String) : LinearLayout(context)
        {
            init
            {
                createUI()
            }

            private fun createUI()
            {
                setPadding(Utils.dp(20), 0, Utils.dp(7), Utils.dp(10))

                val drawable = Theme.drawable(R.drawable.time, Theme.COLOR_WHITE)
                val iv = ImageView(context).apply {
                    setImageDrawable(drawable)
                }
                addView(iv, LayoutHelper.createLinear(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
                ))

                val tv = TextView(context).apply {
                    setTextColor( Theme.COLOR_WHITE )
                    textSize = 17F
                    typeface = Theme.typeface(Theme.tf_bold)

                    gravity = Gravity.CENTER_VERTICAL

                    text = duration
                }
                addView(tv, LayoutHelper.createLinear(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL,
                    5, 0, 0, 0
                ))
            }
        }

    }

    private fun createSectionNameView(text: String) : TextView
    {
        return TextView(context).apply {
            setPadding(Utils.dp(20), Utils.dp(15), Utils.dp(20), Utils.dp(5))

            this.text = text

            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor(Theme.color(Theme.color_main))
        }
    }

}





































//