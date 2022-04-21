package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isEmpty
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
import tv.ridal.ui.setBackgroundColor
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
    private lateinit var frame: FrameLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: VLinearLayout

    private val requestQueue: RequestQueue = App.instance().requestQueue
    private val searchRequestTag: String = "searchRequestTag"


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return rootFrame
    }

    override fun onResume()
    {
        super.onResume()

        inputBar.showKeyboard()
    }

    override fun onPause()
    {
        super.onPause()

        inputBar.hideKeyboard()
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

        frame = FrameLayout(context).apply {
            addView(scroll)
        }

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )

            addView(inputBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            addView(frame, Layout.frame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
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

            onStopTyping {
                loadSearchResults(it)
            }

            onTextChange {
                requestQueue.cancelAll(searchRequestTag)
            }

            onTextClear {
                if (layout.isEmpty()) return@onTextClear

                ValueAnimator.ofFloat(1F, 0F).apply {
                    duration = 70

                    addUpdateListener {
                        val value = it.animatedValue as Float

                        layout.apply {
                            alpha = value
                            scaleX = 0.99F + 0.01F * value
                            scaleY = 0.99F + 0.01F * value
                        }
                    }

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?)
                        {
                            super.onAnimationEnd(animation)

                            layout.removeAllViews()
                        }
                    })

                    start()
                }
            }
        }
    }


    private fun loadSearchResults(searchText: String)
    {
        requestQueue.cancelAll(searchRequestTag)

        val url = "https://rezka.ag/engine/ajax/search.php"
        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                val results = Parser.parseSearchResults(response)

                results?.let {
                    showSearchResults(results = it.first, hasMore = it.second, searchText)
                }
            },
            {
                println("ERROR!")
            }
        )
        {
            override fun getParams(): MutableMap<String, String>
            {
                return HashMap<String, String>().apply {
                    put("q", searchText)
                }
            }
        }.apply {
            tag = searchRequestTag
        }

        requestQueue.add(request)
    }

    private fun showSearchResults(results: ArrayList<SearchResult>, hasMore: Boolean, searchText: String)
    {
        val views = ArrayList<View>()
        for (i in results.indices)
        {
            val r = results[i]

            val needDivider = hasMore || (i != results.lastIndex)
            val searchResultCell = SearchResultCell(context, needDivider).apply {
                movieName = r.movieName
                movieData = r.movieData
                movieRating = r.movieRating

                setOnClickListener { _ ->
                    openMovie(r)
                }
            }
            views.add(searchResultCell)
        }

        if (hasMore)
        {
            val allResultsCell = PointerCell(context).apply {
                text = Locale.string(R.string.viewAllResults)
                textColor = Theme.mainColor

                setOnClickListener {
                    openMovies(searchText)
                }
            }
            views.add(allResultsCell)
        }

        if ( layout.isEmpty() )
        {
            ValueAnimator.ofFloat(0F, 1F).apply {
                duration = 70

                addUpdateListener {
                    val value = it.animatedValue as Float

                    layout.apply {
                        alpha = value
                        scaleX = 0.99F + 0.01F * value
                        scaleY = 0.99F + 0.01F * value
                    }
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?)
                    {
                        super.onAnimationStart(animation)

                        layout.apply {
                            alpha = 0F
                        }
                    }

                    override fun onAnimationEnd(animation: Animator?)
                    {
                        super.onAnimationEnd(animation)

                        layout.removeAllViews()
                        views.forEach {
                            layout.addView(it)
                        }

                        setFloatValues(0F, 1F)
                        start()

                        removeListener(this)
                    }
                })

                start()
            }
        }
        else
        {
            ValueAnimator.ofFloat(1F, 0F).apply {
                duration = 70

                addUpdateListener {
                    val value = it.animatedValue as Float

                    layout.apply {
                        alpha = value
                        scaleX = 0.99F + 0.01F * value
                        scaleY = 0.99F + 0.01F * value
                    }
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?)
                    {
                        super.onAnimationEnd(animation)

                        layout.removeAllViews()
                        views.forEach {
                            layout.addView(it)
                        }

                        setFloatValues(0F, 1F)
                        start()

                        removeListener(this)
                    }
                })

                start()
            }
        }

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