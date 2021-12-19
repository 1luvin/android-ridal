package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
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
import tv.ridal.Cells.PointerCell
import tv.ridal.Cells.RadioCell
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.Popup.BottomPopup
import tv.ridal.Components.View.NestedScrollView
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Navigator
import tv.ridal.HDRezka.Parser
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

        lateinit var filters: List<Filter>

        var applyGenre: String? = null
    }

    enum class Filter
    {
        GENRE, SORTING, SECTION
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

    private var activeGenre: String = Locale.text(Locale.text_allGenres)
    private var activeSorting: String = Locale.text(Locale.sorting_last)

    private var document: Document? = null

    private lateinit var rootFrame: FrameLayout
    private lateinit var actionBar: ActionBar
    private lateinit var moviesFrame: FrameLayout
    private lateinit var moviesView: RecyclerView
    private lateinit var filtersButton: FloatingActionButton

    private val movies: ArrayList<Movie> = ArrayList()

    private var loading: Boolean = false

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUi()

        if (arguments.applyGenre != null) {
            activeGenre = arguments.applyGenre!!
        }
        setSubtitle(activeGenre, activeSorting)



        loadMovies()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
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
                FiltersPopup().show()
            }
        }
    }

    private fun loadMovies()
    {
        loading = true

        var url: String = ""
        if (document == null) {
            url = arguments.url!!
        } else {
            if (Navigator.isNextPageExist(document!!)) {
                url = Navigator.nextPageUrl(document!!)
            } else {
                loading = false
                return
            }
        }

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                loading = false

                document = Jsoup.parse(response)

                movies.addAll(Parser.parseMovies(document!!)!!)

                (moviesView.adapter as MoviesAdapter).notifyItemRangeInserted(movies.size, movies.size + 36)
            },
            {
                println("ERROR!")
            }
        )
        requestQueue.add(stringRequest)
    }

    class FiltersPopup() : BottomPopup(ApplicationActivity.instance())
    {
        private var popupView: FrameLayout

        private var filtersView: LinearLayout
        private var genreView: FrameLayout

        init
        {
            filtersView = FiltersView().apply {
                genreCell.setOnClickListener {
                    showGenreView()
                }
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL

                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
                addView(RadioCell().apply { text = "Ахахахаха, типа работает" })
            }
            layout.measure(0, 0)
            println("MSG HEIGHT " + layout.measuredHeight)

            val scroll = NestedScrollView(context).apply {
                addView(layout)
            }

            genreView = FrameLayout(context).apply {
                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(createActionBar(Locale.text(Locale.text_genre)))

                val availableHeight = (Utils.displayHeight * 0.5).toInt() - Utils.dp(56 + 15 + 50 + 15)
                val scrollHeight = if (layout.measuredHeight < availableHeight) {
                    LayoutHelper.WRAP_CONTENT
                } else {
                    Utils.px( availableHeight )
                }

                addView(scroll, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, scrollHeight,
                    Gravity.TOP,
                    0, 56, 0, 0
                ))
            }

            popupView = FrameLayout(context).apply {
                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
                )

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(filtersView, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, 15 + 50 + 15
                ))

                genreView.visibility = View.GONE
                addView(genreView, LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP,
                    0, 0, 0, 15 + 50 + 15
                ))

                addView(createShowResultsButton(), LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, 50,
                    Gravity.BOTTOM,
                    20, 15, 20, 15
                ))
            }

            setContentView(popupView)
        }

        private fun showGenreView()
        {
            val alphaAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                addUpdateListener {
                    val animatedAlpha = it.animatedValue as Float
                    genreView.alpha = animatedAlpha
                    filtersView.alpha = 1F - animatedAlpha
                }
            }

            filtersView.measure(0, 0)
            val startHeight = filtersView.measuredHeight + Utils.dp(15 + 50 + 15)
            println(startHeight)
            genreView.measure(0, 0)
            val endHeight = genreView.measuredHeight + Utils.dp(15 + 50 + 15)
            println(endHeight)

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {

                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = it.animatedValue as Int
                    }
                }
            }

            AnimatorSet().apply {
                duration = 280L
                interpolator = DecelerateInterpolator(1.1F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = filtersView.measuredHeight
                        }

                        genreView.alpha = 0F
                        genreView.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = LayoutHelper.WRAP_CONTENT
                        }

                        filtersView.visibility = View.GONE
                    }
                })

                playTogether(alphaAnimator, heightAnimator)

                start()
            }
        }

        private fun showFiltersView()
        {
            val alphaAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                addUpdateListener {
                    val animatedAlpha = it.animatedValue as Float
                    filtersView.alpha = animatedAlpha
                    genreView.alpha = 1F - animatedAlpha
                }
            }

            genreView.measure(0, 0)
            val startHeight = genreView.measuredHeight + Utils.dp(15 + 50 + 15)
            filtersView.measure(0, 0)
            val endHeight = filtersView.measuredHeight + Utils.dp(15 + 50 + 15)

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {

                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = it.animatedValue as Int
                    }
                }
            }

            AnimatorSet().apply {
                duration = 280L
                interpolator = DecelerateInterpolator(1.1F)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = genreView.measuredHeight
                        }

                        filtersView.alpha = 0F
                        filtersView.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                            height = LayoutHelper.WRAP_CONTENT
                        }

                        genreView.visibility = View.GONE
                    }
                })

                playTogether(alphaAnimator, heightAnimator)

                start()
            }
        }

        private fun createActionBar(title: String) : ActionBar
        {
            return ActionBar(context).apply {
                this.title = title

                actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                onActionButtonClick {
                    showFiltersView()
                }

                setOnClickListener {
                    if (title == "Фильтры") showGenreView()
                    else showFiltersView()
                }
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
                    showResultsListener?.invoke()
                }
            }
        }

        private var showResultsListener: (() -> Unit)? = null
        fun onShowResults(l: () -> Unit)
        {
            showResultsListener = l
        }


        class FiltersView : LinearLayout(ApplicationActivity.instance())
        {

            var genreCell: FilterCell
            var sortingCell: FilterCell

            init
            {
                orientation = LinearLayout.VERTICAL

                genreCell = FilterCell().apply {
                    filterName = Locale.text(Locale.text_genre)
                    filterValue = Locale.text(Locale.text_allGenres)
                }

                sortingCell = FilterCell().apply {
                    filterName = Locale.text(Locale.text_sorting)
                    filterValue = Locale.text(Locale.sorting_last)
                }

                addView(createActionBar())
                addView(genreCell, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    20, 15, 20, 0
                ))
                addView(sortingCell, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    20, 15, 20, 10
                ))
            }

            private fun createActionBar() : ActionBar
            {
                return ActionBar(context).apply {
                    title = Locale.text(Locale.text_filters)
                }
            }
        }

    }

}






































//