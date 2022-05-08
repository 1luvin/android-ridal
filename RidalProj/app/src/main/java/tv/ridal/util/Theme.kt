package tv.ridal.util

import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.*
import android.view.Window
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import tv.ridal.App

class Theme
{
    companion object
    {
        /*
            Color themes
         */

        const val FOLLOW_SYSTEM = -1
        const val LIGHT: Int = 0
        const val DARK: Int = 1

        /*
            Main colors
         */

        private const val COLOR_TWITCH = 0xFF7C24FF.toInt()
        const val COLOR_LIGHT_CHERRY = 0xFFFF005F.toInt()
        private const val COLOR_TELEGRAM = 0xFF00B2FF.toInt()
        private const val COLOR_SOFT_BLUE = 0xFF4D00FF.toInt()
        private const val COLOR_SOFT_GREEN = 0xFF54EA54.toInt()
        private const val COLOR_ORANGE = 0xFFFA8700.toInt()
        private const val COLOR_PINK = 0xFFFF00F5.toInt()

        fun mainColors() : IntArray
        {
            return intArrayOf(
                COLOR_TWITCH,
                COLOR_LIGHT_CHERRY,
                COLOR_TELEGRAM,
                COLOR_SOFT_BLUE,
                COLOR_SOFT_GREEN,
                COLOR_ORANGE,
                COLOR_PINK
            )
        }

        init
        {
            createColors()
        }

        private fun createColors()
        {
            var colorBg = 0xFF1A1640.toInt()
            var colorText = Color.WHITE
            var colorText2 = mixColors(colorBg, colorText, 0.7F)
            var colorBottomNavIconInactive = mixColors(colorBg, Color.WHITE, 0.5F)
            val colorSearchResultBest = 0xFF00FF00.toInt()
            var colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
            val colorSearchResultWorst = 0xFFFF0000.toInt()
            var colorActionBarBack = mixColors(colorBg, Color.WHITE, 0.7F)

            darkColors = HashMap<String, Int>().apply {
                put(color_bg, colorBg)

                put(color_text, colorText)
                put(color_text2, colorText2)

                put(color_bottomNavBg, lightenColor(colorBg, 0.03F))
                put(color_bottomNavIcon_inactive, colorBottomNavIconInactive)
                put(color_bottomNavIcon_active, 0xFFFFFFFF.toInt())

                put(color_searchResult_best, colorSearchResultBest)
                put(color_searchResult_middle, colorSearchResultMiddle)
                put(color_searchResult_worst, colorSearchResultWorst)

                put(color_actionBar_bg, lightenColor(colorBg, 0.03F))
                put(color_actionBar_back, colorActionBarBack)
                put(color_actionBar_menuItem, 0xFFFFFFFF.toInt())
            }

            colorBg = Color.WHITE
            colorText = 0xFF000000.toInt()
            colorText2 = mixColors(colorBg, colorText, 0.7F)
            colorBottomNavIconInactive = mixColors(colorBg, Color.BLACK, 0.5F)
            colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
            colorActionBarBack = mixColors(colorBg, Color.BLACK, 0.7F)

            lightColors = HashMap<String, Int>().apply {
                put(color_bg, colorBg)

                put(color_text, colorText)
                put(color_text2, colorText2)

                put(color_bottomNavBg, colorBg)
                put(color_bottomNavIcon_inactive, colorBottomNavIconInactive)
                put(color_bottomNavIcon_active, Color.BLACK)

                put(color_searchResult_best, colorSearchResultBest)
                put(color_searchResult_middle, colorSearchResultMiddle)
                put(color_searchResult_worst, colorSearchResultWorst)

                put(color_actionBar_bg, colorBg)
                put(color_actionBar_back, colorActionBarBack)
                put(color_actionBar_menuItem, 0xFF000000.toInt())
            }
        }

        fun isDark() : Boolean
        {
            return activeColors == darkColors
        }

        fun initTheme()
        {
            val pref = App.instance().settingsPref
            // Colors init
            colors = pref.getInt( key_colors, FOLLOW_SYSTEM )
            // Main color init
            mainColor = pref.getInt( key_mainColor, COLOR_TWITCH )
        }

        var colors: Int = FOLLOW_SYSTEM // !
            set(value) {
                field = value

                if ( colors == FOLLOW_SYSTEM )
                {
                    val conf = App.instance().configuration
                    val nightMode = conf.uiMode and Configuration.UI_MODE_NIGHT_YES
                    if (nightMode == Configuration.UI_MODE_NIGHT_YES)
                        activeColors = colorsList[DARK]
                    else
                        activeColors = colorsList[LIGHT]
                }
                else
                {
                    activeColors = colorsList[colors]
                }

                val editor = App.instance().settingsPref.edit()
                editor.apply {
                    putInt( key_colors, colors )
                    apply()
                }
            }

        var mainColor = COLOR_TWITCH // !
            set(value) {
                field = value

                val editor = App.instance().settingsPref.edit()
                editor.apply {
                    putInt( key_mainColor, mainColor )
                    apply()
                }
            }


        /*
            Theme keys for shared preferences
         */

        private const val key_colors = "key_colors"
        private const val key_mainColor = "key_mainColor"

        /*
            Color keys
         */

        const val color_main = "color_main"

        const val color_bg = "color_bg"

        const val color_text = "color_text"
        const val color_text2 = "color_text2"

        const val color_searchResult_best = "color_searchResult_best"
        const val color_searchResult_middle = "color_searchResult_middle"
        const val color_searchResult_worst = "color_searchResult_worst"

        const val color_bottomNavBg = "color_bottomNavBg"
        const val color_bottomNavIcon_inactive = "color_bottomNavIcon_inactive"
        const val color_bottomNavIcon_active = "color_bottomNavIcon_active"

        const val color_actionBar_bg = "color_actionBar_bg"
        const val color_actionBar_back = "color_actionBar_back"
        const val color_actionBar_menuItem = "color_actionBar_menuItem"

        /*
            Colors
         */

        // Light colors
        lateinit var lightColors: HashMap<String, Int>
            private set
        // Dark colors
        lateinit var darkColors: HashMap<String, Int>
            private set
        // Active colors
        lateinit var activeColors: HashMap<String, Int>
            private set
        // All colors array
        val colorsList = arrayOf(
            lightColors,
            darkColors,
        )

        fun color(colorKey: String) : Int
        {
            when (colorKey)
            {
                color_main -> return mainColor
            }
            return activeColors[colorKey] ?: 0xFF
        }

        fun color(colorKey: String, theme: Int) : Int
        {
            when (colorKey) {
                color_main -> return mainColor
            }

            return when (theme) {
                LIGHT -> lightColors[colorKey] ?: 0xFF
                DARK -> darkColors[colorKey] ?: 0xFF
                else -> 0xFF
            }
        }

        /*
            Fonts
         */

        const val tf_normal = "fonts/ps_normal.ttf"
        const val tf_bold = "fonts/ps_bold.ttf"

        fun typeface(tfKey: String) : Typeface
        {
            return Typeface.createFromAsset(App.instance().assets, tfKey)
        }

        /*
            Color functions
         */

        fun lightenColor(color: Int, value: Float = 0.02F) : Int
        {
            val hsv = FloatArray(3)
            val outHsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            outHsv[0] = hsv[0]
            outHsv[1] = hsv[1]
            if (hsv[2] + value <= 1) {
                outHsv[2] = hsv[2] + value
            } else {
                outHsv[2] = 1F
            }

            return Color.HSVToColor(outHsv)
        }
        fun lightenColor(colorKey: String, value: Float = 0.02F) : Int
        {
            return lightenColor( color(colorKey), value )
        }

        fun darkenColor(color: Int, value: Float = 0.02F) : Int
        {
            val hsv = FloatArray(3)
            val outHsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            outHsv[0] = hsv[0]
            outHsv[1] = hsv[1]
            if (hsv[2] - value >= 0) {
                outHsv[2] = hsv[2] - value
            } else {
                outHsv[2] = 0F
            }

            return Color.HSVToColor(outHsv)
        }
        fun darkenColor(colorKey: String, value: Float = 0.02F) : Int
        {
            return darkenColor( color(colorKey), value )
        }

        // Darken or lighten the color (depends on the input color)
        fun overlayColor(color: Int, value: Float = 0.02F) : Int
        {
            val hsv = FloatArray(3)
            val outHsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            outHsv[0] = hsv[0]
            outHsv[1] = hsv[1]
            if (hsv[2] > 0.5F) {
                outHsv[2] = hsv[2] - value
            } else {
                outHsv[2] = hsv[2] + value
            }

            return Color.HSVToColor(outHsv)
        }
        fun overlayColor(colorKey: String, value: Float = 0.02F) : Int
        {
            return overlayColor( color(colorKey), value )
        }

        fun alphaColor(color: Int, @FloatRange( from=0.0, to=1.0 ) alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent(color, (255 * alpha).toInt())
        }
        fun alphaColor(colorKey: String, @FloatRange( from=0.0, to=1.0 ) alpha: Float) : Int
        {
            return alphaColor(color(colorKey), alpha)
        }

        fun mixColors(color1: Int, color2: Int, @FloatRange( from=0.0, to=1.0 ) ratio: Float) : Int
        {
            return ColorUtils.blendARGB(color1, color2, ratio)
        }

        /*
            Drawable
         */

        fun drawable(drawableId: Int): Drawable
        {
            return ContextCompat.getDrawable( App.instance().applicationContext, drawableId )!!
        }
        fun drawable(drawableId: Int, color: Int): Drawable
        {
            return drawable(drawableId).apply {
                setTint(color)
            }
        }
        fun drawable(drawableId: Int, colorKey: String): Drawable
        {
            return drawable( drawableId, color(colorKey) )
        }

        /*
            Rect
         */

        fun rect(color: Int, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
        {
            return rect(
                Fill( intArrayOf(color, color) ),
                outline,
                radii
            )
        }
        fun rect(colorKey: String, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
        {
            return rect(
                color(colorKey),
                outline,
                radii
            )
        }
        fun rect(fill: Fill? = null, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
        {
            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in radii.indices)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            return GradientDrawable().apply {
                if (fill != null) {
                    colors = fill.colors
                    orientation = fill.orientation
                }
                if (outline != null) {
                    setStroke( outline.width, outline.color )
                }
                if (radii != null) {
                    cornerRadii = radiiArray
                }
            }
        }

        /*
            Window
         */

        fun enableDarkStatusBar(window: Window, enable: Boolean)
        {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = enable
        }

    }

    data class Outline( val color: Int, val width: Int = Utils.dp(1) )
    data class Fill( val colors: IntArray, val orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT )
}


































//