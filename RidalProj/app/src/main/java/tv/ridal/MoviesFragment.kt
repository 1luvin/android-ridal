package tv.ridal

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.EdgeEffect
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.ActionBar.ActionBar
import tv.ridal.Application.Locale
import tv.ridal.Components.BottomSheetSharedTransition
import tv.ridal.Components.GridSpacingItemDecoration
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.Popup.BottomPopup
import tv.ridal.HDRezka.Movie
import tv.ridal.HDRezka.Navigator
import tv.ridal.HDRezka.Parser
import tv.ridal.Utils.Utils
import tv.ridal.Utils.withFilters

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
    private lateinit var filtersBottomPopupFragment: FiltersBottomPopupFragment


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


        filtersBottomPopupFragment = FiltersBottomPopupFragment()


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
                filtersBottomPopupFragment.show(
                    ApplicationActivity.instance().supportFragmentManager,
                    "tag"
                )
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

    class FiltersBottomPopupFragment : BottomSheetDialogFragment()
    {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomPopup)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            childFragmentManager
                .beginTransaction()
                .add(
                    R.id.super_container, FiltersFragment()
                )
                .addToBackStack("rootygfgfgfgf")
                .commit()
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
        {
            return BottomPopup.Builder(requireContext()).build()
        }

        private fun transitToFragment(newFragment: Fragment) {
            val currentFragmentRoot = childFragmentManager.fragments[0].requireView()
            childFragmentManager
                .beginTransaction()
                .apply {
                    addSharedElement(currentFragmentRoot, currentFragmentRoot.transitionName)
                    setReorderingAllowed(true)

                    newFragment.sharedElementEnterTransition = BottomSheetSharedTransition()
                }
                .replace(R.id.super_container, newFragment)
                .addToBackStack(newFragment.javaClass.name)
                .commit()
        }

        fun goToGenre() {
            transitToFragment(GenreFragment())
        }

        fun goBack()
        {
            childFragmentManager.popBackStack()
        }
    }

    class FiltersFragment : Fragment()
    {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        {
            return LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())

                transitionName = "Anya"
            }
        }

        private fun createActionBar() : ActionBar
        {
            return ActionBar(requireContext()).apply {
                title = Locale.text(Locale.text_filters)

                setOnClickListener {
                    withFilters {
                        goToGenre()
                    }
                }
            }
        }
    }

    class GenreFragment : Fragment()
    {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        {

            return LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL

                background = Theme.createRect(
                    Theme.color_bg, floatArrayOf(
                        Utils.dp(12F), Utils.dp(12F), 0F, 0F
                    ))

                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())
                addView(createActionBar())

                transitionName = "Anya"
            }
        }

        private fun createActionBar() : ActionBar
        {
            return ActionBar(requireContext()).apply {
                title = Locale.text(Locale.text_genre)

                actionButtonIcon = Theme.drawable(R.drawable.back, Theme.color_actionBar_back)
                onActionButtonClick {
                    withFilters {
                        goBack()
                    }
                }
            }
        }
    }

}






































//