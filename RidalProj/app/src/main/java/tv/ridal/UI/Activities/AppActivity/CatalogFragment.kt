package tv.ridal.UI.Activities.AppActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import tv.ridal.*
import tv.ridal.UI.Adapters.MoviesAdapter
import tv.ridal.Application.App
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.UI.ActionBar.BigActionBar
import tv.ridal.UI.View.SectionView
import tv.ridal.HDRezka.HDRezka
import tv.ridal.HDRezka.Parser
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.Application.Utils
import tv.ridal.UI.Activities.SettingsActivity.SettingsActivity
import tv.ridal.UI.msg
import kotlin.random.Random

class CatalogFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "CatalogFragment${Random.nextInt()}"

    companion object
    {
        fun newInstance() = CatalogFragment()
    }

    private lateinit var rootLayout: FrameLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var scrollLayout: LinearLayout
    private lateinit var actionBar: BigActionBar

    private val requestQueue: RequestQueue = App.instance().requestQueue

    private val sectionViews: ArrayList<SectionView> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootLayout = FrameLayout(requireContext()).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        scroll = NestedScrollView(requireContext()).apply {
            isVerticalScrollBarEnabled = false
        }
        scrollLayout = VLinearLayout(requireContext())
        scroll.addView(scrollLayout)

        rootLayout.addView(scroll)

        createActionBar()
        scrollLayout.addView(actionBar)

        for (i in HDRezka.SECTION_URLS.indices)
        {
            val view = SectionView(requireContext())
            sectionViews.add(view)
            scrollLayout.addView(view)
        }

        loadSections()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }

    private fun createActionBar()
    {
        val menu = BigActionBar.Menu(context).apply {
            addItem(Theme.drawable(R.drawable.invite, Theme.color_actionBar_menuItem)) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, Locale.text(Locale.share_application))
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "")
                startActivity(shareIntent)
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

    private fun loadSections()
    {
        val urls = HDRezka.SECTION_URLS

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

                    val movies = Parser.parseMovies(response, 10)!!

                    sectionView.apply {
                        sectionName = sectionNames[i]
                        sectionSubtext = Parser.parseSectionMoviesSize(response)

                        adapter = MoviesAdapter(movies).apply {
                            onMovieClick {
                                startFragment(MovieFragment.newInstance(it))
                            }
                        }

                        onOpen {
                            val args = MoviesFragment.Arguments().apply {
                                title = sectionView.sectionName
                                url = urls[i]
                            }
                            startFragment(
                                MoviesFragment.newInstance(args)
                            )
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

}


































//