package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.HDRezka.HDRezka
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Pager
import tv.ridal.HDRezka.Parser
import tv.ridal.HDRezka.Streams.FilmStreamData
import tv.ridal.HDRezka.Streams.SeriesStreamData
import tv.ridal.HDRezka.Streams.Stream
import tv.ridal.HDRezka.Streams.StreamData
import tv.ridal.UI.ActionBar.ActionBar
import tv.ridal.UI.Adapters.PeopleAdapter
import tv.ridal.UI.Cells.PointerCell
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.UI.Popup.BottomPopup
import tv.ridal.UI.Popup.LoadingPopup
import tv.ridal.UI.SpacingItemDecoration
import tv.ridal.UI.View.RTextView
import tv.ridal.Utils.Utils
import kotlin.math.abs
import kotlin.random.Random

class MovieFragment : BaseFragment()
{
    override val stableTag: String
        get() = "Movie${Random.nextInt()}"

    companion object
    {
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

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var actionBar: ActionBar
    private lateinit var scroll: NestedScrollView
    private lateinit var scrollLayout: LinearLayout

    private lateinit var headerView: HeaderView
    private var actorsView: RecyclerView? = null
    private var producersView: RecyclerView? = null

    private lateinit var watchButton: Button
    private lateinit var watchFab: FloatingActionButton

    private val loadingPopup: LoadingPopup = LoadingPopup(context)

    private fun createUI()
    {
        rootFrame = FrameLayout(context).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        createScroll()
        scrollLayout = VLinearLayout(context)
        scroll.addView(scrollLayout)

        headerView = HeaderView()
        createWatchButtons()
        scrollLayout.apply {
            addView(headerView)
            addView(watchButton, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, 40,
                Gravity.CENTER_HORIZONTAL,
                20, 10, 20, 15
            ))
        }

        createActionBar()

        rootFrame.apply {
            addView(scroll, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            ))

            addView(actionBar, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))

            addView(watchFab, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.END,
                0, 0, 12, 12
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

                if (limitHeight in oldScrollY until scrollY) {
                    actionBar.apply {
                        showBackground(1)
                        showTitle()
                    }
                } else if (limitHeight in scrollY until oldScrollY) {
                    actionBar.apply {
                        showBackground(0)
                        hideTitle()
                    }
                }

                val watchBtnHeight = headerView.measuredHeight + watchButton.measuredHeight - actionBar.measuredHeight
                if (watchBtnHeight in oldScrollY until scrollY) {
                    watchFab.show()
                } else if (watchBtnHeight in scrollY until oldScrollY) {
                    watchFab.hide()
                }
            }
        }
    }

    private fun createWatchButtons()
    {
        watchButton = MaterialButton(context).apply {
            gravity = Gravity.CENTER
            setOnTouchListener( InstantPressListener(this) )

            backgroundTintList = null
            background = Theme.createRect(
                Theme.color_main,
                radii = FloatArray(4).apply {
                    fill(Utils.dp(7F))
                }
            )

            icon = Theme.drawable(R.drawable.play)
            iconTint = ColorStateList.valueOf(Theme.COLOR_WHITE)
            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START

            letterSpacing = 0.0F
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
            isAllCaps = false
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor(Theme.COLOR_WHITE)
            text = Locale.text(Locale.text_watch)

            setOnClickListener {
                onWatch()
            }
        }

        watchFab = FloatingActionButton(context).apply {
            setOnTouchListener( InstantPressListener(this) )

            backgroundTintList = ColorStateList.valueOf(Theme.color(Theme.color_main))
            rippleColor = Theme.COLOR_TRANSPARENT

            setImageDrawable(Theme.drawable(R.drawable.play))
            imageTintList = ColorStateList.valueOf(Theme.COLOR_WHITE)

            setOnClickListener {
                onWatch()
            }

            hide()
        }
    }


    private var watchSettingsPopup: WatchSettingsPopup? = null

    private fun onWatch()
    {
        if (document == null)
        {
            createWebView()
            return
        }

        if (watchSettingsPopup == null)
        {
            watchSettingsPopup = WatchSettingsPopup()
        }

        watchSettingsPopup!!.show()
    }

    private lateinit var webView: WebView
    private lateinit var webViewInterface: JSInterface
    private var document: Document? = null

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView()
    {
        loadingPopup.show()

        webViewInterface = JSInterface().apply {
            onLoaded {
                document = Jsoup.parse(it)
                (context as ApplicationActivity).runOnUiThread {
                    loadingPopup.dismiss()
                    onWatch()
                }
            }
        }

        webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(webViewInterface, "HTMLOUT")

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                }
            }

            loadUrl(movie.url)
        }
    }

    inner class JSInterface
    {
        @JavascriptInterface
        fun processHTML(html: String)
        {
            onLoadListener?.invoke(html)
        }

        private var onLoadListener: ((html: String) -> Unit)? = null
        fun onLoaded(l: (html: String) -> Unit)
        {
            onLoadListener = l
        }
    }


    inner class WatchSettingsPopup : BottomPopup(context)
    {
        private lateinit var popupView: FrameLayout

        private var translationView: TranslationView? = null
        private fun hasTranslation() : Boolean = translationView != null
        private var seasonView: SeasonView? = null

        private var parsingDocument: Document

        private lateinit var streamData: StreamData

        init
        {
            isDraggable = false

            parsingDocument = document!!.clone()

            createStreamData()
            createUI()
        }

        override fun show()
        {
            super.show()

            popupView.apply {
                removeAllViews()
                updateLayoutParams<FrameLayout.LayoutParams> {
                    height = LayoutHelper.WRAP_CONTENT
                }
                if (translationView != null) {
                    translationView = TranslationView()
                    addView(translationView)
                } else if (seasonView != null) {
                    seasonView = SeasonView()
                    addView(seasonView)
                }
            }
        }

        private fun createStreamData()
        {
            streamData = if ( ! movie.type.isSerial) {
                FilmStreamData()
            } else SeriesStreamData()
        }

        private fun createUI()
        {
            popupView = FrameLayout(context).apply {
                background = Theme.createRect(
                    Theme.color_bg, radii = floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), Utils.dp(12F), Utils.dp(12F)
                    ))
            }
            // Фильм, Мультфильм (односерийный), Аниме (односерийное)
            if (
                movie.type.ruType == HDRezka.FILM ||
                (movie.type.ruType == HDRezka.CARTOON && ! movie.type.isSerial) ||
                (movie.type.ruType == HDRezka.ANIME && ! movie.type.isSerial)
            )
            {
                // если есть озвучки
                if ( Pager.isTranslatorsExist(document!!) )
                {
                    translationView = TranslationView()
                    popupView.addView(translationView)
                }
                else // если нет озвучек
                {

                }
            }
            else // Сериал, Мультфильм (многосерийный), Аниме (многосерийное)
            {
                // если есть озвучки
                if (Pager.isTranslatorsExist(document!!))
                {
                    translationView = TranslationView()
                    popupView.addView(translationView)
                }
                else // если нет озвучек
                {
                    goToSeason()
                }
            }

            val w = Utils.displayWidth - Utils.dp(12) * 2
            setContentView(popupView, FrameLayout.LayoutParams(
                w, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            ).apply {
                setMargins(0, 0, 0, Utils.dp(12))
            })
        }

        private fun goToSeason()
        {
            // если Сериал без озвучек, ищем id озвучки самостоятельно
            if ( ! Pager.isTranslatorsExist(document!!) )
            {
                val translatorId = Parser.parseTranslatorId(document!!.html())
                streamData.translatorId = translatorId
            }

            // имея id озвучки создаем соответствующий url и отправляем запрос
            // после успешного ответа создаем SeasonView
            // если имеются озвучки -> переходим от экрана TranslationView к SeasonView используя анимацию
            // в противном случае -> просто добавляем SeasonView

            webView.apply {
                webViewInterface.apply {
                    onLoaded {
                        parsingDocument = Jsoup.parse(it)

                        (context as ApplicationActivity).runOnUiThread {
                            seasonView = SeasonView()
                            if (translationView != null) {
                                navigate(translationView!!, seasonView!!)
                                translationView!!.actionBar.hideLoading()
                            } else {
                                popupView.addView(seasonView)
                            }
                        }
                    }
                }
                loadUrl("javascript:(function(){document.querySelectorAll('[data-translator_id=\"${streamData.translatorId}\"]')[0].click();})()")
            }
        }

        private fun navigate(fromView: View, toView: View, animated: Boolean = true)
        {
            toView.measure(0, 0)
            val endHeight = toView.measuredHeight

            if ( ! animated)
            {
                fromView.apply {
                    alpha = 0F
                    visibility = View.GONE
                }
                toView.apply {
                    alpha = 1F
                    visibility = View.VISIBLE
                }

                popupView.apply {
                    updateLayoutParams<FrameLayout.LayoutParams> {
                        height = endHeight
                    }

                    removeView(fromView)
                    popupView.addView(toView, LayoutHelper.createFrame(
                        LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.TOP
                    ))
                }

                return
            }

            fromView.measure(0 ,0)
            val startHeight = fromView.measuredHeight

            val alphaAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                addUpdateListener {
                    val animatedAlpha = it.animatedValue as Float
                    toView.alpha = animatedAlpha
                    fromView.alpha = 1F - animatedAlpha
                }
            }

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {
                    val animatedHeight = it.animatedValue as Int
                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = animatedHeight
                    }
                }
            }

            AnimatorSet().apply {
                duration = 320L + abs(endHeight - startHeight) / 40
                interpolator = DecelerateInterpolator(1.1F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = fromView.measuredHeight
                        }

                        toView.apply {
                            alpha = 0F
                            visibility = View.VISIBLE
                        }

                        popupView.addView(toView, LayoutHelper.createFrame(
                            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                            Gravity.TOP
                        ))
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = LayoutHelper.WRAP_CONTENT
                        }

                        fromView.apply {
                            visibility = View.GONE
                        }

                        popupView.removeView(fromView)
                    }
                })

                playTogether(
                    alphaAnimator,
                    heightAnimator
                )

                start()
            }
        }

        private fun createLayout(layout: VLinearLayout, actionBar: ActionBar, cells: List<PointerCell>)
        {
            layout.apply {
                addView(actionBar)

                for (cell in cells)
                {
                    addView(cell, LayoutHelper.createLinear(
                        LayoutHelper.MATCH_PARENT, 50
                    ))
                }

                addView(View(context), LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, 12
                ))
            }
        }

        inner class TranslationView() : VLinearLayout(context)
        {
            lateinit var actionBar: ActionBar

            init
            {
                createUI()
            }

            private fun createUI()
            {
                createActionBar()

                val cells = ArrayList<PointerCell>()
                // Фильм, Мультфильм (односерийный), Аниме (односерийное)
                if (
                    movie.type.ruType == HDRezka.FILM ||
                    (movie.type.ruType == HDRezka.CARTOON && ! movie.type.isSerial) ||
                    (movie.type.ruType == HDRezka.ANIME && ! movie.type.isSerial)
                )
                {
                    Parser.parseFilmTranslators(document!!).forEach {
                        val cell = PointerCell(context).apply {
                            text = it.name
                            setOnClickListener {
                                println(text)
                            }
                        }
                        cells.add(cell)
                    }
                }
                else // Сериал, Мультфильм (многосерийный), Аниме (многосерийное)
                {
                    Parser.parseSeriesTranslators(document!!).forEach {
                        val cell = PointerCell(context).apply {
                            text = it.name

                            val tid = it.translatorId
                            setOnClickListener {
                                streamData.translatorId = tid
                                actionBar.showLoading()
                                goToSeason()
                            }
                        }
                        cells.add(cell)
                    }
                }

                createLayout(this, actionBar, cells)
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_translation)
                    menu = ActionBar.LoadingMenu(context)
                }
            }
        }

        inner class SeasonView() : VLinearLayout(context)
        {
            private lateinit var actionBar: ActionBar

            init
            {
                createUI()
            }

            private fun createUI()
            {
                createActionBar()

                val cells = ArrayList<PointerCell>()
                Parser.parseSeasons(parsingDocument).forEach {
                    val cell = PointerCell(context).apply {
                        text = it.title

                        val sid = it.id
                        setOnClickListener {
                            (streamData as SeriesStreamData).apply {
                                season = sid
                            }
                            goToEpisode()
                        }
                    }
                    cells.add(cell)
                }

                createLayout(this, actionBar, cells)
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_season)
                    menu = ActionBar.LoadingMenu(context)

                    if ( hasTranslation() )
                    {
                        actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                        onActionButtonClick {
                            backToTranslation()
                        }
                    }
                }
            }

            private fun backToTranslation()
            {
                navigate(this, translationView!!)
            }

            private fun goToEpisode()
            {
                // показываем индикатор загрузки
                actionBar.showLoading()

                val url = updateUrl()
                val request = StringRequest(Request.Method.GET, url,
                    { response ->
                        // прячем индикатор загрузки
                        actionBar.hideLoading()
                        // обновляем документ
                        parsingDocument = Jsoup.parse(response)
                        // переходим к выбору серии
                        val episodeView = EpisodeView()
                        navigate(this, episodeView)
                    },
                    {
                        println("ERROR!")
                    }
                )
                requestQueue.add(request)
            }
        }

        inner class EpisodeView : VLinearLayout(context)
        {
            private lateinit var actionBar: ActionBar

            init
            {
                createActionBar()

                val cells = ArrayList<PointerCell>()
                Parser.parseEpisodes(parsingDocument).forEach {
                    val cell = PointerCell(context).apply {
                        text = it.title

                        val eid = it.id
                        val en = it.number
                        setOnClickListener {
                            (streamData as SeriesStreamData).apply {
                                id = eid
                                episode = en
                            }
                            goWatch()
                        }
                    }
                    cells.add(cell)
                }

                createLayout(this, actionBar, cells)
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_episode)
                    menu = ActionBar.LoadingMenu(context)

                    actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                    onActionButtonClick {
                        backToSeason()
                    }
                }
            }

            private fun backToSeason()
            {
                navigate(this, seasonView!!)
            }
        }

        private fun updateUrl() : String
        {
            val data = streamData as SeriesStreamData
            var url = movie.url
            url += "#t:"
            url += streamData.translatorId

            url += "-s:"
            if (data.season != "") url += data.season
            else url += "1"

            url += "-e:"
            if (data.episode != "") url += data.episode
            else url += "1"

            return url
        }

        private fun goWatch()
        {
            val data = streamData as SeriesStreamData
            println(data.id)
            println(data.translatorId)
            println(data.season)
            println(data.episode)

            GlobalScope.launch(Dispatchers.IO) {
                val doc = Jsoup.connect(Stream.REQUEST_URL)
                    .data("id", data.id)
                    .data("translator_id", data.translatorId)
                    .data("season", data.season)
                    .data("episode", data.episode)
                    .data("action", "get_stream")
                    .post()

                println(doc.html())

                val streams = Parser.parseStreams(doc.html())
                val intent = Intent(context, PlayerActivity::class.java)
                val bundle = Bundle().apply {
                    putParcelableArrayList("key", streams)
                }
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }
    }

    private val requestQueue: RequestQueue = ApplicationLoader().requestQueue
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
                textSize = 16.5F

                text = movieInfo.description!!.text!!
            }
            scrollLayout.addView(tv)
        }

        // актеры
        if ( movieInfo.hasActors() )
        {
            actorsView = createPeopleView(movieInfo.actors!!)

            val actorsSection = SectionView( Locale.text(Locale.text_actors) ).apply {
                setLayout(
                    actorsView!!
                )
            }

            scrollLayout.addView(actorsSection)
        }

        if ( movieInfo.hasProducers() )
        {
            producersView = createPeopleView(movieInfo.producers!!)

            val producersSection = SectionView( Locale.text(Locale.text_producers) ).apply {
                setLayout(
                    producersView!!
                )
            }

            scrollLayout.addView(producersSection)
        }

        // входит в списки
        if ( movieInfo.hasInLists() )
        {
            val layout = VLinearLayout(context)
            val lists = movieInfo.inLists!!
            for (list in lists)
            {
                val cell = PointerCell(context).apply {
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
                layout.addView(cell)
            }

            val section = SectionView( Locale.text(Locale.text_inLists) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setLayout(layout)
            }

            scrollLayout.addView(section)
        }

        // входит в коллекции
        if ( movieInfo.hasInCollections() )
        {
            val layout = VLinearLayout(context)
            val collections = movieInfo.inCollections!!
            for (collection in collections)
            {
                val cell = PointerCell(context).apply {
                    text = collection.name!!

                    setOnClickListener {
                        val args = MoviesFragment.Arguments().apply {
                            title = collection.name!!
                            url = collection.url!!

                            hasSorting = false
                        }
                        startFragment( MoviesFragment.newInstance(args) )
                    }
                }
                layout.addView(cell)
            }

            val section = SectionView( Locale.text(Locale.text_inCollections) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setLayout(layout)
            }

            scrollLayout.addView(section)
        }

        // страна
        if ( movieInfo.hasCountries() )
        {
            val layout = VLinearLayout(context)
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
                layout.addView(countryCell)
            }

            val countriesSection = SectionView( Locale.text(Locale.text_country) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setLayout(layout)
            }

            scrollLayout.addView(countriesSection)
        }

        // жанры
        if ( movieInfo.hasGenres() )
        {
            val layout = VLinearLayout(context).apply {
                setPadding(0, 0, 0, Utils.dp(40))
            }
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
                layout.addView(genreCell)
            }

            val genresSection = SectionView( Locale.text(Locale.text_genre) ).apply {
                setBackgroundColor( Theme.darkenColor(Theme.color_bg, 0.04F) )

                setLayout(layout)
            }

            scrollLayout.addView(genresSection)
        }
    }

    private fun createPeopleView(people: ArrayList<Movie.Person>) : RecyclerView
    {
        return RecyclerView(context).apply {
            setPadding(Utils.dp(8), 0, Utils.dp(8), 0)
            clipToPadding = false

            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration( SpacingItemDecoration(Utils.dp(12), Utils.dp(5), Utils.dp(10)) )

            adapter = PeopleAdapter(people)
        }
    }

    // pause

    inner class HeaderView : FrameLayout(context)
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

    inner class SectionView(sectionName: String) : LinearLayout(context)
    {
        private var sectionNameView: TextView
        private var container: FrameLayout

        init
        {
            orientation = LinearLayout.VERTICAL

            sectionNameView = createSectionNameView(sectionName)
            addView(sectionNameView)

            container = FrameLayout(context)
            addView(container, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))
        }

        fun setLayout(layout: View)
        {
            if (container.childCount > 0) container.removeAllViews()
            container.addView(layout)
        }
    }

    private fun createSectionNameView(text: String) : RTextView
    {
        return RTextView(context).apply {
            setPadding(Utils.dp(20), Utils.dp(15), Utils.dp(20), Utils.dp(5))

            this.text = text

            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor(Theme.color(Theme.color_main))
        }
    }

}





































//