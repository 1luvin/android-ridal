package tv.ridal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.EdgeEffect
import android.widget.FrameLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.tunjid.androidx.navigation.Navigator
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.Components.ActionBar.ActionBar
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Parser
import tv.ridal.Utils.Utils

class MoviesFragment : BaseFragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "MoviesFragment${View.generateViewId()}"

    companion object
    {
        fun newInstance(): MoviesFragment
        {
            return MoviesFragment()
        }
    }

    private lateinit var rootFrame: FrameLayout

    private lateinit var actionBar: ActionBar

    private lateinit var moviesView: RecyclerView
    private val movies: ArrayList<Movie> = ArrayList()

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
            title = "Сука"
        }

        rootFrame.addView(actionBar, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            0, -25, 0, 0
        ))

        createMoviesView()
        rootFrame.addView(moviesView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
            Gravity.START or Gravity.TOP,
            0, 56 + 25, 0, 0
        ))

        actionBar.bringToFront()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
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

            adapter = MoviesAdapter(movies)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // скроллим вниз
                    if (dy > 0)
                    {
                        if (actionBar.y > - actionBar.height)
                        {
                            val curr = actionBar.y - dy + 0F
                            if (curr >= - actionBar.height) {
                                actionBar.y -= dy + 0F
                            } else {
                                actionBar.y = - actionBar.height + 0F
                            }
                        }
                    }
                    else // скроллим вверх
                    {
                        if (actionBar.y < 0)
                        {
                            val curr = actionBar.y - dy
                            if (curr <= 0) {
                                actionBar.y += -dy
                            } else {
                                actionBar.y = 0F
                            }
                        }
                    }

//                    actionBar.layoutParams = LayoutHelper.createFrame(
//                        LayoutHelper.MATCH_PARENT, 56 + 25,
//                        Gravity.NO_GRAVITY,
//                        0, actionBar.y.toInt(), 0, 0
//                    )
////
//                    moviesView.layoutParams = LayoutHelper.createFrame(
//                        LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
//                        Gravity.START or Gravity.TOP,
//                        0, Utils.px(actionBar.y.toInt() + actionBar.height), 0, 0
//                    )
                }
            })
        }
    }

    private fun loadMovies()
    {
        val urls = listOf(
            "https://rezka.ag/films/?filter=watching",
        )

        for (i in urls.indices)
        {
            val stringRequest = StringRequest(
                Request.Method.GET, urls[i],
                { response ->
                    movies.addAll(Parser.parseMovies(response)!!)

                    (moviesView.adapter as MoviesAdapter).notifyDataSetChanged()
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