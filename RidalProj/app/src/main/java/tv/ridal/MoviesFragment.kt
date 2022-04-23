package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.ColorStateList
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
import com.android.volley.toolbox.Volley
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
import tv.ridal.util.Utils
import tv.ridal.ui.msg
import tv.ridal.ui.setBackgroundColor
import kotlin.math.abs

class MoviesFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "MoviesFragment${View.generateViewId()}"

    companion object
    {
        fun newInstance(args: Arguments): MoviesFragment
        {
            return MoviesFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var arguments: Arguments
    class Arguments
    {
        lateinit var title: String
        var url: String? = null

        lateinit var filters: HDRezka.Filters

        var applyGenre: String? = null
        var applySection: String? = null
    }

    private var subtitle: String? = null
        set(value) {
            if (value == null) return
            field = value
            actionBar.subtitle = subtitle!!
        }
    private fun updateSubtitle()
    {
        var str = ""
        if (activeGenre != null) str += activeGenre
        else if (activeSection != null) str += activeSection

        if (str != "") str += ", "

        if (activeSorting != "") str += activeSorting

        subtitle = str
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
    private var sections: Array<String>? = null
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
    private lateinit var moviesFrame: FrameLayout
    private lateinit var moviesView: RecyclerView
    private lateinit var filtersButton: FloatingActionButton

    private val movies: ArrayList<Movie> = ArrayList()

    private var loading: Boolean = false

    private val requestQueue: RequestQueue = Volley.newRequestQueue( App.instance() )
    private val requestTagMovies: String = "MoviesFragment"


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


    private fun createUI()
    {
        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )
        }

        createActionBar()
        rootFrame.addView(
            actionBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.TOP
            )
        )

        moviesFrame = FrameLayout(context)
        rootFrame.addView(
            moviesFrame, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.TOP,
                0, ActionBar.actionBarHeightDp + 30, 0, 0
            )
        )

        createMoviesView()
        moviesFrame.addView(moviesView)

        if (arguments.filters != HDRezka.Filters.NO_FILTERS)
        {
            createFiltersButton()
            moviesFrame.addView(
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

            if ( ! Theme.isDark() ) // если светлая тема
            {
                elevation = Utils.dp(5F)
            }

            addIosBack()
            onBack {
                finish()
            }

            title = arguments.title
        }
    }

    private fun checkFilters()
    {
        if (arguments.filters == HDRezka.Filters.NO_FILTERS) return

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

        if (arguments.filters == HDRezka.Filters.SECTION_SORTING)
        {
            sections = HDRezka.sectionNames
            activeSection = arguments.applySection ?: sections!![0]
        }

        createFiltersPopup()
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

    private fun createMoviesView()
    {
        moviesView = RecyclerView(requireContext()).apply {
            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }
            clipToPadding = false

            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(GridSpacingItemDecoration(3, Utils.dp(15)))

            adapter = MoviesAdapter(movies).apply {
                onMovieClick {
                    val movieFragment = MovieFragment.instance(it)
                    startFragment(movieFragment)
                }
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val offset = recyclerView.computeVerticalScrollOffset()
                    val range = recyclerView.computeVerticalScrollRange()
                    if (offset > range / 2)
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

            backgroundTintList = ColorStateList.valueOf(Theme.color(Theme.color_main))

            setImageDrawable(Theme.drawable(R.drawable.sett))
            imageTintList = ColorStateList.valueOf(Theme.COLOR_WHITE)

            setOnClickListener {
                filtersPopup.show()
            }
        }
    }

    private fun loadMovies()
    {
        loading = true

        var url = ""
        if (document == null)
        {
            updateSubtitle()

            url = arguments.url!!
            if ( hasGenres() ) {
                url += genres!![activeGenre!!]
            }
            if ( hasSortings() ) {
                url += Sorting.url(activeSorting!!)
            }
            if ( hasSections() ) {
                url += Section.url(activeSection!!)
            }
        }
        else {
            if (Pager.isNextPageExist(document!!)) {
                url = Pager.nextPageUrl(document!!)
            } else {
                loading = false
                return
            }
        }

        msg(url)

        val moviesRequest = StringRequest(
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
            tag = requestTagMovies
        }

        requestQueue.add(moviesRequest)
    }

    private fun clearMovies()
    {
        document = null
        movies.clear()
        moviesView.adapter!!.notifyDataSetChanged()
    }

    inner class FiltersPopup() : BottomPopup(context)
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
                    Theme.color_bg, radii = floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), Utils.dp(12F), Utils.dp(12F)
                    ))
            }

            if (arguments.filters != HDRezka.Filters.SORTING)
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
                setMargins(0, 0, 0, Utils.dp(12))
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

                this.text = Locale.string(R.string.showResults)

                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17F)
                isAllCaps = false
                typeface = Theme.typeface(Theme.tf_bold)
                setTextColor(Theme.mainColor)

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
                    Theme.overlayColor(Theme.color_bg, 0.07F)
                )
            }

            bottomLayout = FrameLayout(context).apply {
                addView(showResultsButton, Layout.ezFrame(
                    Layout.MATCH_PARENT, 60,
                    Gravity.BOTTOM,
                    20, 0, 20, 0
                ))

                addView(divider, Layout.frame(
                    Layout.MATCH_PARENT, Utils.dp(1),
                    Gravity.TOP
                ))

                measure(0, 0)
            }
        }

        private fun applyNewFilters(): Boolean
        {
            var changed = false
            if (hasGenres())
            {
                val newGenre = genreView!!.currentGenre()
                if (activeGenre != newGenre) {
                    activeGenre = newGenre
                    changed = true
                }
            }

            if (hasSections())
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
                        addItem(Theme.drawable(R.drawable.refresh, Theme.color_text)) {
                            if (hasGenres())
                            {
                                activeGenre = genreNames!![0]
                            }
                            if (hasSections())
                            {
                                activeSection = sections!![0]
                            }
                            if (hasSortings())
                            {
                                activeSorting = sortings!![0]
                            }

                            newFiltersListener?.invoke()
                            // Закрываем Фильтры
                            dismiss()
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

                    addIosBack( Locale.string(R.string.filters) )
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
                    sortingCheckGroup.moveCheckedOnTop()

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.string(R.string.sorting)

                    filtersView?.let {
                        addIosBack( Locale.string(R.string.filters) )
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
                    sectionsCheckGroup.moveCheckedOnTop()

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
                        addIosBack( Locale.string(R.string.filters) )
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