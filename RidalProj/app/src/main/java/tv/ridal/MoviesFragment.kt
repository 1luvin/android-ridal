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
import android.widget.EdgeEffect
import android.widget.FrameLayout
import android.widget.LinearLayout
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
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.Popup.BottomPopup
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
        var title: String? = null
        var url: String? = null
    }

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
//                filtersBottomPopupFragment.show(
//                    ApplicationActivity.instance().supportFragmentManager,
//                    "tag"
//                )
                FiltersPopup(requireContext()).show()
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

    class FiltersPopup(context: Context) : BottomPopup(context)
    {
        private var popupView: FrameLayout

        private var filtersView: LinearLayout
        private var genreView: LinearLayout

        init
        {

            filtersView = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL

                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                    Gravity.TOP
                )

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(createActionBar(Locale.text(Locale.text_filters)))
                addView(createActionBar(Locale.text(Locale.text_filters)))
            }

            genreView = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL

                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                    Gravity.TOP
                )

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(createActionBar(Locale.text(Locale.text_genre)))
                addView(createActionBar(Locale.text(Locale.text_genre)))
                addView(createActionBar(Locale.text(Locale.text_genre)))
                addView(createActionBar(Locale.text(Locale.text_genre)))
                addView(createActionBar(Locale.text(Locale.text_genre)))
                addView(createActionBar(Locale.text(Locale.text_genre)))
            }

            popupView = FrameLayout(context).apply {
                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
                )

                setBackgroundColor(Theme.color(Theme.color_bg))

                addView(filtersView)
                addView(genreView.apply {
                    visibility = View.GONE
                })
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
            val startHeight = filtersView.measuredHeight
            println(startHeight)
            genreView.measure(0, 0)
            val endHeight = genreView.measuredHeight
            println(endHeight)

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {

                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = it.animatedValue as Int
                    }
                }
            }

            AnimatorSet().apply {
                duration = 320L
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
            val startHeight = genreView.measuredHeight
            filtersView.measure(0, 0)
            val endHeight = filtersView.measuredHeight

            val heightAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                addUpdateListener {

                    popupView.updateLayoutParams<FrameLayout.LayoutParams> {
                        height = it.animatedValue as Int
                    }
                }
            }

            AnimatorSet().apply {
                duration = 320L
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

                setOnClickListener {
                    if (title == "Фильтры") showGenreView()
                    else showFiltersView()
                }
            }
        }

    }

}






































//