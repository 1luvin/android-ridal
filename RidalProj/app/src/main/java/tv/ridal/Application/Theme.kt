package tv.ridal.Application

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.util.StateSet
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowInsetsControllerCompat
import tv.ridal.ApplicationActivity
import tv.ridal.Utils.Utils
import kotlin.math.ceil

class Theme
{
    companion object
    {
        const val COLOR_TRANSPARENT = 0x00000000
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_BLACK = 0xFF000000.toInt()

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
        private val lightColors = HashMap<String, Int>().apply {
            this[color_main] = 0xFF00B2FF.toInt()
            this[color_bg] = 0xFFFFFFFF.toInt()
            this[color_text] = 0xFF000000.toInt()
            this[color_text2] = 0xFF666666.toInt()

            this[color_searchResult_best] = 0xFF00FF00.toInt()
            this[color_searchResult_middle] = 0xFF666666.toInt()
            this[color_searchResult_worst] = 0xFFFF0000.toInt()

            this[color_bottomNavIcon_inactive] = 0xFFAAAAAA.toInt()
            this[color_bottomNavIcon_active] = 0xFF00B2FF.toInt()

            this[color_actionBar_back] = 0xFF666666.toInt()

            this[color_bigActionBar_item_bg] = 0xFFEEEEEE.toInt()

            this[color_negative] = 0xFFFF6565.toInt()

            this[color_popup_holder] = 0xFFAAAAAA.toInt()

            this[color_radio] = 0xFF777777.toInt()
        }
        // черная тема
        private val darkColors = HashMap<String, Int>().apply {
            this[color_main] = 0xFF00B2FF.toInt()
            this[color_bg] = 0xFF000000.toInt()
            this[color_text] = 0xFFFFFFFF.toInt()
            this[color_text2] = 0xFFAAAAAA.toInt()

            this[color_searchResult_best] = 0xFF00FF00.toInt()
            this[color_searchResult_middle] = 0xFFAAAAAA.toInt()
            this[color_searchResult_worst] = 0xFFFF0000.toInt()

            this[color_bottomNavIcon_inactive] = 0xFFAAAAAA.toInt()
            this[color_bottomNavIcon_active] = 0xFF00B2FF.toInt()

            this[color_actionBar_back] = 0xFFAAAAAA.toInt()

            this[color_bigActionBar_item_bg] = 0xFFEEEEEE.toInt()

            this[color_negative] = 0xFFFF6565.toInt()

            this[color_popup_holder] = 0xFFAAAAAA.toInt()
        }
        // список всех тем
        private val colorsList = listOf(
            lightColors,
            darkColors,
        )
        // активная тема
        private var activeColors = HashMap<String, Int>()

        fun color(colorKey: String) : Int
        {
            return activeColors[colorKey] ?: 0x0
        }

        /*
            Основные цвета
         */

        // const val ...
        // ...

        fun setMainColor(colorKey: String)
        {
            for (colors in colorsList)
            {
                colors[color_main] = color(colorKey)
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
                outHsv[2] = hsv[2] - 0.24F
                return Color.HSVToColor(outHsv)
            }
            else {
                outHsv[2] = hsv[2] + 0.12F
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

        fun createRect(color: Int, radii: FloatArray? = null) : Drawable
        {
            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in radii.indices)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            return ShapeDrawable(RoundRectShape(radiiArray, null, null)).apply {
                paint.color = color
            }
        }
        fun createRect(colorKey: String, radii: FloatArray? = null) : Drawable
        {
            return createRect(color(colorKey), radii)
        }

        fun createRectSelector(color: Int, radii: FloatArray? = null, fillAfter: Boolean = false) : Drawable
        {
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(ripplizeColor(color))
            )

            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in 0 until radii.size)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            val defDrawable = Theme.createRect(
                if (fillAfter) color else COLOR_TRANSPARENT,
                radii
            )

            val rippleDrawable = ShapeDrawable(RoundRectShape(radiiArray, null, null))

            return RippleDrawable(colorStateList, defDrawable, rippleDrawable)
        }
        fun createRectSelector(colorKey: String, radii: FloatArray? = null, fillAfter: Boolean = false) : Drawable
        {
            return createRectSelector(color(colorKey), radii, fillAfter)
        }


        fun createOutlinedRect(fillColor: Int, outline: Outline, radii: FloatArray? = null) : Drawable
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
                setColor(fillColor)
                cornerRadii = radiiArray
                setStroke(outline.width, outline.color)
            }
        }

        fun createOutlinedRectSelector(fillColor: Int, outline: Outline, radii: FloatArray? = null) : Drawable
        {
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(ripplizeColor(fillColor))
            )

            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in radii.indices)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            val defDrawable = Theme.createOutlinedRect(
                fillColor,
                outline,
                radii
            )

            val rippleDrawable = ShapeDrawable(RoundRectShape(radiiArray, null, null))

            return RippleDrawable(colorStateList, defDrawable, rippleDrawable)
        }

    }

    data class Outline(val color: Int, val width: Int = Utils.dp(1))
}


































//