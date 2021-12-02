package tv.ridal.Application

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.util.StateSet
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import tv.ridal.Utils.Utils
import kotlin.math.ceil

class Theme
{
    companion object
    {
        const val COLOR_TRANSPARENT = 0x00000000

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
        }
        // черная тема
        private val darkColors = HashMap<String, Int>().apply {
            this[color_main] = 0xFF00B2FF.toInt()
            this[color_bg] = 0xFF000000.toInt()
            this[color_text] = 0xF
            this[color_text2] = 0xFFAAAAAA.toInt()
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
            Drawable
         */

        fun drawable(drawableId: Int): Drawable
        {
            return ContextCompat.getDrawable(ApplicationLoader.instance().applicationContext, drawableId)!!
        }

        // alpha: от 0 до 1
        fun alphaColor(color: Int, alpha: Float) : Int
        {
            return ColorUtils.setAlphaComponent(color, (255 * alpha).toInt())
        }
        fun alphaColor(colorKey: String, alpha: Float) : Int
        {
            return alphaColor(color(colorKey), alpha)
        }

        // ratio: от 0 до 1 (для 0.2 будет использовано 20% color2 и 80% color1)
        fun mixColors(color1: Int, color2: Int, ratio: Float) : Int
        {
            return ColorUtils.blendARGB(color1, color2, ratio)
        }



        fun createCircleSelector(color: Int, radius: Int = Utils.dp(20)) : Drawable
        {
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(alphaColor(color, 0.1F))
            )
            val rippleDrawable = RippleDrawable(colorStateList, null, null).apply {
                this.radius = radius
            }
            return rippleDrawable
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

        fun createRectSelector(color: Int, radii: FloatArray? = null, fillAfter: Boolean = false) : Drawable
        {
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(alphaColor(color, 0.33F))
            )

            val radiiArray = FloatArray(8)
            if (radii != null) {
                for (i in 0 until radii.size)
                {
                    radiiArray[i*2] = radii[i]
                    radiiArray[i*2 + 1] = radii[i]
                }
            }

            val defDrawable = ShapeDrawable(RoundRectShape(radiiArray, null, null))
            if (fillAfter) {
                defDrawable.paint.color = color
            } else {
                defDrawable.paint.color = COLOR_TRANSPARENT
            }

            val rippleDrawable = ShapeDrawable(RoundRectShape(radiiArray, null, null))

            return RippleDrawable(colorStateList, defDrawable, rippleDrawable)
        }

        fun createOutlinedRect(color: Int, radii: FloatArray? = null) : Drawable
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
                this.setColor(Color.TRANSPARENT)
                cornerRadii = radiiArray
                setStroke(Utils.dp(2), color)
            }
        }

    }
}


































//