package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.ui.actionbar.BigActionBar
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.SingleCheckGroup
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.view.ColorView
import tv.ridal.ui.view.RTextView
import tv.ridal.ui.setPaddings
import tv.ridal.util.Utils

class SettingsFragment : BaseSettingsFragment()
{
    companion object
    {
        fun instance() = SettingsFragment()
    }

    private lateinit var rootLayout: FrameLayout
    private lateinit var scroll: ScrollView
    private lateinit var layout: VLinearLayout
    private lateinit var actionBar: BigActionBar

    private lateinit var themeSectionView: RTextView
    private val themeNames: Array<String> = arrayOf(
        Locale.string(R.string.theme_asInSystem),
        Locale.string(R.string.theme_light),
        Locale.string(R.string.theme_dark)
    )
    private lateinit var themeCheckGroup: SingleCheckGroup

    private lateinit var colorsSectionView: RTextView
    private val colors: IntArray = Theme.mainColors()
    private lateinit var colorsView: ColorsView


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

    override fun onDestroy()
    {
        super.onDestroy()

        // обновление темы в данных пользователя
        Theme.update()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootLayout
    }

    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            title = Locale.string(R.string.settings)
        }
    }

    private fun createSectionView(text: String) : RTextView
    {
        return RTextView(context).apply {
            setPadding(Utils.dp(20 + 15), Utils.dp(13), Utils.dp(20), Utils.dp(10))

            textSize = 20F
            typeface = Theme.typeface(Theme.tf_bold)
            setTextColor( Theme.color_text2 )
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            this.text = text
        }
    }

    private fun createThemeSection()
    {
        themeSectionView = createSectionView( Locale.string(R.string.theme) )

        themeCheckGroup = SingleCheckGroup(context).apply {
            background = Theme.rect(
                Theme.overlayColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill( Utils.dp(15F) )
                }
            )

            for (i in themeNames.indices)
            {
                addCheck( themeNames[i] ) {
                    switchTheme(i - 1)
                }
            }
            check( themeNames[Theme.currentId + 1] )
        }

        layout.apply {
            addView(themeSectionView)
            addView(themeCheckGroup, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                20, 0, 20, 0
            ))
        }
    }

    private fun createColorsSection()
    {
        colorsSectionView = createSectionView( Locale.string(R.string.mainColor) )

        colorsView = ColorsView()

        layout.apply {
            addView(colorsSectionView, Layout.ezLinear(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                0, 15, 0, 0
            ))
            addView(colorsView, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                20, 0, 20, 0
            ))
        }
    }

    private fun switchTheme(themeId: Int)
    {
        val fromColors = Theme.activeColors

        val toTheme: Int
        if (themeId == Theme.FOLLOW_SYSTEM)
        {
            val conf = App.instance().configuration
            val nightMode = conf.uiMode and Configuration.UI_MODE_NIGHT_YES
            if (nightMode == Configuration.UI_MODE_NIGHT_YES)
                toTheme = Theme.DARK
            else
                toTheme = Theme.LIGHT
        }
        else
        {
            toTheme = themeId
        }

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
            duration = 190

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
                colorsView.apply {
                    leftGradientView.background = Theme.rect(
                        Theme.Fill(
                            intArrayOf( Theme.mixColors( f_bg_l, t_bg_l, animRatio ), Theme.COLOR_TRANSPARENT ),
                            leftGradientOri
                        ),
                        radii = FloatArray(4).apply {
                            fill(Utils.dp(15F))
                        }
                    )
                    rightGradientView.background = Theme.rect(
                        Theme.Fill(
                            intArrayOf( Theme.mixColors( f_bg_l, t_bg_l, animRatio ), Theme.COLOR_TRANSPARENT ),
                            rightGradientOri
                        ),
                        radii = FloatArray(4).apply {
                            fill(Utils.dp(15F))
                        }
                    )
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)

                    themeCheckGroup.apply {
                        isEnabled = false
                        setTextColor(t_text2)
                        setTextColorChecked(t_text)
                    }

                    val enable = toTheme == 0
                    Theme.enableDarkStatusBar(requireActivity().window, enable)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    themeCheckGroup.apply {
                        isEnabled = true
                    }

                    Theme.setTheme(themeId)
                }
            })

            start()
        }
    }

    inner class ColorsView : FrameLayout(context)
    {
        private lateinit var horizontalScroll: HorizontalScrollView
        private lateinit var layout: LinearLayout
        private var colorViews: ArrayList<ColorView> = ArrayList()

        lateinit var leftGradientView: View
        val leftGradientOri: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT
        lateinit var rightGradientView: View
        val rightGradientOri: GradientDrawable.Orientation = GradientDrawable.Orientation.RIGHT_LEFT
        private val gradientWidth: Int = Utils.dp(30)

        init
        {
            isClickable = true
            setOnTouchListener( InstantPressListener(this) )

            background = Theme.rect(
                Theme.overlayColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill(Utils.dp(15F))
                }
            )

            createUI()
        }

        private fun createUI()
        {
            createScroll()

            createColorsLayout()
            horizontalScroll.addView(layout)
            addView(horizontalScroll)

            leftGradientView = createGradientView(leftGradientOri).apply {
                alpha = 0F
            }
            rightGradientView = createGradientView(rightGradientOri)

            addView(leftGradientView, Layout.frame(
                gradientWidth, Layout.MATCH_PARENT,
                Gravity.LEFT
            ))
            addView(rightGradientView, Layout.frame(
                gradientWidth, Layout.MATCH_PARENT,
                Gravity.RIGHT
            ))
        }

        private fun createColorsLayout()
        {
            layout = LinearLayout(context)

            for (i in colors.indices)
            {
                val colorView = ColorView(context).apply {
                    color = colors[i]

                    setOnClickListener {
                        if ( ! it.isSelected)
                        {
                            selectColor(color)
                        }
                    }
                }
                colorViews.add(colorView)

                if (colorView.color == Theme.mainColor)
                {
                    layout.addView(colorView, 0, Layout.ezLinear(
                        Layout.WRAP_CONTENT, Layout.WRAP_CONTENT
                    ))
                }
                else
                {
                    layout.addView(colorView, Layout.ezLinear(
                        Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                        10, 0, 0, 0
                    ))
                }
            }

            val cv = colorViews.find {
                it.color == Theme.mainColor
            }
            cv?.isSelected = true
        }

        private fun selectColor(color: Int)
        {
            var cv = colorViews.find {
                it.isSelected
            }
            cv?.isSelected = false

            cv = colorViews.find {
                it.color == color
            }
            cv?.isSelected = true

            onColorChanged(color)
        }

        private fun createScroll()
        {
            horizontalScroll = HorizontalScrollView(context).apply {
                setPaddings( Utils.dp(15) )
                clipToPadding = false

                isSmoothScrollingEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER

                setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    // TODO: странная дичь со scrollRange
                    val range = computeHorizontalScrollRange() - Utils.dp(30)
                    if (scrollX < gradientWidth)
                    {
                        leftGradientView.alpha = scrollX / gradientWidth.toFloat()
                    }
                    else if (scrollX > range - gradientWidth)
                    {
                        rightGradientView.alpha = 1F - Utils.mapToFloat(scrollX, (range - gradientWidth), range)
                    }
                    else
                    {
                        leftGradientView.alpha = 1F
                        rightGradientView.alpha = 1F
                    }
                }
            }
        }

        private fun createGradientView(orientation: GradientDrawable.Orientation) : View
        {
            val cFrom: Int = Theme.overlayColor(Theme.color_bg, 0.04F)
            val cTo: Int = Theme.COLOR_TRANSPARENT

            return View(context).apply {
                background = Theme.rect(
                    Theme.Fill( intArrayOf(cFrom, cTo), orientation ),
                    radii = FloatArray(4).apply {
                        fill(Utils.dp(15F))
                    }
                )
            }
        }

        private fun onColorChanged(newColor: Int)
        {
            Theme.mainColor = newColor

            val oldColor = themeCheckGroup.getCheckColor()

            ValueAnimator.ofInt(oldColor, newColor).apply {
                setEvaluator( ArgbEvaluator() )
                duration = 250

                addUpdateListener {
                    val color = it.animatedValue as Int
                    themeCheckGroup.setCheckColor(color)
                }

                start()
            }
        }

    }
}


































//