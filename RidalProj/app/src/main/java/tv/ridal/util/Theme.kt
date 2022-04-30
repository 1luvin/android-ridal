package tv.ridal.util

import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.*
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import tv.ridal.App

class Theme
{
    companion object
    {
        /*
            Константы
         */

        const val COLOR_TRANSPARENT = 0x00000000
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_BLACK = 0xFF000000.toInt()

        /*
            Основные цвета (те, которые пользователь может выбрать)
         */

        const val COLOR_TWITCH = 0xFF7C24FF.toInt()
        const val COLOR_LIGHT_CHERRY = 0xFFFF005F.toInt()
        const val COLOR_TELEGRAM = 0xFF00B2FF.toInt()
        const val COLOR_SOFT_BLUE = 0xFF4D00FF.toInt()
        const val COLOR_SOFT_GREEN = 0xFF54EA54.toInt()
        const val COLOR_ORANGE = 0xFFFA8700.toInt()
        const val COLOR_PINK = 0xFFFF00F5.toInt()

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
//            var colorBg = 0xFF1A1640.toInt()
//            var colorText = 0xFFFFFFFF.toInt()
//            var colorText2 = mixColors(colorBg, colorText, 0.7F)
//            var colorBottomNavIconInactive = mixColors(colorBg, 0xFFFFFFFF.toInt(), 0.5F)
//            var colorSearchResultBest = 0xFF00FF00.toInt()
//            var colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
//            var colorSearchResultWorst = 0xFFFF0000.toInt()
//            var colorActionBarBack = mixColors(colorBg, 0xFFFFFFFF.toInt(), 0.7F)

            var colorBg = 0xFF000000.toInt()
            var colorText = 0xFFFFFFFF.toInt()
            var colorText2 = mixColors(colorBg, colorText, 0.7F)
            var colorBottomNavIconInactive = mixColors(colorBg, 0xFFFFFFFF.toInt(), 0.5F)
            var colorSearchResultBest = 0xFF00FF00.toInt()
            var colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
            var colorSearchResultWorst = 0xFFFF0000.toInt()
            var colorActionBarBack = mixColors(colorBg, 0xFFFFFFFF.toInt(), 0.7F)

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

                put(color_negative, 0xFFFF6565.toInt())
                put(color_popup_holder, 0xFFAAAAAA.toInt())
                put(color_radio, 0xFFBBBBBB.toInt())
            }

            colorBg = COLOR_WHITE
            colorText = 0xFF000000.toInt()
            colorText2 = mixColors(colorBg, colorText, 0.7F)
            colorBottomNavIconInactive = mixColors(colorBg, 0xFF000000.toInt(), 0.5F)
            colorSearchResultMiddle = mixColors(colorBg, colorText, 0.5F)
            colorActionBarBack = mixColors(colorBg, 0xFF000000.toInt(), 0.7F)

            lightColors = HashMap<String, Int>().apply {
                put(color_bg, colorBg)

                put(color_text, colorText)
                put(color_text2, colorText2)

                put(color_bottomNavBg, colorBg)
                put(color_bottomNavIcon_inactive, colorBottomNavIconInactive)
                put(color_bottomNavIcon_active, 0xFF000000.toInt())

                put(color_searchResult_best, colorSearchResultBest)
                put(color_searchResult_middle, colorSearchResultMiddle)
                put(color_searchResult_worst, colorSearchResultWorst)

                put(color_actionBar_bg, colorBg)
                put(color_actionBar_back, colorActionBarBack)
                put(color_actionBar_menuItem, 0xFF000000.toInt())

                put(color_negative, 0xFFFF6565.toInt())
                put(color_popup_holder, 0xFF666666.toInt())
                put(color_radio, 0xFF333333.toInt())
            }
        }

        const val FOLLOW_SYSTEM = -1
        const val LIGHT: Int = 0
        const val DARK: Int = 1

        fun isDark() : Boolean
        {
            return activeColors == darkColors
        }

        fun initTheme()
        {
            // Инициализация темы
            val pref = App.instance().settingsPref
            if ( ! pref.contains(theme) )
            {
                pref.edit()
                    .putInt(theme, FOLLOW_SYSTEM)
                    .apply()
            }
            val t = pref.getInt(theme, FOLLOW_SYSTEM)
            setTheme(t)
            // Инициализация основного цвета
            if ( ! pref.contains(color_main) )
            {
                pref.edit()
                    .putInt(color_main, COLOR_TWITCH)
                    .apply()
            }
            mainColor = pref.getInt(color_main, COLOR_TWITCH)
        }

        fun setTheme(themeId: Int)
        {
            currentId = themeId

            if (themeId == FOLLOW_SYSTEM)
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
                activeColors = colorsList[themeId]
            }
        }

        fun update()
        {
            val editor = App.instance().settingsPref.edit()
            editor.apply {
                putInt(theme, currentId)
                putInt(color_main, mainColor)
            }
            editor.apply()
        }

        var currentId: Int = 0 // !

        /*
            Ключи для тем
         */

        const val theme = "theme"

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

        const val color_bottomNavBg = "color_bottomNavBg"
        const val color_bottomNavIcon_inactive = "color_bottomNavIcon_inactive"
        const val color_bottomNavIcon_active = "color_bottomNavIcon_active"

        const val color_actionBar_bg = "color_actionBar_bg"
        const val color_actionBar_back = "color_actionBar_back"
        const val color_actionBar_menuItem = "color_actionBar_menuItem"

        const val color_negative = "color_negative"

        const val color_popup_holder = "color_popup_holder"

        const val color_radio = "color_radio"

        /*
            Цвета
         */

        var mainColor = 0 // !

        // Светлые цвета
        lateinit var lightColors: HashMap<String, Int>
            private set
        // Темные цвета
        lateinit var darkColors: HashMap<String, Int>
            private set
        // Активные цвета
        var activeColors = HashMap<String, Int>()
            private set
        // Список всех цветов
        val colorsList = listOf(
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
            Шрифты
         */

        const val tf_normal = "fonts/ps_normal.ttf"
        const val tf_italic = "fonts/ps_italic.ttf"
        const val tf_bold = "fonts/ps_bold.ttf"
        const val tf_boldItalic = "fonts/ps_boldItalic.ttf"

        fun typeface(tfKey: String) : Typeface
        {
            return Typeface.createFromAsset(App.instance().assets, tfKey)
        }

        /*
            Функции для цветов
         */

        // Осветлить цвет
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
        // Затемнить цвет
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
        // Осветляет или затемняет цвет
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
        // Сделать прозрачнее
        fun alphaColor(color: Int, alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent(color, (255 * alpha).toInt())
        }
        fun alphaColor(colorKey: String, alpha: Float) : Int
        {
            return alphaColor(color(colorKey), alpha)
        }
        // Смешать цвета
        fun mixColors(color1: Int, color2: Int, ratio: Float) : Int
        {
            return ColorUtils.blendARGB(color1, color2, ratio)
        }

        /*
            Drawable
         */

        fun drawable(drawableId: Int): Drawable
        {
            return ContextCompat.getDrawable(App.instance().applicationContext, drawableId)!!
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
            return rect( color(colorKey), outline, radii )
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
                    setStroke(outline.width, outline.color)
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

    data class Outline(val color: Int, val width: Int = Utils.dp(1))
    data class Fill(val colors: IntArray, val orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT)
}


































//