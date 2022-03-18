package tv.ridal

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.github.nitrico.stickyscrollview.StickyScrollView
import tv.ridal.UI.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
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
import tv.ridal.Utils.Utils
import kotlin.collections.ArrayList


class SearchFragment : BaseFragment()
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

    private lateinit var scroll: StickyScrollView

    private lateinit var rootLayout: LinearLayout
    // children
    private lateinit var actionBar: BigActionBar
    private lateinit var searchView: SearchView
    // подсказки
    private lateinit var movieSuggestionsView: RecyclerView

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootFrame = FrameLayout(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)
            clipToPadding = true
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        rootLayout = VLinearLayout(context)
        // rootLayout's children creation
        createActionBar()
        createSearchView()
        createMovieSuggestionsView()

        rootLayout.apply {
            addView(actionBar)
            addView(searchView, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, 50
            ))
            addView(movieSuggestionsView, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))
        }

        scroll = StickyScrollView(context).apply {
            this.addOnStickyScrollViewListener(object : StickyScrollView.OnStickyScrollViewListener {
                override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
                    val limit = Utils.dp(90)
                    if (limit in oldY until y)
                    {
                        searchView.background = Theme.rect(Theme.color_main)
                    }
                    else if (limit in y until oldY)
                    {
                        searchView.background = Theme.rect(Theme.color_bg)
                    }
                }
            })
            isVerticalScrollBarEnabled = false
            addView(rootLayout)
        }

        rootFrame.addView(scroll)

        loadMovieSuggestions()
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

    private fun createSearchView()
    {
        searchView = SearchView(context).apply {
            setPadding(Utils.dp(20), 0, Utils.dp(20), 0)

            background = Theme.rect(
                Theme.color_bg
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