package tv.ridal

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.core.view.contains
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.hdrezka.HDRezka
import tv.ridal.ui.layout.Layout
import tv.ridal.hdrezka.Movie
import tv.ridal.hdrezka.Parser
import tv.ridal.ui.actionbar.ActionBar
import tv.ridal.adapter.PeopleAdapter
import tv.ridal.ui.actionbar.IosBack
import tv.ridal.ui.cell.PointerCell
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.recyclerview.SpacingItemDecoration
import tv.ridal.ui.popup.ImagePopup
import tv.ridal.ui.recyclerview.EdgeColorEffect
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Utils
import tv.ridal.ui.setBackgroundColor
import tv.ridal.ui.view.DropdownTextLayout
import kotlin.random.Random

class MovieFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "Movie${Random.nextInt()}"


    companion object
    {
        fun newInstance(movie: Movie) = MovieFragment().apply {
            this.movie = movie
        }
    }


    private lateinit var rootFrame: FrameLayout
    private lateinit var actionBar: ActionBar
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: LinearLayout

    private lateinit var headerView: HeaderView
    private var actorsView: RecyclerView? = null
    private var producersView: RecyclerView? = null

    private lateinit var movie: Movie
    private lateinit var movieInfo: Movie.Info

    private val requestQueue: RequestQueue = App.instance().requestQueue
    private val requestTag: String = stableTag


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        enableDarkStatusBar(false)

        loadMovieInfo()

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onDestroy()
    {
        super.onDestroy()

        requestQueue.cancelAll(requestTag)

        enableDarkStatusBar( ! Theme.isDark() )
    }


    private fun createUI()
    {
        headerView = HeaderView()
        layout = VLinearLayout(context).apply {
            addView(headerView)
        }

        createScroll()
        scroll.addView(layout)

        createActionBar()

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )

            addView(scroll)

            addView(actionBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }
    }

    private fun createActionBar()
    {
        actionBar = ActionBar(context).apply {
            setPadding( 0, Utils.dp(30), 0, 0 )

            background = Theme.rect( Theme.color_bg )
            enableOnlyBackButton(enable = true, animated = false)

            iosBack = IosBack(context).apply {
                type = IosBack.Type.ICON
                canChangeType = false

                onBack {
                    this@MovieFragment.finish()
                }
            }

            title = movie.name
        }
    }

    private fun createScroll()
    {
        scroll = NestedScrollView(context).apply {
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                val limitHeight = headerView.movieNameHeightIndicator

                if (limitHeight in oldScrollY until scrollY)
                {
                    enableDarkStatusBar( ! Theme.isDark() )
                    actionBar.apply {
                        enableOnlyBackButton(false)
                    }
                }
                else if (limitHeight in scrollY until oldScrollY)
                {
                    enableDarkStatusBar(false)
                    actionBar.apply {
                        enableOnlyBackButton(true)
                    }
                }
            }
        }
    }

    private fun createPeopleView(people: ArrayList<Movie.NameUrl>) : RecyclerView
    {
        return RecyclerView(context).apply {
            setPadding( Utils.dp(20), 0, Utils.dp(20), 0 )
            clipToPadding = false

            edgeEffectFactory = EdgeColorEffect( Theme.mainColor )

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration( SpacingItemDecoration( Utils.dp(11) ) )

            adapter = PeopleAdapter(people).apply {
                onPersonClick {
                    ImagePopup(context).apply {
                        setImageDrawable(it)
                        show()
                    }
                }
            }
        }
    }


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
                .crossfade(170)
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
            setData(movieInfo.releaseYear, movieInfo.countries, movieInfo.genres)
            setActors(movieInfo.actors)
            setDuration(movieInfo.duration)
        }

        // Рейтинги
        if ( movieInfo.hasRatings() )
        {
            headerView.setRatings(movieInfo.ratings!!)
        }

        // Описание
        if ( movieInfo.hasDescription() )
        {
            val tv = RTextView(context).apply {
                setPadding( Utils.dp(20), Utils.dp(10), Utils.dp(20), 0 )

                setTextColor( Theme.color_text )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16.5F

                text = movieInfo.description!!.text
            }

            val dropdownLayout = DropdownTextLayout(context).apply {
                setPadding(0, Utils.dp(5), 0, Utils.dp(5))

                textView = tv
                expandText = Locale.string(R.string.more)
                collapseLines = 3
            }

            layout.addView(dropdownLayout, Layout.frame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        // Актеры
        if ( movieInfo.hasActors() )
        {
            actorsView = createPeopleView(movieInfo.actors!!)

            val actorsSection = SectionView( Locale.string(R.string.actors) ).apply {
                setView(
                    actorsView!!
                )
            }

            layout.addView(actorsSection)
        }

        // Режиссеры
        if ( movieInfo.hasProducers() )
        {
            producersView = createPeopleView(movieInfo.producers!!)

            val producersSection = SectionView( Locale.string(R.string.producers) ).apply {
                setView(
                    producersView!!
                )
            }

            layout.addView(producersSection)
        }

        // Входит в списки
        if ( movieInfo.hasInLists() )
        {
            val layout = VLinearLayout(context)
            val lists = movieInfo.inLists!!
            for (list in lists)
            {
                val cell = PointerCell(context).apply {
                    text = list.name

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = list.name
                            url = list.url

                            filters = HDRezka.Filters.NO_FILTERS
                        }
                        startFragment(MoviesFragment.newInstance(args))
                    }
                }
                layout.addView(cell)
            }

            val section = SectionView( Locale.string(R.string.inLists) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setView(layout)
            }

            this.layout.addView(section)
        }

        // Входит в коллекции
        if ( movieInfo.hasInCollections() )
        {
            val layout = VLinearLayout(context)
            val collections = movieInfo.inCollections!!
            for (collection in collections)
            {
                val cell = PointerCell(context).apply {
                    text = collection.name

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = collection.name
                            url = collection.url

                            filters = HDRezka.Filters.SORTING
                        }
                        startFragment(MoviesFragment.newInstance(args))
                    }
                }
                layout.addView(cell)
            }

            val section = SectionView( Locale.string(R.string.inCollections) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setView(layout)
            }

            this.layout.addView(section)
        }

        // Страна
        if ( movieInfo.hasCountries() )
        {
            val layout = VLinearLayout(context)
            val countries = movieInfo.countries!!
            for (country in countries)
            {
                val countryCell = PointerCell(context).apply {
                    text = country.name

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = country.name
                            url = country.url

                            filters = HDRezka.Filters.SECTION_SORTING
                        }
                        startFragment(MoviesFragment.newInstance(args))
                    }
                }
                layout.addView(countryCell)
            }

            val countriesSection = SectionView( Locale.string(R.string.country) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setView(layout)
            }

            this.layout.addView(countriesSection)
        }

        // Жанры
        if ( movieInfo.hasGenres() )
        {
            val layout = VLinearLayout(context).apply {
                setPadding(0, 0, 0, Utils.dp(40))
            }
            val genres = movieInfo.genres!!
            for (genre in genres)
            {
                val genreCell = PointerCell(context).apply {
                    text = genre.name

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = HDRezka.getSectionNameByMovieType(movie.type!!)
                            url = genre.url

                            filters = HDRezka.Filters.GENRE_SORTING

                            applyGenre = genre.name
                        }
                        startFragment( MoviesFragment.newInstance(args) )
                    }
                }
                layout.addView(genreCell)
            }

            val genresSection = SectionView( Locale.string(R.string.genre) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setView(layout)
            }

            this.layout.addView(genresSection)
        }
    }


    private fun enableDarkStatusBar(enable: Boolean)
    {
        Theme.enableDarkStatusBar( (context as AppActivity).window, enable )
    }


    inner class HeaderView : FrameLayout(context)
    {
        var posterView: ImageView
        private lateinit var gradientView: View
        private var nameView: TextView
        private var ratingsLayout: LinearLayout? = null
        private var dataText: TextView? = null
        private var actorsText: TextView? = null
        private var durationView: LinearLayout? = null

        private val secondaryTextColor: Int = Theme.alphaColor(Theme.COLOR_WHITE, 0.8F)

        var movieNameHeightIndicator: Int = 0 // !

        fun setPoster(drawable: Drawable)
        {
            posterView.setImageDrawable(drawable)
            invalidate()
        }

        fun setData(date: String?, countries: ArrayList<Movie.NameUrl>?, genres: ArrayList<Movie.NameUrl>?)
        {
            var dataStr = ""

            if (date != null)
            {
                dataStr += date
                if (countries != null) {
                    dataStr += ", "
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

            dataText = RTextView(context).apply {
                setTextColor( secondaryTextColor )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16F
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = dataStr
            }
            addView(dataText, Layout.ezFrame(
                Layout.MATCH_PARENT,  Layout.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        fun setActors(actors: ArrayList<Movie.NameUrl>?)
        {
            if (actors == null) return

            var str = "${Locale.string(R.string.actors)}: "

            for (i in actors.indices)
            {
                str += actors[i].name

                if (i == 2) break

                if (i != actors.lastIndex) {
                    str += ", "
                }
            }

            actorsText = RTextView(context).apply {
                setTextColor( secondaryTextColor )
                typeface = Theme.typeface(Theme.tf_normal)
                textSize = 16F
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = str
            }
            addView(actorsText, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        fun setDuration(duration: String?)
        {
            if (duration == null) return

            durationView = DurationView(duration)
            addView(durationView, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
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
                val ratingView = RatingView(rating)

                ratingsLayout!!.addView(ratingView, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    if (ratingsLayout!!.childCount > 0) 10 else 0, 0, 0, 0
                ))
            }

            addView(ratingsLayout, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        init
        {
            background = Theme.rect(Theme.color_main)

            posterView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            addView(posterView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            ))

            createBottomToGradient()
            addView(gradientView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.BOTTOM
            ))

            nameView = RTextView(context).apply {
                setTextColor( Theme.COLOR_WHITE )
                textSize = 32F
                typeface = Theme.typeface(Theme.tf_bold)
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = movie.name
            }
            addView(nameView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.BOTTOM
            ))
        }

        private fun createBottomToGradient()
        {
            val color = if ( Theme.isDark() ) {
                Theme.color(Theme.color_bg)
            } else {
                Theme.alphaColor(Theme.COLOR_BLACK, 0.5F)
            }

            gradientView = View(context).apply {
                background = Theme.rect(
                    Theme.Fill(
                        intArrayOf( color, Theme.COLOR_TRANSPARENT ),
                        GradientDrawable.Orientation.BOTTOM_TOP
                    )
                )
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            val wms = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
            super.onMeasure(wms, wms)

            val availableWidthForText = measuredWidth - (Utils.dp(20 * 2))

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

                actorsText!!.setPadding(0, 0, 0, padding)
                actorsText!!.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(Utils.dp(20), 0, Utils.dp(20), bottomPadding)
                }

                actorsText!!.measure(
                    MeasureSpec.makeMeasureSpec( availableWidthForText, MeasureSpec.AT_MOST ),
                    0
                )

                bottomPadding += actorsText!!.measuredHeight
            }

            if (dataText != null)
            {
                var padding = Utils.dp(15)
                if (actorsText != null) padding = Utils.dp(5)

                dataText!!.setPadding(0, 0, 0, padding)
                dataText!!.updateLayoutParams<FrameLayout.LayoutParams> {
                    setMargins(Utils.dp(20), 0, Utils.dp(20), bottomPadding)
                }

                dataText!!.measure(
                    MeasureSpec.makeMeasureSpec( availableWidthForText, MeasureSpec.AT_MOST ),
                    0
                )

                bottomPadding += dataText!!.measuredHeight
            }

            bottomPadding += Utils.dp(3)
            nameView.updateLayoutParams<FrameLayout.LayoutParams> {
                setMargins(Utils.dp(20), 0, Utils.dp(20), bottomPadding)
            }

            gradientView.updateLayoutParams<FrameLayout.LayoutParams> {
                height = this@HeaderView.measuredHeight - Utils.dp(30 + 50)
            }

            movieNameHeightIndicator = measuredHeight - bottomPadding - actionBar.measuredHeight
        }


        inner class RatingView(private val rating: Movie.Rating) : LinearLayout(context)
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
                if (rating.whose == HDRezka.IMDB)
                {
                    background = Theme.rect(COLOR_IMDB, radii = FloatArray(4).apply {
                        fill( Utils.dp(19F) )
                    })

                    drawable = Theme.drawable(R.drawable.imdb)
                }
                else if (rating.whose == HDRezka.KP)
                {
                    background = Theme.rect(COLOR_KP, radii = FloatArray(4).apply {
                        fill( Utils.dp(19F) )
                    })

                    drawable = Theme.drawable(R.drawable.kp)
                }

                if (drawable == null) return

                val iv = ImageView(context).apply {
                    setImageDrawable(drawable)
                }
                addView(iv, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT
                ))

                val tv = TextView(context).apply {
                    setTextColor( Theme.COLOR_WHITE )
                    textSize = 17F
                    typeface = Theme.typeface(Theme.tf_bold)

                    gravity = Gravity.CENTER_VERTICAL

                    text = rating.value
                }
                addView(tv, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL,
                    5, 0, 0, 0
                ))
            }

        }

        inner class DurationView(private val duration: String) : LinearLayout(context)
        {
            init
            {
                createUI()
            }

            private fun createUI()
            {
                setPadding(Utils.dp(20), 0, Utils.dp(7), Utils.dp(10))

                val drawable = Theme.drawable(R.drawable.time, Color.WHITE)
                val iv = ImageView(context).apply {
                    setImageDrawable(drawable)
                }
                addView(iv, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT
                ))

                val tv = TextView(context).apply {
                    setTextColor( Theme.COLOR_WHITE )
                    textSize = 17F
                    typeface = Theme.typeface(Theme.tf_bold)

                    gravity = Gravity.CENTER_VERTICAL

                    text = duration
                }
                addView(tv, Layout.ezLinear(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL,
                    5, 0, 0, 0
                ))
            }
        }

    }

    inner class SectionView(sectionName: String) : VLinearLayout(context)
    {
        private var sectionNameView: TextView
        private var container: FrameLayout

        init
        {
            sectionNameView = RTextView(context).apply {
                setPadding(Utils.dp(20), Utils.dp(15), Utils.dp(20), Utils.dp(5))

                textSize = 20F
                typeface = Theme.typeface(Theme.tf_bold)
                setTextColor( Theme.color_text )
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                text = sectionName
            }
            addView(sectionNameView)

            container = FrameLayout(context)
            addView(container, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        fun setView(view: View)
        {
            if ( contains(view) ) removeView(view)
            addView(view)
        }

    }
}





































//