package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.ActionBar.ActionBar
import tv.ridal.Application.Locale
import tv.ridal.Cells.FilterCell
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.Popup.BottomPopup
import tv.ridal.Components.RadioGroup
import tv.ridal.Components.View.NestedScrollView
import tv.ridal.HDRezka.*
import tv.ridal.Utils.Utils

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

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue
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
        rootFrame = FrameLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )

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
            0, 56 + 25, 0, 0
        ))

        createMoviesView()
        moviesFrame.addView(moviesView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))

        createFiltersButton()
        moviesFrame.addView(filtersButton, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.END or Gravity.BOTTOM,
            0, 0, 10, 10
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

            adapter = MoviesAdapter(movies, true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    println(recyclerView.computeVerticalScrollOffset())

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
        if (document == null) {
            url = arguments.url!!
            if ( hasGenres() ) {
                println(activeGenre)
                url += Genre.url(activeGenre!!)
            }
            url += Sorting.url(activeSorting)

            println(url)
        } else {
            if (Navigator.isNextPageExist(document!!)) {
                url = Navigator.nextPageUrl(document!!)
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

                document = Jsoup.parse(response)

                val newMovies = Parser.parseMovies(document!!)
                if (newMovies == null) return@StringRequest

                println(newMovies.size)

                movies.addAll(newMovies)

                (moviesView.adapter as MoviesAdapter).notifyItemRangeInserted(movies.size, movies.size + newMovies.size)
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

    inner class FiltersPopup() : BottomPopup(ApplicationActivity.instance())
    {
        private lateinit var popupView: FrameLayout

        private var filtersView: FiltersView? = null
        private var genreView: GenreView? = null
        private lateinit var sortingView: SortingView
        private var sectionView: SectionView? = null

        private lateinit var currentView: View

        init
        {
            this.createUi()

            this.setOnShowListener {
                onFiltersOpen()
            }

            this.isDraggable = false
        }

        private fun createUi()
        {
            // создание контейнера для View с фильтрами
            popupView = FrameLayout(context).apply {
                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
                )

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))
            }
            // добавление кнопки <Показать результаты>
            popupView.addView(createShowResultsButton(), LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 50,
                Gravity.BOTTOM,
                20, 15, 20, 15
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
                popupView.addView(filtersView, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, 15 + 50 + 15
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
                popupView.addView(genreView, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, 15 + 50 + 15
                ))
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
                popupView.addView(genreView, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, 15 + 50 + 15
                ))
            }

            sortingView = SortingView().apply {
                if (filtersView != null) {
                    visibility = View.GONE
                } else {
                    currentView = this
                }
            }
            popupView.addView(sortingView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, 0, 0, 15 + 50 + 15
            ))

            setContentView(popupView)
        }

        private fun navigate(fromView: View, toView: View, animated: Boolean = true)
        {
            currentView = toView

            val alphaAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                addUpdateListener {
                    val animatedAlpha = it.animatedValue as Float
                    toView.alpha = animatedAlpha
                    fromView.alpha = 1F - animatedAlpha
                }
            }

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

                popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                    height = endHeight + Utils.dp(15 + 50 + 15)
                }

                return
            }

            fromView.measure(0 ,0)
            val startHeight = fromView.measuredHeight

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {
                    val animatedHeight = it.animatedValue as Int
                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = animatedHeight + Utils.dp(15 + 50 + 15)
                    }
                }
            }

            AnimatorSet().apply {
                duration = 300L
                interpolator = DecelerateInterpolator(1.1F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = fromView.measuredHeight + Utils.dp(15 + 50 + 15)
                        }

                        toView.apply {
                            alpha = 0F
                            visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = LayoutHelper.WRAP_CONTENT
                        }

                        fromView.apply {
                            visibility = View.GONE
                        }
                    }
                })

                playTogether(alphaAnimator, heightAnimator)

                start()
            }
        }

        private fun createShowResultsButton() : TextView
        {
            return TextView(context).apply {
                gravity = Gravity.CENTER

                background = Theme.createRectSelector(
                    Theme.color_main,
                    FloatArray(4).apply {
                        fill(Utils.dp(7F))
                    },
                    true
                )

                this.text = Locale.text(Locale.text_showResults)

                textSize = 16F
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

                    genreView!!.radioGroup.check(activeGenre!!)
                }
                if (hasSections()) {
                    filtersView!!.sectionCell!!.filterValue = activeSection!!

                    // sectionView!!.checkBoxGroup.check("xxx", "xxx")
                }

                filtersView!!.sortingCell.filterValue = activeSorting
            }

            sortingView.radioGroup.check(activeSorting)

        }

        private var newFiltersListener: (() -> Unit)? = null
        fun onNewFilters(l: () -> Unit) {
            newFiltersListener = l
        }

        inner class FiltersView : LinearLayout(ApplicationActivity.instance())
        {

            var genreCell: FilterCell? = null
            lateinit var sortingCell: FilterCell
            var sectionCell: FilterCell? = null

            init
            {
                orientation = LinearLayout.VERTICAL

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
                        20, 15, 20, 0
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
                        20, 15, 20, 0
                    ))
                }
                // Сортировка есть всегда
                sortingCell = FilterCell().apply {
                    filterName = Locale.text(Locale.text_sorting)
                    filterValue = activeSorting
                }
                addView(sortingCell, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    20, 15, 20, 10
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

        inner class GenreView : LinearLayout(ApplicationActivity.instance())
        {
            var radioGroup: RadioGroup

            init
            {
                orientation = LinearLayout.VERTICAL

                addView(createActionBar())

                radioGroup = RadioGroup().apply {
                    for (genre in genres!!)
                    {
                        addRadio(genre)
                    }
                    check(activeGenre!!)
                }
                radioGroup.measure(0, 0)
                val scroll = NestedScrollView(context).apply {
                    addView(radioGroup)
                }

                val availableHeight = (Utils.displayHeight * 0.7).toInt() - Utils.dp(56 + 15 + 50 + 15)
                val scrollHeight = if (radioGroup.measuredHeight < availableHeight) {
                    LayoutHelper.WRAP_CONTENT
                } else {
                    Utils.px( availableHeight )
                }

                addView(scroll, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, scrollHeight
                ))
            }

            private fun createActionBar() : ActionBar
            {
                return ActionBar(context).apply {
                    title = Locale.text(Locale.text_genre)

                    actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                    onActionButtonClick {
                        filtersView!!.genreCell!!.filterValue = radioGroup.currentChecked()
                        navigate(genreView!!, filtersView!!)
                    }
                }
            }

            fun currentGenre(): String {
                return radioGroup.currentChecked()
            }
        }

        inner class SortingView : LinearLayout(ApplicationActivity.instance())
        {
            var radioGroup: RadioGroup

            init
            {
                orientation = LinearLayout.VERTICAL

                addView(createActionBar())

                radioGroup = RadioGroup().apply {
                    for (sorting in sortings)
                    {
                        addRadio(sorting)
                    }
                    check(activeSorting)
                }
                radioGroup.measure(0, 0)

                val scroll = NestedScrollView(context).apply {
                    addView(radioGroup)
                }

                val availableHeight = (Utils.displayHeight * 0.7).toInt() - Utils.dp(56 + 15 + 50 + 15)
                val scrollHeight = if (radioGroup.measuredHeight < availableHeight) {
                    LayoutHelper.WRAP_CONTENT
                } else {
                    Utils.px( availableHeight )
                }

                addView(scroll, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, scrollHeight
                ))
            }

            private fun createActionBar() : ActionBar
            {
                val actionBar = ActionBar(context).apply {
                    title = Locale.text(Locale.text_sorting)
                }

                if (filtersView != null)
                {
                    actionBar.apply {
                        actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                        onActionButtonClick {
                            filtersView!!.sortingCell.filterValue = radioGroup.currentChecked()
                            navigate(sortingView, filtersView!!)
                        }
                    }
                }

                return actionBar
            }

            fun currentSorting(): String {
                return radioGroup.currentChecked()
            }
        }

        inner class SectionView : LinearLayout(ApplicationActivity.instance())
        {

        }


    }

}






































//