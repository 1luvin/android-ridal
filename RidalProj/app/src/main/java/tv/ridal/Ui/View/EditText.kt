package tv.ridal.Ui.View

import android.content.Context
import android.view.KeyEvent
import android.widget.EditText

class EditText(context: Context) : EditText(context)
{
    var keyImeChangeListener: KeyImeChange? = null

    interface KeyImeChange
    {
        fun onKeyIme(keyCode: Int, event: KeyEvent)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean
    {
        keyImeChangeListener?.onKeyIme(keyCode, event)
        return false
    }

}