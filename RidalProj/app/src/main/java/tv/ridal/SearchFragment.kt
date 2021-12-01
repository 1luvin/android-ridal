package tv.ridal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tunjid.androidx.navigation.Navigator
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Cells.SearchResultCell
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.SearchView
import tv.ridal.Utils.Utils
import kotlin.random.Random


class SearchFragment : BaseFragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "SearchFragment"

    companion object
    {
        const val TAG = "CatalogFragment"

        fun newInstance(): SearchFragment {
            val fragment = SearchFragment()
            return fragment
        }
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var rootLayout: LinearLayout

    private lateinit var searchView: SearchView

    private lateinit var resultsFrame: FrameLayout
    private lateinit var resultsScroll: ScrollView
    private lateinit var resultsLayout: LinearLayout

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

        rootLayout.addView(createScreenTitleView())

        createSearchView()
        rootLayout.addView(searchView)

        resultsFrame = FrameLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT)
        }

        resultsScroll = ScrollView(requireContext()).apply {
            layoutParams = LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        }

        resultsLayout = LinearLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
        }

        for (i in 0 until 11)
        {
            resultsLayout.addView(
                SearchResultCell(requireContext()).apply {
                    resultText = "Result ${i + 1}"
                    resultValue = i + 0F
                }
            )
        }

        resultsScroll.addView(resultsLayout)

        resultsFrame.addView(resultsScroll)

        rootLayout.addView(resultsFrame)

        rootFrame.addView(rootLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        return rootFrame
    }

    private fun createScreenTitleView() : View
    {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.WRAP_CONTENT
            )
            setPadding(Utils.dp(30), Utils.dp(25), 0, Utils.dp(25))

            text = Locale.text(Locale.text_search)

            typeface = Theme.typeface(Theme.tf_bold)
            textSize = 30F
            setTextColor(Theme.color(Theme.color_text))
        }
    }

    private fun createSearchView()
    {
        searchView = SearchView(requireContext()).apply {
            maxLength = 5

            searchListener = object : SearchView.SearchListener() {
                override fun onTextChanged(text: CharSequence) {
                    //println(text)
                }
            }
        }
    }


    private fun clearResults()
    {
        resultsLayout.removeAllViews()
    }

}





































//