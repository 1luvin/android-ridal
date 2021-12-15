package tv.ridal.Components.Popup

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper

class PopupFrame(context: Context) : FrameLayout(context)
{

    private var dismissListener: (() -> Unit)? = null
    fun onDismiss(l: () -> Unit)
    {
        dismissListener = l
    }

    private var animator: ObjectAnimator
    private var animatorDismissListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator) {
            where?.removeView(this@PopupFrame)
            where = null

            animation.removeListener(this)
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
    }

    init
    {
        isClickable = true
        setOnClickListener {
            dismiss()
        }

        background = Theme.createRect(Theme.alphaColor(Theme.COLOR_BLACK, 0.3F))

        animator = ObjectAnimator().apply {
            duration = 200
            addUpdateListener {
                val currAlpha = it.animatedValue as Float
                this@PopupFrame.alpha = currAlpha
            }
        }
    }

    private var where: ViewGroup? = null

    var isShowing: Boolean = false
        private set
        get() = where != null

    fun show(where: ViewGroup)
    {
        where.addView(this, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))

        animator.apply {
            //removeListener(animatorDismissListener)

            setFloatValues(0F, 1F)
            start()
        }

        this.where = where
    }

    fun dismiss()
    {
        animator.apply {
            addListener(animatorDismissListener)

            setFloatValues(1F, 0F)
            start()
        }

        dismissListener?.invoke()
    }

}





































//