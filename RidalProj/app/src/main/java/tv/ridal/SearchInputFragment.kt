package tv.ridal

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.hdrezka.HDRezka
import tv.ridal.hdrezka.Movie
import tv.ridal.hdrezka.Parser
import tv.ridal.hdrezka.SearchResult
import tv.ridal.ui.cell.PointerCell
import tv.ridal.ui.cell.SearchResultCell
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.msg
import tv.ridal.ui.view.ClearableInputView
import tv.ridal.ui.view.InputBar
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class SearchInputFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "SearchInputFragment"


    private lateinit var rootFrame: FrameLayout
    private lateinit var inputBar: InputBar
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: VLinearLayout

    private val requestQueue: RequestQueue = App.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return rootFrame
    }

    private fun createUI()
    {
        createInputBar()
        layout = VLinearLayout(context).apply {
            setPadding(0, Utils.dp(8), 0, Utils.dp(8))
        }

        scroll = NestedScrollView(context).apply {
            addView(layout)
        }

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color(Theme.color_bg) )

            addView(inputBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            addView(scroll, Layout.frame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.TOP,
                0, inputBar.measuredHeight, 0, 0
            ))
        }

    }

    private fun createInputBar()
    {
        inputBar = InputBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            setBackgroundColor(
                Theme.overlayColor( Theme.color_bg, 0.04F )
            )

            measure(0, 0)
        }

        inputBar.apply {
            onBack {
                finish()
            }

            onTextChange {
                layout.removeAllViews()
                loadSearchResults(it)
            }

            onTextClear {
                layout.removeAllViews()
            }
        }
    }


    private fun loadSearchResults(searchText: String)
    {
        val url = "https://rezka.ag/engine/ajax/search.php"
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                val results = Parser.parseSearchResults(response)

                results?.let {
                    showSearchResults(it, searchText)
                }
            },
            {
                println("ERROR!")
            }
        ) {
            override fun getParams(): MutableMap<String, String>
            {
                return HashMap<String, String>().apply {
                    put("q", searchText)
                }
            }
        }

        requestQueue.add(request)
    }

    private fun showSearchResults(results: ArrayList<SearchResult>, searchText: String)
    {
        for (i in results.indices)
        {
            val r = results[i]

            val searchResultCell = SearchResultCell(context).apply {
                movieName = r.movieName
                movieData = r.movieData
                movieRating = r.movieRating

                setOnClickListener { _ ->
                    openMovie(r)
                }
            }
            layout.addView(searchResultCell, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        val divider = View(context).apply {
            setBackgroundColor( Theme.overlayColor(Theme.color_bg, 0.07F) )
        }
        layout.addView(divider, Layout.ezLinear(
            Layout.MATCH_PARENT, 1,
            0, 4, 0, 4
        ))

        val allResultsCell = PointerCell(context).apply {
            text = Locale.string(R.string.viewAllResults)

            setOnClickListener {
                openMovies(searchText)
            }
        }
        layout.addView(allResultsCell)
    }

    private fun openMovie(searchResult: SearchResult)
    {
        val movie = Movie(
            searchResult.movieName,
            "",
            Movie.Type(HDRezka.FILM, false),
            searchResult.movieUrl
        )
        startFragment(
            MovieFragment.instance(movie)
        )
    }

    private fun openMovies(searchText: String)
    {
        val args = MoviesFragment.Arguments().apply {
            url = "https://rezka.ag/search/?do=search&subaction=search&q=${searchText}"
            title = searchText
            filters = HDRezka.Filters.NO_FILTERS
        }
        startFragment(
            MoviesFragment.newInstance(args)
        )
    }

}


































//