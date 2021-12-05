package tv.ridal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tunjid.androidx.navigation.Navigator
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Cells.CatalogSectionCell
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.MovieView
import tv.ridal.Components.ScreenTitleBar
import tv.ridal.Components.SectionView
import tv.ridal.HDRezka.HDRezka
import tv.ridal.HDRezka.Parser
import tv.ridal.Utils.Utils

class CatalogFragment : BaseFragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "CatalogFragment${View.generateViewId()}"

    companion object
    {
        const val TAG = "CatalogFragment"

        fun newInstance(): CatalogFragment
        {
            val fragment = CatalogFragment()
            return fragment
        }
    }

    private lateinit var rootLayout: RelativeLayout
    private lateinit var scroll: ScrollView
    private lateinit var containerLayout: LinearLayout

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue

    private val sectionViews: ArrayList<SectionView> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootLayout = RelativeLayout(requireContext()).apply {
            layoutParams = RelativeLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )

            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        scroll = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
        }

        containerLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(containerLayout)
        rootLayout.addView(scroll)

        containerLayout.addView(createScreenTitleView())


        for (i in HDRezka.SECTION_URLS.indices)
        {
            sectionViews.add( SectionView(requireContext()) )
            containerLayout.addView(sectionViews[i])
        }


        loadSections()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }


    private fun createScreenTitleView() : View
    {
        return ScreenTitleBar(requireContext()).apply {
            title = ApplicationLoader.APP_NAME
            image = Theme.drawable(R.drawable.invite).apply {
                setTint(Theme.color(Theme.color_text))
            }
            imageClickListener = View.OnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, Locale.text(Locale.share_application))
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "")
                startActivity(shareIntent)
            }
        }
    }

    private fun loadSections()
    {
        val urls = HDRezka.SECTION_URLS

        println(urls)

        for (i in urls.indices)
        {
            val stringRequest = StringRequest(Request.Method.GET, urls[i],
                { response ->
                    val sectionView = sectionViews[i]

                    val movies = Parser.parseMovies(response, 10)!!

                    sectionView.sectionName = "Хеллов"
                    sectionView.sectionSubtext = "Сабтекст"

                    sectionView.adapter = MoviesAdapter(movies)
                },
                {
                    println("ERROR!")
                }
            )
            requestQueue.add(stringRequest)
        }
    }

    private fun createMoviesView()
    {

    }


}


































//