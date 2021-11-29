package tv.ridal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.TransitionInflater
import com.tunjid.androidx.navigation.Navigator
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Cells.CatalogSectionCell
import tv.ridal.Components.Layout.LayoutHelper
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

    override fun onCreate(savedInstanceState: Bundle?) {
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

        for (i in 0 until 30)
        {
            containerLayout.addView(CatalogSectionCell(requireContext()).apply {
                sectionName = Locale.text(Locale.text_collections)
                sectionSubtext = "90 подборок"

                setOnClickListener {
                    (requireActivity() as ApplicationActivity).multiStackNavigator.push(CatalogFragment.newInstance(), this@CatalogFragment.stableTag)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        return rootLayout
    }

    private fun createScreenTitleView() : View
    {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.WRAP_CONTENT
            )
            setPadding(Utils.dp(30), Utils.dp(25), 0, Utils.dp(25))

            text = Locale.text(Locale.text_catalog)

            typeface = Theme.typeface(Theme.tf_bold)
            textSize = 30F
            setTextColor(Theme.color(Theme.color_text))
        }
    }

}


































//