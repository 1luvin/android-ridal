package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.UI.ActionBar.ActionBar
import tv.ridal.UI.ActionBar.BigActionBar
import tv.ridal.UI.Cells.ValueCell
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.UI.Layout.SingleCheckGroup
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.UI.Popup.BottomPopup
import tv.ridal.UI.View.RTextView
import tv.ridal.Utils.Utils

class SettingsFragment : BaseSettingsFragment()
{
    companion object
    {
        const val TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment
        {
            val fragment = SettingsFragment()
            return fragment
        }
    }

    private lateinit var rootLayout: FrameLayout
    private lateinit var scroll: ScrollView
    private lateinit var containerLayout: LinearLayout
    private lateinit var actionBar: BigActionBar

    private lateinit var visualAppearanceSectionView: TextView
    private lateinit var themeCell: ValueCell
    private var themePopup: ThemePopup? = null
    private var activeTheme: String = Locale.text(Locale.text_theme_asInSystem)
        set(value) {
            field = value

            themeCell.valueText = activeTheme
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootLayout = FrameLayout(context).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        scroll = ScrollView(context)

        containerLayout = VLinearLayout(context)

        scroll.addView(containerLayout)

        rootLayout.addView(scroll)

        //

        createActionBar()
        containerLayout.addView(actionBar)

        visualAppearanceSectionView = createSectionView( Locale.text(Locale.text_visualAppearance) )
        containerLayout.addView( visualAppearanceSectionView )

        createThemeCell()
        containerLayout.addView(themeCell, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, 46
        ))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }

    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            title = Locale.text(Locale.text_sett)
        }
    }

    private fun createSectionView(text: String) : TextView
    {
        return RTextView(context).apply {
            setPadding(Utils.dp(20), Utils.dp(15), Utils.dp(20), Utils.dp(5))

            textSize = 18F
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor(Theme.color(Theme.color_main))

            this.text = text
        }
    }

    private fun createThemeCell()
    {
        themeCell = ValueCell(context).apply {
            setPadding(Utils.dp(20), 0, Utils.dp(20), 0)

            keyText = Locale.text(Locale.text_theme)
            valueText = Locale.text(Locale.text_theme_asInSystem)

            setOnClickListener {
                if (themePopup == null) ThemePopup().show()
            }
        }
    }

    private fun switchTheme(isDark: Boolean)
    {
        val fromColors = Theme.activeColors
        val toTheme = if (isDark) 1 else 0
        val toColors = Theme.colorsList[toTheme]

        ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 250

            addUpdateListener {
                val animRatio = it.animatedValue as Float

                rootLayout.apply {
                    setBackgroundColor(
                        Theme.mixColors( fromColors[Theme.color_bg]!!, toColors[Theme.color_bg]!!, animRatio )
                    )
                }

                actionBar.titleColor = Theme.mixColors( fromColors[Theme.color_text]!!, toColors[Theme.color_text]!!, animRatio )

                themeCell.keyColor = Theme.mixColors( fromColors[Theme.color_text]!!, toColors[Theme.color_text]!!, animRatio )
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)

                    Utils.enableDarkStatusBar(requireActivity().window, Theme.isDarkTheme())
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    Theme.setTheme(toTheme)
                }
            })

            start()
        }
    }


    inner class ThemePopup : BottomPopup(context)
    {
        private lateinit var popupView: FrameLayout
        private lateinit var layout: VLinearLayout
        private lateinit var actionBar: ActionBar
        private lateinit var checkGroup: SingleCheckGroup

        init
        {
            createUI()
        }

        private fun createUI()
        {
            popupView = FrameLayout(context).apply {
                background = Theme.rect(
                    Theme.color(Theme.color_bg),
                    radii = FloatArray(4).apply {
                        fill( Utils.dp(12F) )
                    }
                )
            }

            setContentView(popupView, LayoutHelper.frame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            ).apply {
                setMargins(Utils.dp(12), 0, Utils.dp(12), Utils.dp(12))
            })

            createActionBar()
            createCheckGroup()

            layout = VLinearLayout(context).apply {
                addView(actionBar, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT
                ))
                addView(checkGroup, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    0, 0, 0, 15
                ))
            }

            popupView.addView(layout)
        }

        private fun createActionBar()
        {
            actionBar = ActionBar(context).apply {
//                titleColor = Theme.color(Theme.color_text, Theme.LIGHT)
                title = Locale.text(Locale.text_theme)
            }
        }

        private fun createCheckGroup()
        {
            checkGroup = SingleCheckGroup(context).apply {
                addCheck( Locale.text(Locale.text_theme_asInSystem) ) {
                    switchTheme(true)
                    activeTheme = Locale.text(Locale.text_theme_asInSystem)
                    dismiss()
                }
                addCheck( Locale.text(Locale.text_theme_light) ) {
                    switchTheme(false)
                    activeTheme = Locale.text(Locale.text_theme_light)
                    dismiss()
                }
                addCheck( Locale.text(Locale.text_theme_dark) ) {
                    switchTheme(true)
                    activeTheme = Locale.text(Locale.text_theme_dark)
                    dismiss()
                }

                check( activeTheme, false )
            }
        }
    }
}


































//