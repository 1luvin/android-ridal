package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.tunjid.androidx.navigation.Navigator
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Application.UserData.USER
import tv.ridal.Ui.Layout.LayoutHelper
import tv.ridal.Ui.ActionBar.BigActionBar
import tv.ridal.Utils.Utils

class SettingsFragment : BaseFragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "SettingsFragment"

    private val key = "xxx"

    companion object
    {
        const val TAG = "SettingsFragment"
        //const val KEY_N = "key_n";

        fun newInstance(): SettingsFragment
        {
            //val args = Bundle()
            //args.putInt(KEY_N, n)
            val fragment = SettingsFragment()
            //fragment.arguments = args
            return fragment
        }
    }

    private lateinit var rootLayout: RelativeLayout
    private lateinit var scroll: ScrollView
    private lateinit var containerLayout: LinearLayout
    private lateinit var actionBar: BigActionBar

    private lateinit var visualAppearanceSectionView: TextView

    private lateinit var darkThemeSwitch: SwitchCompat

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

        //

        createActionBar()
        containerLayout.addView(actionBar)

        visualAppearanceSectionView = createSectionView( Locale.text(Locale.text_visualAppearance) )
        containerLayout.addView( visualAppearanceSectionView )

        createDarkThemeSwitch()
        containerLayout.addView(darkThemeSwitch)

        if (USER.hasSettings()) {
            darkThemeSwitch.isChecked = USER.settings!!.theme_isDark
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }

    override fun onResume()
    {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop()
    {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            title = Locale.text(Locale.text_sett)
        }
    }

    private fun createSectionView(text: String) : TextView
    {
        return TextView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(Utils.dp(20), Utils.dp(15), Utils.dp(20), Utils.dp(5))

            this.text = text

            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor(Theme.color(Theme.color_main))
        }
    }

    private fun createDarkThemeSwitch()
    {
        darkThemeSwitch = SwitchCompat(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                LayoutHelper.MATCH_PARENT,
                Utils.dp(46)
            )
            setPadding(Utils.dp(20), 0, Utils.dp(20), 0)

            text = Locale.text(Locale.text_darkTheme)
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_normal)

            setTextColor(ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(),
                ),
                intArrayOf(
                    Color.BLACK,
                    Color.GRAY
                )
            ))

            setOnCheckedChangeListener { buttonView, isChecked ->
                switchTheme(isChecked)
            }
        }
    }

    private fun switchTheme(isDark: Boolean)
    {
//        val views = rootLayout.getAllViews()
//        for (view in views)
//        {
//            if (view is TextView)
//            {
//                println( view.colorKey )
//            }
//        }

        val fromColors = Theme.activeColors
        val toTheme = if (isDark) 1 else 0
        val toColors = Theme.colorsList[toTheme]

        ValueAnimator.ofFloat(0F, 1F).apply {

            addUpdateListener {

                val animRatio = it.animatedValue as Float

                rootLayout.apply {
                    setBackgroundColor(
                        Theme.mixColors( fromColors[Theme.color_bg]!!, toColors[Theme.color_bg]!!, animRatio )
                    )
                }

                actionBar.titleColor = Theme.mixColors( fromColors[Theme.color_text]!!, toColors[Theme.color_text]!!, animRatio )
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    Theme.setTheme( toTheme )
                }
            })

            start()
        }

        USER.settings?.theme_isDark = isDark
    }

}


































//