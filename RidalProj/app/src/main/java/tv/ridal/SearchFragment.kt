package tv.ridal

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.tunjid.androidx.navigation.Navigator
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import tv.ridal.Adapters.SearchAdapter
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Cells.SearchResultCell
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.SearchView
import tv.ridal.HDRezka.Parser
import tv.ridal.HDRezka.SearchResult
import tv.ridal.Utils.Utils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


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
    //
    // список результатов поиска
    private lateinit var resultsListView: ListView
    // результаты поиска
    private var searchResults: ArrayList<SearchResult> = ArrayList()
    // индикатор загрузки
    private lateinit var loadingProgress: ProgressBar

    private val requestQueue: RequestQueue = ApplicationLoader.instance().requestQueue

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
            layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
        }

        loadingProgress = ProgressBar(requireContext()).apply {
            isIndeterminate = true
            visibility = View.GONE
        }

        resultsFrame.addView(loadingProgress, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.CENTER
        ))

//        resultsScroll = ScrollView(requireContext()).apply {
//            layoutParams = LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
//        }

//        resultsLayout = LinearLayout(requireContext()).apply {
//            layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT)
//            orientation = LinearLayout.VERTICAL
//        }

        // создание ListView
        resultsListView = ListView(requireContext()).apply {
            adapter = SearchAdapter(searchResults)
        }

        resultsFrame.addView(resultsListView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))

//        resultsScroll.addView(resultsLayout)
//
//        resultsFrame.addView(resultsScroll)

        rootLayout.addView(resultsFrame)

        rootFrame.addView(rootLayout)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
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

//                val resList = doc.getElementsByTag("ul")
//                val lis = resList[0].getElementsByTag("li")
//                for (li in lis)
//                {
//                    val s = li.getElementsByTag("a").attr("href")
//                    println(s)
//                }

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
        loadingProgress.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        loadingProgress.visibility = View.GONE
        resultsListView.visibility = View.VISIBLE
    }

}





































//