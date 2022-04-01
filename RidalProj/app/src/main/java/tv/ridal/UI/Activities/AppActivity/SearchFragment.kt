package tv.ridal.UI.Activities.AppActivity

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.UI.Adapters.MoviesAdapter
import tv.ridal.Application.App
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.UI.GridSpacingItemDecoration
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.UI.ActionBar.BigActionBar
import tv.ridal.UI.View.SearchView
import tv.ridal.HDRezka.HDRezka
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Parser
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.Application.Utils
import kotlin.collections.ArrayList


class SearchFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "SearchFragment"

    companion object
    {
        fun instance(): SearchFragment {
            return SearchFragment()
        }
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var scroll: ScrollView
    private lateinit var rootLayout: LinearLayout
    private lateinit var actionBar: BigActionBar
    private lateinit var searchView: SearchView
    // подсказки
    private lateinit var movieSuggestionsView: RecyclerView

    private val requestQueue: RequestQueue = App.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootFrame = FrameLayout(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)
            clipToPadding = true
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        createScroll()
        rootLayout = VLinearLayout(context)

        createActionBar()
        createSearchView()
        createMovieSuggestionsView()

        rootLayout.apply {
            addView(actionBar)
            addView(searchView, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                20, 0, 20, 0
            ))
            addView(movieSuggestionsView, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))
        }

        scroll.addView(rootLayout)
        rootFrame.addView(scroll, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))

        loadMovieSuggestions()
    }

    override fun onStop()
    {
        super.onStop()

        println(scroll.scrollY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    private val movies: ArrayList<Movie> = ArrayList()


    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            title = Locale.text(Locale.text_search)
        }
    }

    private fun createScroll()
    {
        scroll = ScrollView(context).apply {

//            addOnStickyScrollViewListener { x, y, oldX, oldY ->
//                var startV = 0F
//                var endV = 0F
//
//                val limit = Utils.dp(90)
//                if (limit in oldY until y)
//                {
//                    startV = 1F
//                    endV = 0F
//                }
//                else if (limit in y until oldY)
//                {
//                    startV = 0F
//                    endV = 1F
//                }
//
//                if (startV != 0F || endV != 0F)
//                {
//                    ValueAnimator.ofFloat(startV, endV).apply {
//                        duration = 100
//
//                        addUpdateListener {
//                            val animValue = it.animatedValue as Float
//                            val animMargin = (Utils.dp(20) * animValue).toInt()
//
//                            searchView.apply {
//                                updateLayoutParams<LinearLayout.LayoutParams> {
//                                    setMargins(animMargin, 0, animMargin, 0)
//                                }
//
//                                background = Theme.rect(
//                                    Theme.lightenColor(Theme.color_bg, 0.04F),
//                                    radii = FloatArray(4).apply {
//                                        fill( Utils.dp(7F) * animValue )
//                                    }
//                                )
//                            }
//                        }
//
//                        start()
//                    }
//                }
//            }

//            isVerticalScrollBarEnabled = false
        }
    }

    private fun createSearchView()
    {
        searchView = SearchView(context).apply {
            background = Theme.rect(
                Theme.lightenColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill( Utils.dp(7F) )
                }
            )

            tag = "sticky"
        }
    }

    private fun createMovieSuggestionsView()
    {
        movieSuggestionsView = RecyclerView(context).apply {
            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }

            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration( GridSpacingItemDecoration(3, Utils.dp(15)) )

            adapter = MoviesAdapter(movies)
        }
    }


    private fun loadMovieSuggestions()
    {
        val url = HDRezka.createUrl(sorting = HDRezka.sorting_watching)
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                movies.addAll( Parser.parseMovies(response)!! )

                (movieSuggestionsView.adapter as MoviesAdapter).notifyItemRangeInserted(
                    movies.size, HDRezka.PAGE_CAPACITY
                )
            },
            {
                println("ERROR!")
            }
        )
        requestQueue.add(request)
    }

}





































//