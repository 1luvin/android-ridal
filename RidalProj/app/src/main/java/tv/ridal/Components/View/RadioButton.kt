package tv.ridal.Components.View

import android.animation.ObjectAnimator
import android.graphics.*
import android.view.View
import androidx.annotation.Keep
import tv.ridal.ApplicationActivity
import tv.ridal.Utils.Utils


class RadioButton : View(ApplicationActivity.instance())
{
    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null
    private var paint: Paint
    private var eraser: Paint
    private var checkedPaint: Paint

    var color = 0
        set(value) {
            if (value == color) return
            field = value
            invalidate()
        }

    var checkedColor = 0
        set(value) {
            if (value == checkedColor) return
            field = value
            invalidate()
        }

    @Keep
    var progress: Float = 0F
        set(value) {
            if (value == progress) return
            field = value
            invalidate()
        }

    private var checkAnimator: ObjectAnimator? = null

    private var attachedToWindow = false
    var isChecked = false
        private set

    var size: Int = Utils.dp(16)
        set(value) {
            if (value == size) return
            field = value
            invalidate()
        }

    init
    {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = Utils.dp(2F)
            style = Paint.Style.STROKE
        }
        checkedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        eraser = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        try {
            bitmap = Bitmap.createBitmap(Utils.dp(size), Utils.dp(size), Bitmap.Config.ARGB_8888)
            bitmapCanvas = Canvas(bitmap!!)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
        }
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator =
            ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1F else 0F)
        checkAnimator!!.duration = 200
        checkAnimator!!.start()
    }

    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    protected override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setChecked(checked: Boolean) {
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            progress = if (checked) 1F else 0F
        }
    }

    protected override fun onDraw(canvas: Canvas) {
        if (bitmap == null || bitmap!!.width != measuredWidth) {
            if (bitmap != null) {
                bitmap!!.recycle()
                bitmap = null
            }
            try {
                bitmap = Bitmap.createBitmap(
                    measuredWidth,
                    measuredHeight,
                    Bitmap.Config.ARGB_8888
                )
                bitmapCanvas = Canvas(bitmap!!)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        val circleProgress: Float
        var innerRad: Float
        if (progress <= 0.5F) {
            paint.color = color
            checkedPaint.color = color
            circleProgress = progress / 0.5F
        } else {
            circleProgress = 2.0F - progress / 0.5F
            val r1: Int = Color.red(color)
            val rD = ((Color.red(checkedColor) - r1) * (1F - circleProgress)).toInt()
            val g1: Int = Color.green(color)
            val gD = ((Color.green(checkedColor) - g1) * (1F - circleProgress)).toInt()
            val b1: Int = Color.blue(color)
            val bD = ((Color.blue(checkedColor) - b1) * (1F - circleProgress)).toInt()
            val c: Int = Color.rgb(r1 + rD, g1 + gD, b1 + bD)
            paint.color = c
            checkedPaint.color = c
        }
        if (bitmap != null) {
            bitmap!!.eraseColor(0)
            val rad: Float = size / 2 - (1 + circleProgress) * Utils.density
            bitmapCanvas!!.drawCircle(measuredWidth / 2F, measuredHeight / 2F, rad, paint)
            if (progress <= 0.5F) {
                bitmapCanvas!!.drawCircle(
                    measuredWidth / 2F, measuredHeight / 2F,
                    rad - Utils.dp(1), checkedPaint
                )
                bitmapCanvas!!.drawCircle(
                    measuredWidth / 2F,
                    measuredHeight / 2F,
                    (rad - Utils.dp(1)) * (1F - circleProgress),
                    eraser
                )
            } else {
                bitmapCanvas!!.drawCircle(
                    measuredWidth / 2F,
                    measuredHeight / 2F,
                    size / 4 + (rad - Utils.dp(1) - size / 4) * circleProgress,
                    checkedPaint
                )
            }
            canvas.drawBitmap(bitmap!!, 0F, 0F, null)
        }
    }
}





































//