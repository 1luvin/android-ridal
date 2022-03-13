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
import tv.ridal.UI.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.UI.ActionBar.ActionBar
import tv.ridal.Application.Locale
import tv.ridal.UI.Cells.FilterCell
import tv.ridal.UI.GridSpacingItemDecoration
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.UI.Layout.SingleCheckGroup
import tv.ridal.UI.Popup.BottomPopup
import tv.ridal.HDRezka.*
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.Utils.Utils
import kotlin.math.abs

class MoviesFragment : BaseFragment()
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

        var hasSections: Boolean = false
        var hasSorting: Boolean = true
        var applySection: String? = null

        var applyGenre: String? = null
    }

    private var subtitle: String? = null
        set(value) {
            if (value == null) return
            field = value
            actionBar.subtitle = subtitle!!
        }
    private fun setSubtitle(genre: String, sorting: String)
    {
        subtitle = "$genre, $sorting"
    }

    /*
        Фильтры
     */

    private lateinit var filtersPopup: FiltersPopup

    private var genres: List<String>? = null
    private var activeGenre: String? = null
    private var sortings: List<String> = listOf(
        Locale.text(Locale.sorting_last),
        Locale.text(Locale.sorting_popular),
        Locale.text(Locale.sorting_watching)
    )
    private var activeSorting: String = sortings[0]
    private var sections: List<String>? = null
    private var activeSection: String? = null

    private fun hasGenres(): Boolean = genres != null
    private fun hasSections(): Boolean = sections != null

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

    private val requestQueue: RequestQueue = Volley.newRequestQueue( ApplicationLoader.instance() )
    private val requestTagMovies: String = "requestTagMovies"


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUi()

        checkFilters()

        loadMovies()
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

        cancelRequests()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }


    private fun cancelRequests()
    {
        requestQueue.cancelAll(requestTagMovies)
    }


    private fun createUi()
    {
        rootFrame = FrameLayout(context).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        actionBar = ActionBar(requireContext()).apply {
            setPadding(0, Utils.dp(25), 0, 0)
            setBackgroundColor(Theme.alphaColor(Theme.color_bg, 0.9F))

            actionButtonIcon = Theme.drawable(R.drawable.back)
            actionButtonColor = Theme.color(Theme.color_actionBar_back)
            onActionButtonClick {
                finish()
            }

            title = arguments.title ?: ""
        }

        rootFrame.addView(actionBar, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
            Gravity.TOP
        ))

        moviesFrame = FrameLayout(requireContext())
        rootFrame.addView(moviesFrame, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
            Gravity.TOP,
            0, ActionBar.actionBarHeightDp + 25, 0, 0
        ))

        createMoviesView()
        moviesFrame.addView(moviesView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))

        createFiltersButton()
        moviesFrame.addView(filtersButton, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.END or Gravity.BOTTOM,
            0, 0, 12, 12
        ))
    }

    private fun checkFilters()
    {
        val title = arguments.title
        if (title in HDRezka.SECTION_NAMES)
        {
            genres = Genre.createGenres(title)
            activeGenre = if (arguments.applyGenre != null) {
                arguments.applyGenre!!
            } else {
                Locale.text(Locale.text_allGenres)
            }
        }

        if (arguments.hasSections)
        {
            sections = HDRezka.SECTION_NAMES
            activeSection = if (arguments.applySection != null) {
                arguments.applySection
            } else {
                sections!![0]
            }
        }

        createFiltersPopup()
    }

    private fun createFiltersPopup()
    {
        filtersPopup = FiltersPopup().apply {
            onNewFilters {
                setSubtitle(activeGenre ?: "", activeSorting)

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
                    val movieFragment = MovieFragment.newInstance(it)
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
        filtersButton = FloatingActionButton(requireContext()).apply {
            backgroundTintList = ColorStateList.valueOf(Theme.color(Theme.color_main))
            rippleColor = Theme.ripplizeColor(Theme.color_main)

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
            url = arguments.url!!
            if ( hasGenres() ) {
                println(activeGenre)
                url += Genre.url(activeGenre!!)
            }
            if ( arguments.hasSorting ) {
                url += Sorting.url(activeSorting)
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

        println(url)

        val moviesRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                loading = false

                println("LOADED")

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

        private lateinit var bottomLayout: FrameLayout
        private lateinit var showResultsButton: TextView

        private lateinit var currentView: View

        init
        {
            createUi()

            setOnShowListener {
                onFiltersOpen()
            }

            isDraggable = false
        }

        private fun createUi()
        {
            // создание контейнера для View с фильтрами
            popupView = FrameLayout(context).apply {
                background = Theme.createRect(
                    Theme.color_bg, radii = floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), Utils.dp(12F), Utils.dp(12F)
                    ))
            }
            // добавление кнопки <Показать результаты>
            createBottomLayout()
            popupView.addView(bottomLayout, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.BOTTOM,
            ))

            // если имеются Жанры или Секции (Фильмы, Сериалы, ...)
            // добавляем FiltersView
            if (hasGenres() || hasSections())
            {
                filtersView = FiltersView().apply {
                    sortingCell.setOnClickListener {
                        navigate(filtersView!!, sortingView)
                    }
                }
                popupView.addView(filtersView, LayoutHelper.createFrame2(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, bottomLayout.measuredHeight
                ))

                currentView = filtersView!!
            }

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
//                popupView.addView(genreView, LayoutHelper.createFrame2(
//                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
//                    Gravity.TOP,
//                    0, 0, 0, bottomLayout.measuredHeight
//                ))
            }

            if (hasSections())
            {
                filtersView!!.apply {
                    sectionCell!!.setOnClickListener {
                        navigate(filtersView!!, sectionView!!)
                    }
                }

                genreView = GenreView().apply {
                    visibility = View.GONE
                }
            }

            sortingView = SortingView().apply {
                if (filtersView != null) {
                    visibility = View.GONE
                } else {
                    currentView = this
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
                        height = endHeight + bottomLayout.measuredHeight
                    }

                    removeView(fromView)
                    popupView.addView(toView, LayoutHelper.createFrame2(
                        LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                        Gravity.TOP,
                        0, 0, 0, bottomLayout.measuredHeight
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
                        height = animatedHeight + bottomLayout.measuredHeight
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
                            height = fromView.measuredHeight + showResultsButton.measuredHeight
                        }

                        toView.apply {
                            alpha = 0F
                            visibility = View.VISIBLE
                        }

                        popupView.addView(toView, LayoutHelper.createFrame2(
                            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                            Gravity.TOP,
                            0, 0, 0, bottomLayout.measuredHeight
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

        private fun createBottomLayout()
        {
            showResultsButton = Button(context).apply {
                gravity = Gravity.CENTER
                setOnTouchListener( InstantPressListener(this) )

                background = Theme.createRect(
                    Theme.color_main,
                    radii = FloatArray(4).apply {
                        fill(Utils.dp(7F))
                    }
                )

                this.text = Locale.text(Locale.text_showResults)

                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                isAllCaps = false
                typeface = Theme.typeface(Theme.tf_bold)
                setTextColor(Theme.COLOR_WHITE)

                setOnClickListener {
                    val isFiltersChanged = applyNewFilters()
                    if (isFiltersChanged) {
                        newFiltersListener?.invoke()
                    }

                    this@FiltersPopup.dismiss()
                }
            }

            bottomLayout = FrameLayout(context).apply {
                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
                )

                addView(showResultsButton, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, 46,
                    Gravity.BOTTOM,
                    20, 10, 20, 10
                ))
            }

            bottomLayout.measure(0, 0)
        }

        private fun applyNewFilters(): Boolean
        {
            var changed = false
            if (hasGenres()) {
                val newGenre = genreView!!.currentGenre()
                if (activeGenre != newGenre) {
                    activeGenre = newGenre
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
            // если фильтры существуют
            if (filtersView != null)
            {
                // Если экран при закрытии не равен экрану с фильтрами
                // Показываем экран с фильтрами
                if (currentView != filtersView)
                {
                    navigate(currentView, filtersView!!, false)
                }

                if (hasGenres()) {
                    filtersView!!.genreCell!!.filterValue = activeGenre!!

                    genreView!!.singleCheckGroup.check(activeGenre!!)
                }
                if (hasSections()) {
                    filtersView!!.sectionCell!!.filterValue = activeSection!!

                    // sectionView!!.checkBoxGroup.check("xxx", "xxx")
                }

                filtersView!!.sortingCell.filterValue = activeSorting
            }

            sortingView.singleCheckGroup.check(activeSorting)
        }

        private var newFiltersListener: (() -> Unit)? = null
        fun onNewFilters(l: () -> Unit)
        {
            newFiltersListener = l
        }

        inner class FiltersView : VLinearLayout(context)
        {
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
                addView(createActionBar())
                // Фильтры могут содержать либо Жанры либо Секции, вместе не может быть
                if (hasGenres())
                {
                    genreCell = FilterCell().apply {
                        filterName = Locale.text(Locale.text_genre)
                        filterValue = activeGenre!!
                    }
                    addView(genreCell, LayoutHelper.createLinear(
                        LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                        0, CELL_SPACING, 0, 0
                    ))
                }
                else if (hasSections())
                {
                    sectionCell = FilterCell().apply {
                        filterName = Locale.text(Locale.text_section)
                        filterValue = activeSection!!
                    }
                    addView(genreCell, LayoutHelper.createLinear(
                        LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                        20, CELL_SPACING, 20, 0
                    ))
                }
                // Сортировка есть всегда
                sortingCell = FilterCell().apply {
                    filterName = Locale.text(Locale.text_sorting)
                    filterValue = activeSorting
                }
                addView(sortingCell, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    0, CELL_SPACING, 0, CELL_SPACING
                ))
            }

            private fun createActionBar() : ActionBar
            {
                return ActionBar(context).apply {
                    title = Locale.text(Locale.text_filters)

                    menu = ActionBar.Menu(context).apply {
                        addItem(Theme.drawable(R.drawable.refresh, Theme.color_text)) {
                            if (hasGenres()) {
                                activeGenre = genres!![0]
                            }
                            if (hasSections()) {
                                // Применяем первоначальные Секции (то есть все)
                            }
                            activeSorting = sortings[0]

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
            private var scroll: NestedScrollView
            var singleCheckGroup: SingleCheckGroup
            private lateinit var doneButton: FloatingActionButton

            init
            {
                createActionBar()
                addView(actionBar)

                singleCheckGroup = SingleCheckGroup(context).apply {
                    for (genre in genres!!)
                    {
                        addCheck(genre)
                    }
                    check(activeGenre!!)
                }
                singleCheckGroup.measure(0, 0)
                scroll = NestedScrollView(context).apply {
                    addView(singleCheckGroup)
                }
                val availableHeight = (Utils.displayHeight * 0.65).toInt() - (actionBar.measuredHeight + bottomLayout.measuredHeight)
                val scrollHeight = if (singleCheckGroup.measuredHeight < availableHeight) {
                    LayoutHelper.WRAP_CONTENT
                } else {
                    availableHeight
                }

                addView(scroll, LayoutHelper.createFrame2(
                    LayoutHelper.MATCH_PARENT, scrollHeight,
                    Gravity.START or Gravity.TOP,
                    0, actionBar.measuredHeight, 0, 0
                ))

                createDoneButton()
                addView(doneButton, LayoutHelper.createFrame(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.END or Gravity.BOTTOM,
                    0, 0, 10, 10
                ))
            }

            override fun onVisibilityChanged(changedView: View, visibility: Int)
            {
                super.onVisibilityChanged(changedView, visibility)
                // открытие жанров
                if (visibility == View.VISIBLE)
                {
                    singleCheckGroup.check( filtersView!!.genreCell!!.filterValue, false )
                    singleCheckGroup.moveCheckedOnTop()

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_genre)

                    actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                    onActionButtonClick {
                        navigate(genreView!!, filtersView!!)
                    }
                }

                actionBar.measure(0, 0)
            }

            private fun createDoneButton()
            {
                doneButton = FloatingActionButton(requireContext()).apply {
                    customSize = Utils.dp(50)

                    backgroundTintList = ColorStateList.valueOf(Theme.color(Theme.color_main))
                    rippleColor = Theme.ripplizeColor(Theme.color_main)

                    setImageDrawable(Theme.drawable(R.drawable.done_bold))
                    imageTintList = ColorStateList.valueOf(Theme.COLOR_WHITE)

                    setOnClickListener {
                        filtersView!!.genreCell!!.filterValue = singleCheckGroup.currentChecked()
                        navigate(genreView!!, filtersView!!)
                    }
                }
            }

            fun currentGenre(): String
            {
                return singleCheckGroup.currentChecked()
            }
        }

        inner class SortingView : FrameLayout(context)
        {
            private lateinit var actionBar: ActionBar
            private var scroll: NestedScrollView
            var singleCheckGroup: SingleCheckGroup
            private lateinit var doneButton: FloatingActionButton

            init
            {
                createActionBar()
                addView(actionBar)

                singleCheckGroup = SingleCheckGroup(context).apply {
                    for (sorting in sortings)
                    {
                        addCheck(sorting)
                    }
                    check(activeSorting)
                }
                singleCheckGroup.measure(0, 0)
                scroll = NestedScrollView(context).apply {
                    addView(singleCheckGroup)
                }
                val availableHeight = (Utils.displayHeight * 0.65).toInt() - (actionBar.measuredHeight + bottomLayout.measuredHeight)
                val scrollHeight = if (singleCheckGroup.measuredHeight < availableHeight) {
                    LayoutHelper.WRAP_CONTENT
                } else {
                    Utils.px( availableHeight )
                }

                addView(scroll, LayoutHelper.createFrame2(
                    LayoutHelper.MATCH_PARENT, scrollHeight,
                    Gravity.START or Gravity.TOP,
                    0, actionBar.measuredHeight, 0, 0
                ))

                createDoneButton()
                addView(doneButton, LayoutHelper.createFrame(
                    LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.END or Gravity.BOTTOM,
                    0, 0, 10, 10
                ))
            }

            override fun onVisibilityChanged(changedView: View, visibility: Int)
            {
                super.onVisibilityChanged(changedView, visibility)
                // открытие сортировки
                if (visibility == View.VISIBLE)
                {
                    // проверить на нулл !!
                    singleCheckGroup.check( filtersView!!.sortingCell.filterValue, false )
                    singleCheckGroup.moveCheckedOnTop()

                    scroll.scrollTo(0, 0)
                }
            }

            private fun createActionBar()
            {
                actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_sorting)
                }
                actionBar.measure(0, 0)

                if (filtersView != null)
                {
                    actionBar.apply {
                        actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                        onActionButtonClick {
                            navigate(sortingView, filtersView!!)
                        }
                    }
                }
            }

            private fun createDoneButton()
            {
                doneButton = FloatingActionButton(requireContext()).apply {
                    customSize = Utils.dp(50)

                    backgroundTintList = ColorStateList.valueOf(Theme.color(Theme.color_main))
                    rippleColor = Theme.ripplizeColor(Theme.color_main)

                    setImageDrawable(Theme.drawable(R.drawable.done_bold))
                    imageTintList = ColorStateList.valueOf(Theme.COLOR_WHITE)

                    setOnClickListener {
                        filtersView!!.sortingCell.filterValue = singleCheckGroup.currentChecked()
                        navigate(sortingView, filtersView!!)
                    }
                }
            }

            fun currentSorting(): String
            {
                return singleCheckGroup.currentChecked()
            }
        }

        inner class SectionView : LinearLayout(context)
        {

        }


    }

}






































//