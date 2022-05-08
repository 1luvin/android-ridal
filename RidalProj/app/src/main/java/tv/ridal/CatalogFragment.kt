package tv.ridal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.adapter.MoviesAdapter
import tv.ridal.hdrezka.*
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.ui.actionbar.BigActionBar
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.util.Utils
import tv.ridal.ui.*
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.recyclerview.EdgeColorEffect
import tv.ridal.ui.recyclerview.SpacingItemDecoration
import tv.ridal.ui.view.RTextView

class CatalogFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "CatalogFragment"


    private lateinit var rootLayout: FrameLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: LinearLayout
    private lateinit var actionBar: BigActionBar
    private val sectionViews: ArrayList<SectionView> = ArrayList()

    private val sections: List<HDRezka.MovieSection> = HDRezka.movieSections

    private val requestQueue: RequestQueue = App.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()

        loadSections()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }


    private fun createUI()
    {
        createActionBar()

        layout = VLinearLayout( requireContext() ).apply {
            addView(actionBar)
        }

        for (i in sections.indices)
        {
            val view = SectionView( requireContext() )
            sectionViews.add(view)
            layout.addView(view)
        }

        scroll = NestedScrollView( requireContext() ).apply {
            isVerticalScrollBarEnabled = false

            addView(layout)
        }

        rootLayout = FrameLayout( requireContext() ).apply {
            setBackgroundColor( Theme.color_bg )

            addView(scroll)
        }
    }

    private fun createActionBar()
    {
        val menu = BigActionBar.Menu(context).apply {
            addItem(Theme.drawable(R.drawable.invite, Theme.color_actionBar_menuItem)) {
                showSharePopup()
            }
            addItem(Theme.drawable(R.drawable.sett, Theme.color_actionBar_menuItem)) {
                context.startActivity(
                    Intent(context, SettingsActivity::class.java)
                )
            }
        }

        actionBar = BigActionBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            title = Locale.string(R.string.catalog)

            this.menu = menu
        }
    }

    private fun showSharePopup()
    {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, Locale.string(R.string.app_share))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "")
        startActivity(shareIntent)
    }

    private fun loadSections()
    {
        for (i in sections.indices)
        {
            val section = sections[i]
            val url = section.url + Sorting.url( Locale.string(R.string.sorting_watching) )
            val request = StringRequest(Request.Method.GET, url,
                { response ->
                    val sectionView = sectionViews[i]

                    sectionView.apply {
                        sectionName = section.name
                        sectionSubtext = Parser.parseSectionMoviesSize(response)

                        setMovies( Parser.parseMovies(response, 10)!! )

                        onOpen {
                            startMoviesFragment( section )
                        }

                        onMovieClick {
                            startFragment( MovieFragment.newInstance(it) )
                        }
                    }
                },
                {
                    println("ERROR!")
                }
            )

            requestQueue.add(request)
        }
    }

    private fun startMoviesFragment(section: HDRezka.MovieSection)
    {
        val args = MoviesFragment.Arguments().apply {
            title = section.name
            url = section.url
            filters = Filters.GENRE_SORTING
        }

        startFragment( MoviesFragment.newInstance(args) )
    }


    inner class SectionView(context: Context) : VLinearLayout(context)
    {
        private lateinit var headerCell: SectionCell
        private lateinit var moviesView: RecyclerView
        private lateinit var moviesAdapter: RecyclerView.Adapter<MoviesAdapter.ViewHolder>

        private var onOpen: (() -> Unit)? = null
        fun onOpen(l: () -> Unit)
        {
            onOpen = l
        }

        private var onMovieClick: ((Movie) -> Unit)? = null
        fun onMovieClick(l: (Movie) -> Unit)
        {
            onMovieClick = l
        }

        var sectionName: String = ""
            set(value) {
                field = value

                headerCell.sectionName = sectionName
            }
        var sectionSubtext: String = ""
            set(value) {

                field = "$value+"

                headerCell.sectionSubtext = sectionSubtext
            }


        private val movies: ArrayList<Movie> = ArrayList()
        fun setMovies(movies: ArrayList<Movie>)
        {
            this.movies.addAll(movies)
            moviesAdapter.notifyDataSetChanged()
        }

        init
        {
            setPadding(0, 0, 0, Utils.dp(20))

            createHeaderCell()
            addView(headerCell, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            createMoviesView()
            addView(moviesView, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        private fun createHeaderCell()
        {
            headerCell = SectionCell(context).apply {
                setOnClickListener {
                    onOpen?.invoke()
                }
            }
        }

        private fun createMoviesView()
        {
            moviesView = RecyclerView(context).apply {
                setPadding( Utils.dp(20), Utils.dp(5), Utils.dp(20), Utils.dp(5) )
                clipToPadding = false
                edgeEffectFactory = EdgeColorEffect( Theme.mainColor )
                addItemDecoration( SpacingItemDecoration( Utils.dp(13) ) )

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                adapter = MoviesAdapter(movies).apply {
                    onMovieClick {
                        onMovieClick?.invoke(it)
                    }
                }.also {
                    moviesAdapter = it
                }
            }
        }

    }

    inner class SectionCell(context: Context) : FrameLayout(context)
    {
        private var sectionNameView: TextView
        private var sectionSubtextView: TextView
        private var pointerImage: ImageView

        var sectionName: String = ""
            set(value) {
                field = value
                sectionNameView.text = sectionName
            }

        var sectionSubtext: String = ""
            set(value) {
                field = value
                sectionSubtextView.text = sectionSubtext
            }

        init
        {
            isClickable = true
            setOnTouchListener( InstantPressListener(this) )

            sectionNameView = RTextView(context).apply {
                setTextColor( Theme.color_text)
                textSize = 21F
                setTypeface( Theme.tf_bold )
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }
            addView(sectionNameView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.START or Gravity.TOP,
                20, 7, 56, 0)
            )

            sectionSubtextView = RTextView(context).apply {
                setTextColor( Theme.color_text2 )
                textSize = 14F
                setTypeface( Theme.tf_normal )
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }
            addView(sectionSubtextView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.START or Gravity.TOP,
                20, 31, 56, 0))

            pointerImage = ImageView(context).apply {
                setImageDrawable( Theme.drawable(R.drawable.pointer_forward, Theme.mainColor))
            }
            addView(pointerImage, Layout.ezFrame(
                24, 24,
                Gravity.END or Gravity.CENTER_VERTICAL,
                0, 0, 12, 0))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Utils.dp(56), MeasureSpec.EXACTLY)
            )
        }

    }

}


































//