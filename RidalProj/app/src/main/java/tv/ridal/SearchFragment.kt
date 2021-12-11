package tv.ridal

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Adapters.SearchAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.PopupFrame
import tv.ridal.Components.ActionBar.BigActionBar
import tv.ridal.Components.View.SearchView
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Parser
import tv.ridal.HDRezka.SearchResult
import tv.ridal.Utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


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

    private lateinit var rootLayout: LinearLayout
    // children
    private lateinit var bigActionBar: BigActionBar
    private lateinit var searchView: SearchView
    private lateinit var resultsFrame: FrameLayout
    // подсказки
    private lateinit var movieSuggestionsView: RecyclerView
    // список результатов поиска
    private lateinit var resultsListView: ListView
    // результаты поиска
    private var searchResults: ArrayList<SearchResult> = ArrayList()

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue

    private lateinit var resultsPopupFrame: PopupFrame


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootFrame = FrameLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)

            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        rootLayout = LinearLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }
        // rootLayout's children creation
        createBigActionBar()

        createSearchView()
        resultsFrame = FrameLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        }
        // resultsFrame's children creation
        createMovieSuggestionsView()
        resultsPopupFrame = PopupFrame(requireContext())
        // resultsPopupFrame's ListView child
        resultsListView = ListView(requireContext()).apply {
            background = Theme.createRect(Theme.color_bg)

            adapter = SearchAdapter(searchResults)
        }
        resultsPopupFrame.apply {
            addView(resultsListView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
            ))
        }

        resultsFrame.apply {
            addView(movieSuggestionsView)
        }

        rootLayout.apply {
            addView(bigActionBar)
            addView(searchView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 50,
                Gravity.CENTER,
                25, 0, 25, 10
            ))
            addView(resultsFrame)
        }

        rootFrame.addView(rootLayout)

        loadMovieSuggestions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }


    private val movies: ArrayList<Movie> = ArrayList()

    private fun createMovieSuggestionsView()
    {
        movieSuggestionsView = RecyclerView(requireContext()).apply {
            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }
            clipToPadding = false

            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(GridSpacingItemDecoration(3, Utils.dp(15)))

            adapter = MoviesAdapter(movies)
        }
    }

    private fun createBigActionBar()
    {
        bigActionBar = BigActionBar(requireContext()).apply {
            title = Locale.text(Locale.text_search)
        }
    }

    var updateResultsTimer: Timer? = null


    private fun createSearchView()
    {
        searchView = SearchView(requireContext()).apply {
            maxLength = 20

            searchListener = object : SearchView.SearchListener() {
                override fun onTextChanged(text: CharSequence) {
                    if (updateResultsTimer != null)
                    {
                        try {
                            updateResultsTimer!!.cancel()
                            updateResultsTimer!!.purge()
                            updateResultsTimer = null
                        } catch (e: IllegalStateException) {
                            println("FDF")
                        }

                        //updateResultsTimer!!.purge()
                        //updateResultsTimer = null
                    }

                    if (searchView.text == "")
                    {
                        clearResults()
                        return
                    }
                    else
                    {
                        updateResultsTimer = Timer()
                        updateResultsTimer!!.schedule(300) {
                            requireActivity().runOnUiThread {
                                loadResults()
                            }
                        }
                    }
                }

                override fun onSearch(text: CharSequence) {
                    super.onSearch(text)

                    println("SEARCH IS CLICKED")
                }

                override fun onFocusChange(focus: Boolean) {
                    super.onFocusChange(focus)

                    if (focus)
                    {
                        resultsPopupFrame.show(resultsFrame)
                    }
                    else
                    {
                        resultsPopupFrame.dismiss()
                    }
                }
            }
        }
    }


    private fun loadResults()
    {
        println(searchView.text)

        showLoading()

        val url = "https://rezka.ag/engine/ajax/search.php"

        val request = object : StringRequest(Request.Method.POST, url, object : Response.Listener<String>
        {
            override fun onResponse(response: String?)
            {
                if (response == null || response == "") return

                searchResults.clear()
                searchResults.addAll(Parser.parseSearchResults(response)!!)
                (resultsListView.adapter as SearchAdapter).notifyDataSetChanged()

                hideLoading()
            }
        }, Response.ErrorListener {
            println("ERROR")
        }
        ) {
            override fun getParams(): MutableMap<String, String>
            {
                val params = HashMap<String, String>().apply {
                    put("q", searchView.text)
                }

                return params
            }
        }

        requestQueue.add(request)
    }
    private fun clearResults()
    {
        searchResults.clear()
        (resultsListView.adapter as SearchAdapter).notifyDataSetChanged()
    }

    private fun showLoading() {
        resultsListView.visibility = View.GONE
    }
    private fun hideLoading() {
        resultsListView.visibility = View.VISIBLE
    }


    private fun loadMovieSuggestions()
    {
        val urls = listOf(
            "https://rezka.ag/films/?filter=watching",
            "https://rezka.ag/series/?filter=watching",
            "https://rezka.ag/cartoons/?filter=watching",
            "https://rezka.ag/animation/?filter=watching"
        )

        for (i in urls.indices)
        {
            val stringRequest = StringRequest(Request.Method.GET, urls[i],
                { response ->
                    movies.addAll(Parser.parseMovies(response, 5)!!)

                    (movieSuggestionsView.adapter as MoviesAdapter).notifyDataSetChanged()
                },
                {
                    println("ERROR!")
                }
            )
            requestQueue.add(stringRequest)
        }
    }

}





































//