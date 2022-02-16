package tv.ridal.Application

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.util.StateSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import tv.ridal.Utils.Utils

class Theme
{
    companion object
    {

        init
        {
            createColors()
        }

        private fun createColors()
        {
            mainColor = COLOR_TWITCH

            var colorBg = 0xFF201B4E.toInt()
            var colorText = 0xFFFFFFFF.toInt()
            var colorText2 = mixColors(colorBg, colorText, 0.7F)
            var colorBottomNavIconInactive = mixColors(colorBg, mainColor, 0.5F)
            var colorSearchResultBest = 0xFF00FF00.toInt()
            var colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
            var colorSearchResultWorst = 0xFFFF0000.toInt()
            var colorActionBarBack = mixColors(colorBg, colorText, 0.7F)

            darkColors = HashMap<String, Int>().apply {
                put(color_bg, colorBg)
                put(color_text, colorText)
                put(color_text2, colorText2)
                put(color_bottomNavIcon_inactive, colorBottomNavIconInactive)
                put(color_searchResult_best, colorSearchResultBest)
                put(color_searchResult_middle, colorSearchResultMiddle)
                put(color_searchResult_worst, colorSearchResultWorst)
                put(color_actionBar_back, colorActionBarBack)
                put(color_negative, 0xFFFF6565.toInt())
                put(color_popup_holder, 0xFFAAAAAA.toInt())
                put(color_radio, 0xFFBBBBBB.toInt())
            }
        }

        const val COLOR_TRANSPARENT = 0x00000000
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_BLACK = 0xFF000000.toInt()

        const val COLOR_TWITCH = 0xFF7C24FF.toInt()
        const val COLOR_LIGHT_CHERRY = 0xFFFF005F.toInt()

        const val LIGHT: Int = 0
        const val DARK: Int = 1

        fun setTheme(theme: Int)
        {
            activeColors = colorsList[theme]
        }

        /*
            Ключи для цветов
         */

        const val color_main = "color_main"

        const val color_bg = "color_bg"

        const val color_text = "color_text"
        const val color_text2 = "color_text2"

        const val color_searchResult_best = "color_searchResult_best"
        const val color_searchResult_middle = "color_searchResult_middle"
        const val color_searchResult_worst = "color_searchResult_worst"

        const val color_bottomNavIcon_inactive = "color_bottomNavIcon_inactive"
        const val color_bottomNavIcon_active = "color_bottomNavIcon_active"

        const val color_actionBar_back = "color_actionBar_back"

        const val color_bigActionBar_item_bg = "color_bigActionBar_item_bg"

        const val color_negative = "color_negative"

        const val color_popup_holder = "color_popup_holder"

        // RadioButton
        const val color_radio = "color_radio"

        /*
            Темы
         */

        // светлая тема
        val lightColors = HashMap<String, Int>().apply {
            this[color_main] = COLOR_TWITCH
            this[color_bg] = 0xFFFFFFFF.toInt()
            this[color_text] = 0xFF000000.toInt()
            this[color_text2] = 0xFF666666.toInt()

            this[color_searchResult_best] = 0xFF00FF00.toInt()
            this[color_searchResult_middle] = 0xFF666666.toInt()
            this[color_searchResult_worst] = 0xFFFF0000.toInt()

            this[color_bottomNavIcon_inactive] = 0xFFAAAAAA.toInt()
            this[color_bottomNavIcon_active] = COLOR_TWITCH

            this[color_actionBar_back] = 0xFF666666.toInt()

            this[color_bigActionBar_item_bg] = 0xFFEEEEEE.toInt()

            this[color_negative] = 0xFFFF6565.toInt()

            this[color_popup_holder] = 0xFFAAAAAA.toInt()

            this[color_radio] = 0xFF777777.toInt()
        }
        // черная тема
        lateinit var darkColors: HashMap<String, Int>
        // список всех тем
        val colorsList = listOf(
            lightColors,
            darkColors,
        )
        // активная тема
        var activeColors = HashMap<String, Int>()
            private set
        private var mainColor = 0 // !

        fun color(colorKey: String) : Int
        {
            when (colorKey)
            {
                color_main -> return mainColor
                color_bottomNavIcon_active -> return mainColor
            }
            return activeColors[colorKey] ?: 0x0
        }

        fun setMainColor(color: Int)
        {
            mainColor = color
        }

        /*
            Основные цвета
         */

        // const val ...
        // ...


        /*
            Шрифты
         */

        const val tf_normal = "fonts/ps_normal.ttf"
        const val tf_italic = "fonts/ps_italic.ttf"
        const val tf_bold = "fonts/ps_bold.ttf"
        const val tf_boldItalic = "fonts/ps_boldItalic.ttf"

        fun typeface(tfKey: String) : Typeface
        {
            return Typeface.createFromAsset(ApplicationLoader.instance().assets, tfKey)
        }

        /*
            Color
         */

        // alpha: от 0 до 1
        fun alphaColor(color: Int, alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent(color, (255 * alpha).toInt())
        }
        fun alphaColor(colorKey: String, alpha: Float) : Int
        {
            return alphaColor(color(colorKey), alpha)
        }

        fun ripplizeColor(color: Int) : Int
        {
            val hsv = FloatArray(3)
            val outHsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            outHsv[0] = hsv[0]
            outHsv[1] = hsv[1]
            if (hsv[2] >= 0.5F) {
                outHsv[2] = hsv[2] - 0.05F
                return Color.HSVToColor(outHsv)
            }
            else {
                outHsv[2] = hsv[2] + 0.05F
                return Color.HSVToColor(outHsv)
            }
        }
        fun ripplizeColor(colorKey: String) : Int
        {
            return ripplizeColor(color(colorKey))
        }

        // ratio: от 0 до 1 (для 0.2 будет использовано 20% color2 и 80% color1)
        fun mixColors(color1: Int, color2: Int, ratio: Float) : Int
        {
            return ColorUtils.blendARGB(color1, color2, ratio)
        }

        /*
            Drawable
         */

        fun drawable(drawableId: Int): Drawable
        {
            return ContextCompat.getDrawable(ApplicationLoader.instance().applicationContext, drawableId)!!
        }
        fun drawable(drawableId: Int, color: Int): Drawable
        {
            return drawable(drawableId).apply {
                setTint(color)
            }
        }
        fun drawable(drawableId: Int, colorKey: String): Drawable
        {
            return drawable(drawableId, color(colorKey))
        }

        fun createCircleSelector(color: Int, radius: Int = Utils.dp(20)) : Drawable
        {
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(ripplizeColor(color))
            )
            val rippleDrawable = RippleDrawable(colorStateList, null, null).apply {
                this.radius = radius
            }
            return rippleDrawable
        }
        fun createCircleSelector(colorKey: String, radius: Int = Utils.dp(20)) : Drawable
        {
            return createCircleSelector(color(colorKey), radius)
        }

        /*
            Rect
         */

        fun createRect(color: Int, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
        {
            return createRect(
                Fill( intArrayOf(color, color) ),
                outline,
                radii
            )
        }

        fun createRect(colorKey: String, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
        {
            return createRect( color(colorKey), outline, radii )
        }

        fun createRect(fill: Fill? = null, outline: Outline? = null, radii: FloatArray? = null) : GradientDrawable
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
                    setStroke(outline.width, outline.color)
                }
                if (radii != null) {
                    cornerRadii = radiiArray
                }
            }
        }

    }

    data class Outline(val color: Int, val width: Int = Utils.dp(1))
    data class Fill(val colors: IntArray, val orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT)
}


































//