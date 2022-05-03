package tv.ridal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.ui.actionbar.BigActionBar
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.SingleCheckGroup
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.setBackgroundColor
import tv.ridal.ui.view.ColorView
import tv.ridal.ui.view.RTextView
import tv.ridal.ui.setPaddings
import tv.ridal.util.Utils

class SettingsFragment : BaseSettingsFragment()
{
    companion object
    {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: VLinearLayout
    private lateinit var actionBar: BigActionBar

    private lateinit var themeSection: SectionView
    private lateinit var colorsSection: SectionView

    private val themeNames: Array<String> = arrayOf(
        Locale.string(R.string.theme_asInSystem),
        Locale.string(R.string.theme_light),
        Locale.string(R.string.theme_dark)
    )
    private lateinit var themeCheckGroup: SingleCheckGroup
    private lateinit var colorsView: ColorsView

    private var text_views: ArrayList<TextView> = ArrayList()
    private var text2_views: ArrayList<TextView> = ArrayList()
    private var bg_views: ArrayList<View> = ArrayList()
    private var sectionBgs_views: ArrayList<View> = ArrayList()

    private val colors: IntArray = Theme.mainColors()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    override fun onPause()
    {
        super.onPause()

        AppActivity.instance().recreate()
    }


    private fun createUI()
    {
        createActionBar()
        createThemeSection()
        createColorsSection()
        layout = VLinearLayout(context).apply {
            addView(actionBar)
            addView(themeSection)
            addView(colorsSection)
        }
        scroll = NestedScrollView(context).apply {
            addView(layout)
        }

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color_bg )

            addView(scroll)
        }

        bg_views.add( rootFrame )
    }

    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            title = Locale.string(R.string.settings)
        }

        text_views.add( actionBar.titleView )
    }

    private fun createThemeSection()
    {
        themeCheckGroup = SingleCheckGroup(context).apply {
            background = createSectionBg( Theme.color_bg )

            for (i in themeNames.indices)
            {
                addCheck( themeNames[i] ) {
                    switchTheme(i - 1)
                }
            }
            check( themeNames[Theme.colors + 1] )
        }

        sectionBgs_views.add( themeCheckGroup )

        themeSection = SectionView(
            Locale.string(R.string.theme),
            themeCheckGroup
        )
    }

    private fun createColorsSection()
    {
        colorsView = ColorsView().apply {
            onColorChanged {
                switchMainColor(it)
            }
        }

        colorsSection = SectionView(
            Locale.string(R.string.mainColor),
            colorsView
        )
    }


    private fun createSectionBg(colorKey: String) : Drawable
    {
        return createSectionBg( Theme.color(colorKey) )
    }

    private fun createSectionBg(color: Int) : Drawable
    {
        return Theme.rect(
            Theme.overlayColor(color, 0.04F),
            radii = FloatArray(4).apply {
                fill( Utils.dp(15F) )
            }
        )
    }


    private fun switchTheme(colors: Int)
    {
        val fromColors = Theme.activeColors

        val toTheme: Int
        if (colors == Theme.FOLLOW_SYSTEM)
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
            toTheme = colors
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

            addUpdateListener { animator ->
                val ratio = animator.animatedValue as Float

                text_views.forEach {
                    it.setTextColor( Theme.mixColors(f_text, t_text, ratio) )
                }

                text2_views.forEach {
                    it.setTextColor( Theme.mixColors(f_text2, t_text2, ratio) )
                }

                bg_views.forEach {
                    it.setBackgroundColor( Theme.mixColors( f_bg, t_bg, ratio ) )
                }

                sectionBgs_views.forEach {
                    it.background = createSectionBg( Theme.mixColors( f_bg_l, t_bg_l, ratio ) )
                }

                colorsView.apply {
                    leftGradientView.background = Theme.rect(
                        Theme.Fill(
                            intArrayOf( Theme.mixColors( f_bg_l, t_bg_l, ratio ), Color.TRANSPARENT ),
                            leftGradientOri
                        ),
                        radii = FloatArray(4).apply {
                            fill( Utils.dp(15F) )
                        }
                    )
                    rightGradientView.background = Theme.rect(
                        Theme.Fill(
                            intArrayOf( Theme.mixColors( f_bg_l, t_bg_l, ratio ), Color.TRANSPARENT ),
                            rightGradientOri
                        ),
                        radii = FloatArray(4).apply {
                            fill( Utils.dp(15F) )
                        }
                    )
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?)
                {
                    super.onAnimationStart(animation)

                    themeCheckGroup.apply {
                        isEnabled = false
                        setTextColor(t_text2)
                        setTextColorChecked(t_text)
                    }

                    val enable = toTheme == 0
                    Theme.enableDarkStatusBar( requireActivity().window, enable )
                }

                override fun onAnimationEnd(animation: Animator?)
                {
                    super.onAnimationEnd(animation)

                    themeCheckGroup.apply {
                        isEnabled = true
                    }

                    Theme.colors = colors
                }
            })

            start()
        }
    }

    private fun switchMainColor(newColor: Int)
    {
        ValueAnimator.ofInt( Theme.mainColor , newColor ).apply {
            duration = 190

            setEvaluator( ArgbEvaluator() )

            addUpdateListener { animator ->
                val color = animator.animatedValue as Int

                color.let {
                    themeCheckGroup.setCheckColor(it)
                }
            }

            addListener( object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?)
                {
                    super.onAnimationEnd(animation)

                    Theme.mainColor = newColor
                }
            })

            start()
        }
    }



    inner class SectionView(sectionName: String, view: View) : VLinearLayout(context)
    {
        init
        {
            setPadding( Utils.dp(20), 0, Utils.dp(20), 0 )

            val textView = createSectionName(sectionName)
            text2_views.add(textView)

            addView(textView, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
            addView(view, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        private fun createSectionName(text: String) : RTextView
        {
            return RTextView(context).apply {
                setPadding( 0, Utils.dp(13), 0, Utils.dp(10) )

                setTextColor( Theme.color_text2 )
                textSize = 18F
                typeface = Theme.typeface(Theme.tf_normal)
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                this.text = text
            }
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
        private val gradientWidth: Int = Utils.dp(20)


        private var onColorChanged: ((Int) -> Unit)? = null
        fun onColorChanged(l: (Int) -> Unit)
        {
            onColorChanged = l
        }


        init
        {
            background = createSectionBg( Theme.color_bg )

            sectionBgs_views.add( this )

            createUI()
        }


        private fun createUI()
        {
            createColorsLayout()
            createScroll()
            horizontalScroll.addView(layout)
            addView(horizontalScroll)

            leftGradientView = createGradientView(leftGradientOri).apply {
                alpha = 0F
            }
            rightGradientView = createGradientView(rightGradientOri)

            addView(leftGradientView, Layout.frame(
                gradientWidth, Layout.MATCH_PARENT,
                Gravity.START
            ))
            addView(rightGradientView, Layout.frame(
                gradientWidth, Layout.MATCH_PARENT,
                Gravity.END
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
                        if ( ! it.isSelected) selectColor(color)
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

            onColorChanged?.invoke(color)
        }

        private fun createScroll()
        {
            horizontalScroll = HorizontalScrollView(context).apply {
                setPaddings( Utils.dp(15) )
                clipToPadding = false

                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER

                setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    val maxScrollX = (Utils.dp(15) * 2) + (Utils.dp(80) * colors.size) + (Utils.dp(10) * (colors.size - 1)) - width

                    if (scrollX < gradientWidth)
                    {
                        leftGradientView.alpha = scrollX / gradientWidth.toFloat()
                    }
                    else if (scrollX > maxScrollX - gradientWidth)
                    {
                        rightGradientView.alpha = 1F - Utils.mapToFloat(scrollX, (maxScrollX - gradientWidth), maxScrollX)
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

    }

}


































//