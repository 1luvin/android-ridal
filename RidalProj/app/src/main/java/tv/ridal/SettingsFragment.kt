package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.UI.ActionBar.BigActionBar
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.UI.Layout.SingleCheckGroup
import tv.ridal.UI.Layout.VLinearLayout
import tv.ridal.UI.View.ColorView
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
    private lateinit var layout: VLinearLayout
    private lateinit var actionBar: BigActionBar

    private lateinit var themeSectionView: RTextView
    private val themes: List<String> = listOf(
        Locale.text(Locale.text_theme_asInSystem),
        Locale.text(Locale.text_theme_light),
        Locale.text(Locale.text_theme_dark)
    )
    private lateinit var themeCheckGroup: SingleCheckGroup

    private lateinit var colorsSectionView: RTextView
    private val colors: IntArray = Theme.mainColors()
    private lateinit var colorsView: HorizontalScrollView


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootLayout = FrameLayout(context).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        scroll = ScrollView(context)

        layout = VLinearLayout(context)

        scroll.addView(layout)

        rootLayout.addView(scroll)

        //

        createActionBar()
        layout.addView(actionBar)

        createThemeSection()
        createColorsSection()
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

    private fun createSectionView(text: String) : RTextView
    {
        return RTextView(context).apply {
            setPadding(Utils.dp(40), Utils.dp(10), Utils.dp(20), Utils.dp(10))

            textSize = 16F
            typeface = Theme.typeface(Theme.tf_normal)
            setTextColor(Theme.color(Theme.color_text2))
            isAllCaps = true

            this.text = text
        }
    }

    private fun createThemeSection()
    {
        themeSectionView = createSectionView( Locale.text(Locale.text_theme) )

        themeCheckGroup = SingleCheckGroup(context).apply {
            background = Theme.rect(
                Theme.overlayColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill( Utils.dp(15F) )
                }
            )

            themes.forEach {
                addCheck(it) {
                    if (it == themes[0] || it == themes[2]) {
                        switchTheme(true)
                    } else {
                        switchTheme(false)
                    }
                }
            }
            check(themes[0])
        }

        layout.apply {
            addView(themeSectionView)
            addView(themeCheckGroup, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                20, 0, 20, 0
            ))
        }
    }

    private fun createColorsSection()
    {
        colorsSectionView = createSectionView( Locale.text(Locale.text_mainColor) )

        val colorsLayout = LinearLayout(context)
        for (i in colors.indices)
        {
            val colorView = ColorView(context).apply {
                color = colors[i]

                setOnClickListener {
                    // TODO
                }
            }

            colorsLayout.addView(colorView, LayoutHelper.createLinear(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                if (i != 0) 10 else 0, 0, 0, 0
            ))
        }

        colorsView = HorizontalScrollView(context).apply {
            setPadding(Utils.dp(15), Utils.dp(15), Utils.dp(15), Utils.dp(15))
            clipToPadding = false
            background = Theme.rect(
                Theme.overlayColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill( Utils.dp(15F) )
                }
            )

            isSmoothScrollingEnabled = false
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER

            addView(colorsLayout)
        }

        layout.apply {
            addView(colorsSectionView, LayoutHelper.createLinear(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                0, 15, 0, 0
            ))
            addView(colorsView, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                20, 0, 20, 0
            ))
        }
    }

    private fun switchTheme(isDark: Boolean)
    {
        val fromColors = Theme.activeColors
        val toTheme = if (isDark) 1 else 0
        val toColors = Theme.colorsList[toTheme]

        val f_bg = fromColors[Theme.color_bg]!!
        val f_bg_l = Theme.overlayColor(f_bg, 0.04F)
        val f_text = fromColors[Theme.color_text]!!
        val f_text2 = fromColors[Theme.color_text2]!!

        val t_bg = toColors[Theme.color_bg]!!
        val t_bg_l = Theme.overlayColor(t_bg, 0.04F)
        val t_text = toColors[Theme.color_text]!!
        val t_text2 = toColors[Theme.color_text2]!!

        ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 250

            addUpdateListener {
                val animRatio = it.animatedValue as Float

                rootLayout.setBackgroundColor(
                    Theme.mixColors( f_bg, t_bg, animRatio )
                )

                actionBar.titleColor =
                    Theme.mixColors( f_text, t_text, animRatio )

                themeSectionView.setTextColor( Theme.mixColors(f_text2, t_text2, animRatio) )
                themeCheckGroup.background = Theme.rect(
                    Theme.mixColors( f_bg_l, t_bg_l, animRatio ),
                    radii = FloatArray(4).apply {
                        fill( Utils.dp(15F) )
                    }
                )

                colorsSectionView.setTextColor( Theme.mixColors(f_text2, t_text2, animRatio) )
                colorsView.background = Theme.rect(
                    Theme.mixColors( f_bg_l, t_bg_l, animRatio ),
                    radii = FloatArray(4).apply {
                        fill( Utils.dp(15F) )
                    }
                )
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)

                    themeCheckGroup.apply {
                        setTextColor(t_text2)
                        setTextColorChecked(t_text)
                    }

                    Utils.enableDarkStatusBar(requireActivity().window, ! isDark)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    Theme.setTheme(toTheme)
                }
            })

            start()
        }
    }
}


































//