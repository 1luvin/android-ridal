package tv.ridal

import android.content.Context
import android.content.Intent
import android.graphics.Rect
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
import tv.ridal.adapters.MoviesAdapter
import tv.ridal.utils.Locale
import tv.ridal.utils.Theme
import tv.ridal.ui.actionbar.BigActionBar
import tv.ridal.hdrezka.HDRezka
import tv.ridal.hdrezka.Movie
import tv.ridal.hdrezka.Parser
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.utils.Utils
import tv.ridal.ui.*
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.view.RTextView
import kotlin.random.Random

class CatalogFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "CatalogFragment${Random.nextInt()}"

    companion object
    {
        fun instance() = CatalogFragment()
    }

    private lateinit var rootLayout: FrameLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var scrollLayout: LinearLayout
    private lateinit var actionBar: BigActionBar
    private val sectionViews: ArrayList<SectionView> = ArrayList()

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
        rootLayout = FrameLayout(requireContext()).apply {
            setBackgroundColor( Theme.color_bg )
        }

        scroll = NestedScrollView(requireContext()).apply {
            isVerticalScrollBarEnabled = false
        }
        scrollLayout = VLinearLayout(requireContext())
        scroll.addView(scrollLayout)

        rootLayout.addView(scroll)

        createActionBar()
        scrollLayout.addView(actionBar)

        for (i in HDRezka.section_urls.indices)
        {
            val view = SectionView(requireContext())
            sectionViews.add(view)
            scrollLayout.addView(view)
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

            title = App.APP_NAME

            this.menu = menu
        }
    }

    private fun showSharePopup()
    {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, Locale.text(Locale.share_application))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "")
        startActivity(shareIntent)
    }

    private fun loadSections()
    {
        val urls = HDRezka.section_urls

        val sectionNames = listOf(
            Locale.text(Locale.text_films),
            Locale.text(Locale.text_series),
            Locale.text(Locale.text_cartoons),
            Locale.text(Locale.text_anime),
        )

        for (i in urls.indices)
        {
            val request = StringRequest(Request.Method.GET, urls[i],
                { response ->
                    val sectionView = sectionViews[i]

                    sectionView.apply {
                        sectionName = sectionNames[i]
                        sectionSubtext = Parser.parseSectionMoviesSize(response)

                        movies.addAll( Parser.parseMovies(response, 10)!! )
                        updateMovies()

                        onOpen {
                            val args = MoviesFragment.Arguments().apply {
                                title = sectionView.sectionName
                                url = urls[i]

                                filters = HDRezka.Filters.GENRE_SORTING
                            }
                            startFragment(
                                MoviesFragment.newInstance(args)
                            )
                        }

                        onMovieClick {
                            startFragment( MovieFragment.instance(it) )
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

    inner class SectionView(context: Context) : VLinearLayout(context)
    {
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

        var movies: ArrayList<Movie> = ArrayList()
            private set
        fun updateMovies()
        {
            moviesAdapter.notifyDataSetChanged()
        }

        private lateinit var headerCell: SectionCell
        private lateinit var moviesView: RecyclerView
        private lateinit var moviesAdapter: RecyclerView.Adapter<MoviesAdapter.ViewHolder>

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
                setPadding(Utils.dp(5), Utils.dp(5), Utils.dp(5), Utils.dp(5))
                clipToPadding = false

                edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                        return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                    }
                }

                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
                    {
                        super.getItemOffsets(outRect, view, parent, state)

                        val position = parent.getChildAdapterPosition(view)
                        val size = adapter!!.itemCount

                        outRect.left = Utils.dp(15)

                        if (position == size - 1)
                            outRect.right = Utils.dp(15)
                    }
                })

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
        var sectionName: String = ""
            set(value) {
                field = value
                sectionNameView.text = sectionName
            }

        private var sectionSubtextView: TextView
        var sectionSubtext: String = ""
            set(value) {
                field = value
                sectionSubtextView.text = sectionSubtext
            }

        private var pointerImage: ImageView

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