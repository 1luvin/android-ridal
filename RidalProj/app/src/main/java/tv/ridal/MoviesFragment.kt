package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.ridal.adapter.MoviesAdapter
import tv.ridal.util.Theme
import tv.ridal.ui.actionbar.ActionBar
import tv.ridal.util.Locale
import tv.ridal.ui.cell.FilterCell
import tv.ridal.ui.recyclerview.GridSpacingItemDecoration
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.SingleCheckGroup
import tv.ridal.ui.popup.BottomPopup
import tv.ridal.hdrezka.*
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.measure
import tv.ridal.ui.msg
import tv.ridal.util.Utils
import tv.ridal.ui.setBackgroundColor
import kotlin.math.abs
import kotlin.random.Random

class MoviesFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "Movies${Random.nextInt()}"


    companion object
    {
        fun newInstance(args: Arguments) = MoviesFragment().apply {
            arguments = args
        }
    }


    private lateinit var arguments: Arguments
    class Arguments
    {
        lateinit var title: String
        var url: String? = null

        lateinit var filters: Filters

        var applyGenre: String? = null
        var applySection: String? = null
    }

    private fun updateSubtitle()
    {
        var str = ""
        if (activeGenre != null) str += activeGenre
        else if (activeSection != null) str += activeSection

        if (str != "") str += ", "

        if (activeSorting != null) str += activeSorting

        actionBar.subtitle = str
    }

    /*
        Фильтры
     */

    private lateinit var filtersPopup: FiltersPopup

    private var genres: LinkedHashMap<String, String>? = null
    private var genreNames: Array<String>? = null
    private var activeGenre: String? = null
    private var sortings: Array<String>? = null
    private var activeSorting: String? = null
    private var sections: List<String>? = null
    private var activeSection: String? = null

    private fun hasGenres(): Boolean = genres != null
    private fun hasSections(): Boolean = sections != null
    private fun hasSortings(): Boolean = sortings != null

    private var document: Document? = null

    /*
        UI компоненты
     */

    private lateinit var rootFrame: FrameLayout
    private lateinit var actionBar: ActionBar

    private lateinit var moviesView: RecyclerView
    private var heightIndicator: Int = 0 // !

    private lateinit var filtersButton: FloatingActionButton

    private val movies: ArrayList<Movie> = ArrayList()

    private var loading: Boolean = false

    private val requestQueue: RequestQueue = App.instance().requestQueue
    private val moviesTag: String = "moviesTag"


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()

        checkFilters()

        loadMovies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onDestroy()
    {
        super.onDestroy()

        requestQueue.cancelAll(moviesTag)
    }


    private fun createUI()
    {
        createActionBar()
        createMoviesView()

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )

            addView(actionBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.TOP
            ))
            actionBar.measure()

            addView(moviesView, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.TOP,
                0, actionBar.measuredHeight, 0, 0
            ))
        }

        if (arguments.filters != Filters.NO_FILTERS)
        {
            createFiltersButton()
            rootFrame.addView(
                filtersButton, Layout.ezFrame(
                    Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                    Gravity.END or Gravity.BOTTOM,
                    0, 0, 12, 12
                )
            )
        }
    }

    private fun createActionBar()
    {
        actionBar = ActionBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)
            setBackgroundColor( Theme.color(Theme.color_bg) )

            onBack {
                this@MoviesFragment.finish()
            }

            title = arguments.title
        }
    }

    private fun createMoviesView()
    {
        moviesView = RecyclerView(context).apply {
            setPadding( Utils.dp(9), 0, Utils.dp(9), 0 )
            clipToPadding = false

            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }

            layoutManager = GridLayoutManager( context, 3 )
            addItemDecoration( GridSpacingItemDecoration(3, Utils.dp(11)) )

            adapter = MoviesAdapter(movies).apply {
                onMovieClick {
                    startFragment( MovieFragment.newInstance(it) )
                }
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val scrollY = computeVerticalScrollOffset()
                    val range = computeVerticalScrollRange()

                    if ( heightIndicator == 0 ) {
                        heightIndicator = range / 2
                    }

                    if ( arguments.filters != Filters.NO_FILTERS )
                    {
                        if ( abs(dy) > Utils.dp(2) )
                        {
                            filtersButton.apply {
                                if ( dy > 0 ) hide()
                                else show()
                            }
                        }

                        if ( scrollY == 0 || scrollY == range) {
                            filtersButton.show()
                        }
                    }

                    actionBar.elevation = if ( scrollY > 0 ) {
                        Utils.dp(5F)
                    } else {
                        0F
                    }

                    if (scrollY > range - heightIndicator)
                    {
                        if ( ! loading)
                        {
                            loadMovies()
                        }
                    }
                }
            })
        }
    }

    private fun createFiltersButton()
    {
        filtersButton = FloatingActionButton(context).apply {
            setOnTouchListener( InstantPressListener(this) )

            backgroundTintList = ColorStateList.valueOf( Theme.color(Theme.color_main) )

            setImageDrawable( Theme.drawable(R.drawable.sett) )
            imageTintList = ColorStateList.valueOf( Color.WHITE )

            setOnClickListener {
                filtersPopup.show()
            }
        }
    }

    private fun createFiltersPopup()
    {
        filtersPopup = FiltersPopup().apply {
            onNewFilters {
                clearMovies()
                loadMovies()
            }
        }
    }


    private fun checkFilters()
     {
        if (arguments.filters == Filters.NO_FILTERS) return

        val title = arguments.title
        if (title in HDRezka.sectionNames)
        {
            genres = Genre.createGenres(title)
            genreNames = genres!!.keys.toTypedArray()
            activeGenre = arguments.applyGenre ?: Locale.string(R.string.allGenres)
        }

        sortings = arrayOf(
            Locale.string(R.string.sorting_watching),
            Locale.string(R.string.sorting_popular),
            Locale.string(R.string.sorting_last)
        )
        activeSorting = sortings!![0]

        if (arguments.filters == Filters.SECTION_SORTING)
        {
            sections = HDRezka.sectionNames
            activeSection = arguments.applySection ?: sections!![0]
        }

        createFiltersPopup()
    }

    private fun loadMovies()
    {
        loading = true

        val url: String = nextUrl() ?: return

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                loading = false

                document = Jsoup.parse(response)

                val newMovies = Parser.parseMovies(document!!) ?: return@StringRequest

                movies.addAll(newMovies)

                (moviesView.adapter as MoviesAdapter).notifyItemRangeInserted(movies.size, newMovies.size)
            },
            {
                println("ERROR!")
            }
        ).apply {
            tag = moviesTag
        }

        requestQueue.add(request)
    }

    private fun nextUrl() : String?
    {
        if (document == null)
        {
            updateSubtitle()

            val url = HDRezka.createUrl(
                baseUrl = arguments.url!!,
                genreUrl = Genre.url(genres, activeGenre),
                sortingUrl = Sorting.url(activeSorting),
                sectionUrl = Section.url(activeSection)
            )

            msg(url)

            return url
        }

        return if ( Pager.isNextPageExist( document!! ) ) {
            Pager.nextPageUrl( document!! )
        } else {
            loading = false
            null
        }
    }

    private fun clearMovies()
    {
        document = null
        movies.clear()
        moviesView.adapter!!.notifyDataSetChanged()
    }

    inner class FiltersPopup : BottomPopup(context)
    {
        private lateinit var popupView: FrameLayout

        private var filtersView: FiltersView? = null
        private var genreView: GenreView? = null
        private lateinit var sortingView: SortingView
        private var sectionView: SectionView? = null

        private var bottomLayout: FrameLayout? = null
        private var showResultsButton: TextView? = null

        private lateinit var currentView: View

        init
        {
            isDraggable = false

            createUi()

            setOnShowListener {
                onFiltersOpen()
            }
        }

        private fun createUi()
        {
            // создание контейнера для View с фильтрами
            popupView = FrameLayout(context).apply {
                background = Theme.rect(
                    Theme.color_bg, radii = FloatArray(4).apply {
                        fill( Utils.dp(15F) )
                    })
            }

            if (arguments.filters != Filters.SORTING)
            {
                createBottomLayout()
                popupView.addView(bottomLayout, Layout.ezFrame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.BOTTOM,
                ))

                filtersView = FiltersView().apply {
                    sortingCell.setOnClickListener {
                        navigate(filtersView!!, sortingView)
                    }
                }
                popupView.addView(filtersView, Layout.frame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, bottomLayout!!.measuredHeight
                ))

                sortingView = SortingView().apply {
                    visibility = View.GONE
                }

                currentView = filtersView!!

                if (hasGenres())
                {
                    filtersView!!.apply {
                        genreCell!!.setOnClickListener {
                            navigate(filtersView!!, genreView!!)
                        }
                    }

                    genreView = GenreView().apply {
                        visibility = View.GONE
                    }
                }

                if (hasSections())
                {
                    filtersView!!.apply {
                        sectionCell!!.setOnClickListener {
                            navigate(filtersView!!, sectionView!!)
                        }
                    }

                    sectionView = SectionView().apply {
                        visibility = View.GONE
                    }
                }
            }
            else
            {
                sortingView = SortingView()
                popupView.addView(sortingView, Layout.frame(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    Gravity.TOP
                ))

                currentView = sortingView
            }

            val w = Utils.displayWidth - Utils.dp(12) * 2
            setContentView(popupView, FrameLayout.LayoutParams(
                w, Layout.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            ).apply {
                setMargins( 0, 0, 0, Utils.dp(12) )
            })
        }

        private fun navigate(fromView: View, toView: View, animated: Boolean = true)
        {
            currentView = toView

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
                        height = endHeight + bottomLayout!!.measuredHeight
                    }

                    removeView(fromView)
                    popupView.addView(toView, Layout.frame(
                        Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                        Gravity.TOP,
                        0, 0, 0, bottomLayout!!.measuredHeight
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
                        height = animatedHeight + bottomLayout!!.measuredHeight
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
                            height = fromView.measuredHeight + showResultsButton!!.measuredHeight
                        }

                        toView.apply {
                            alpha = 0F
                            visibility = View.VISIBLE
                        }

                        popupView.addView(toView, Layout.frame(
                            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                            Gravity.TOP,
                            0, 0, 0, bottomLayout!!.measuredHeight
                        ))
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = Layout.WRAP_CONTENT
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

        private fun createBottomLayout()
        {
            showResultsButton = Button(context).apply {
                gravity = Gravity.CENTER
                setOnTouchListener( InstantPressListener(this) )

                background = null

                setTextSize( TypedValue.COMPLEX_UNIT_DIP, 17F )
                isAllCaps = false
                typeface = Theme.typeface(Theme.tf_bold)
                setTextColor( Theme.mainColor )

                text = Locale.string(R.string.showResults)

                setOnClickListener {
                    val isFiltersChanged = applyNewFilters()
                    if (isFiltersChanged) {
                        newFiltersListener?.invoke()
                    }

                    this@FiltersPopup.dismiss()
                }
            }

            val divider = View(context).apply {
                background = Theme.rect(
                    Theme.overlayColor( Theme.color_bg, 0.07F )
                )
            }

            bottomLayout = FrameLayout(context).apply {
                addView(showResultsButton, Layout.ezFrame(
                    Layout.MATCH_PARENT, 56
                ))

                addView(divider, Layout.ezFrame(
                    Layout.MATCH_PARENT, 1,
                    Gravity.TOP
                ))

                measure()
            }
        }


        private fun applyNewFilters(): Boolean
        {
            var changed = false
            if ( hasGenres() )
            {
                val newGenre = genreView!!.currentGenre()
                if (activeGenre != newGenre) {
                    activeGenre = newGenre
                    changed = true
                }
            }

            if ( hasSections() )
            {
                val newSection = sectionView!!.currentSection()
                if (activeSection != newSection) {
                    activeSection = newSection
                    changed = true
                }
            }

            val newSorting = sortingView.currentSorting()
            if (activeSorting != newSorting) {
                activeSorting = newSorting
                changed = true
            }

            return changed
        }

        private fun checkDefaultFilters(): Boolean
        {
            if ( hasGenres() ) {
                if ( activeGenre != genreNames!![0] ) {
                    activeGenre = genreNames!![0]
                    return false
                }
            }

            if ( hasSections() ) {
                if ( activeSection != sections!![0] ) {
                    activeSection = sections!![0]
                    return false
                }
            }

            if ( hasSortings() ) {
                if ( activeSorting != sortings!![0] ) {
                    activeSorting = sortings!![0]
                    return false
                }
            }

            return true
        }


        private fun onFiltersOpen()
        {
            if (filtersView != null)
            {
                // Если экран при закрытии не равен экрану с фильтрами
                // Показываем экран с фильтрами
                if (currentView != filtersView)
                {
                    navigate(currentView, filtersView!!, false)
                }

                if (hasGenres())
                {
                    filtersView!!.genreCell!!.filterValue = activeGenre!!

                    genreView!!.genresCheckGroup.check(activeGenre!!)
                }
                if (hasSections())
                {
                    filtersView!!.sectionCell!!.filterValue = activeSection!!

                    sectionView!!.sectionsCheckGroup.check(activeSection!!)
                }

                filtersView!!.sortingCell.filterValue = activeSorting!!
            }

            sortingView.sortingCheckGroup.check(activeSorting!!)
        }

        private var newFiltersListener: (() -> Unit)? = null
        fun onNewFilters(l: () -> Unit)
        {
            newFiltersListener = l
        }


        inner class FiltersView : VLinearLayout(context)
        {
            private lateinit var actionBar: ActionBar

            var genreCell: FilterCell? = null
            lateinit var sortingCell: FilterCell
            var sectionCell: FilterCell? = null

            private val CELL_SPACING: Int = 12

            init
            {
                createUi()
            }

            private fun createUi()
            {
                createActionBar()
                addView(actionBar)
                // Фильтры могут содержать либо Жанры либо Секции, вместе не может быть
                if (hasGenres())
                {
                    genreCell = FilterCell(context).apply {
                        filterName = Locale.string(R.string.genre)
                        filterValue = activeGenre!!
                    }
                    addView(genreCell, Layout.ezLinear(
                        Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                        0, CELL_SPACING, 0, 0
                    ))
                }
                else if (hasSections())
                {
                    sectionCell = FilterCell(context).apply {
                        filterName = Locale.string(R.string.section)
                        filterValue = activeSection!!
                    }
                    addView(sectionCell, Layout.ezLinear(
                        Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                        0, CELL_SPACING, 0, 0
                    ))
                }

                sortingCell = FilterCell(context).apply {
                    filterName = Locale.string(R.string.sorting)
                    filterValue = activeSorting!!
                }
                addView(sortingCell, Layout.ezLinear(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    0, CELL_SPACING, 0, CELL_SPACING
                ))
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.string(R.string.filters)

                    menu = ActionBar.Menu(context).apply {
                        addItem( Theme.drawable( R.drawable.refresh, Theme.color_text ) ) {

                            if ( ! checkDefaultFilters() ) {
                                newFiltersListener?.invoke()
                            }

                            this@FiltersPopup.dismiss()
                        }
                    }
                }
            }
        }

        inner class GenreView : FrameLayout(context)
        {
            private lateinit var actionBar: ActionBar

            private lateinit var scroll: NestedScrollView
            private var scrollHeight: Int = 0 // !
            lateinit var genresCheckGroup: SingleCheckGroup

            init
            {
                createActionBar()
                addView(actionBar)

                createScroll()
                addView(scroll, Layout.frame(
                    Layout.MATCH_PARENT, scrollHeight,
                    Gravity.START or Gravity.TOP,
                    0, actionBar.measuredHeight, 0, 0
                ))
            }

            override fun onVisibilityChanged(changedView: View, visibility: Int)
            {
                super.onVisibilityChanged(changedView, visibility)
                // открытие жанров
                if (visibility == View.VISIBLE)
                {
                    genresCheckGroup.check( filtersView!!.genreCell!!.filterValue, false )
                    genresCheckGroup.moveCheckedOnTop()

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.string(R.string.genre)

                    onBack {
                        navigate(genreView!!, filtersView!!)
                    }
                }

                actionBar.measure()
            }

            private fun createScroll()
            {
                genresCheckGroup = SingleCheckGroup(context).apply {
                    genreNames!!.forEach {
                        addCheck(it) {
                            filtersView!!.genreCell!!.filterValue = it
                            navigate(genreView!!, filtersView!!)
                        }
                    }
                    check(activeGenre!!)

                    measure(0, 0)
                }
                scroll = NestedScrollView(context).apply {
                    setPadding(0, 0, 0, Utils.dp(12))
                    clipToPadding = false

                    addView(genresCheckGroup)
                }
                val availableHeight = (Utils.displayHeight * 0.65).toInt() - (actionBar.measuredHeight + bottomLayout!!.measuredHeight)
                scrollHeight = if (genresCheckGroup.measuredHeight < availableHeight) {
                    Layout.WRAP_CONTENT
                } else {
                    availableHeight
                }
            }

            fun currentGenre(): String
            {
                return genresCheckGroup.currentChecked()
            }
        }

        inner class SortingView : FrameLayout(context)
        {
            private lateinit var actionBar: ActionBar

            private lateinit var scroll: NestedScrollView
            private var scrollHeight: Int = 0 // !
            lateinit var sortingCheckGroup: SingleCheckGroup

            init
            {
                createActionBar()
                addView(actionBar)

                createScroll()
                addView(scroll, Layout.frame(
                    Layout.MATCH_PARENT, scrollHeight,
                    Gravity.START or Gravity.TOP,
                    0, actionBar.measuredHeight, 0, 0
                ))
            }

            override fun onVisibilityChanged(changedView: View, visibility: Int)
            {
                super.onVisibilityChanged(changedView, visibility)
                // открытие сортировки
                if (visibility == View.VISIBLE)
                {
                    // проверить на нулл !!
                    if (filtersView != null) sortingCheckGroup.check( filtersView!!.sortingCell.filterValue, false )

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.string(R.string.sorting)

                    filtersView?.let {
                        onBack {
                            navigate(sortingView, it)
                        }
                    }
                }

                actionBar.measure()
            }

            private fun createScroll()
            {
                sortingCheckGroup = SingleCheckGroup(context).apply {
                    sortings!!.forEach {
                        addCheck(it) {
                            if (filtersView != null)
                            {
                                filtersView!!.sortingCell.filterValue = it
                                navigate(sortingView, filtersView!!)
                            }
                            else
                            {
                                if (it != activeSorting)
                                {
                                    activeSorting = it
                                    newFiltersListener?.invoke()
                                }
                                this@FiltersPopup.dismiss()
                            }
                        }
                    }
                    check(activeSorting!!)

                    measure(0, 0)
                }
                scroll = NestedScrollView(context).apply {
                    setPadding(0, 0, 0, Utils.dp(12))
                    clipToPadding = false

                    addView(sortingCheckGroup)
                }
                val availableHeight = (Utils.displayHeight * 0.65).toInt() - actionBar.measuredHeight
                if (filtersView != null) availableHeight - bottomLayout!!.measuredHeight
                scrollHeight = if (sortingCheckGroup.measuredHeight < availableHeight) {
                    Layout.WRAP_CONTENT
                } else {
                    availableHeight
                }
            }

            fun currentSorting(): String
            {
                return sortingCheckGroup.currentChecked()
            }
        }

        inner class SectionView : FrameLayout(context)
        {
            private lateinit var actionBar: ActionBar

            private lateinit var scroll: NestedScrollView
            private var scrollHeight: Int = 0 // !
            lateinit var sectionsCheckGroup: SingleCheckGroup

            init
            {
                createUI()
            }

            override fun onVisibilityChanged(changedView: View, visibility: Int)
            {
                super.onVisibilityChanged(changedView, visibility)
                // открытие сортировки
                if (visibility == View.VISIBLE)
                {
                    // проверить на нулл !!
                    sectionsCheckGroup.check( filtersView!!.sectionCell!!.filterValue, false )

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createUI()
            {
                createActionBar()
                addView(actionBar)

                createScroll()
                addView(scroll, Layout.frame(
                    Layout.MATCH_PARENT, scrollHeight,
                    Gravity.START or Gravity.TOP,
                    0, actionBar.measuredHeight, 0, 0
                ))
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.string(R.string.section)

                    filtersView?.let {
                        onBack {
                            navigate(sectionView!!, it)
                        }
                    }
                }

                actionBar.measure()
            }

            private fun createScroll()
            {
                sectionsCheckGroup = SingleCheckGroup(context).apply {
                    sections!!.forEach {
                        addCheck(it) {
                            filtersView!!.sectionCell!!.filterValue = it
                            navigate(sectionView!!, filtersView!!)
                        }
                    }
                    check(activeSection!!)

                    measure(0, 0)
                }
                scroll = NestedScrollView(context).apply {
                    setPadding(0, 0, 0, Utils.dp(12))
                    clipToPadding = false

                    addView(sectionsCheckGroup)
                }
                val availableHeight = (Utils.displayHeight * 0.65).toInt() - (actionBar.measuredHeight + bottomLayout!!.measuredHeight)
                scrollHeight = if (sectionsCheckGroup.measuredHeight < availableHeight) {
                    Layout.WRAP_CONTENT
                } else {
                    availableHeight
                }
            }

            fun currentSection() : String
            {
                return sectionsCheckGroup.currentChecked()
            }
        }
    }

}






































//