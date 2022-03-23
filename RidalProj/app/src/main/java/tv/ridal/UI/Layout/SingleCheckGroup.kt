package tv.ridal.UI.Layout

import android.content.Context
import tv.ridal.UI.Cells.CheckCell

class SingleCheckGroup(context: Context) : VLinearLayout(context)
{
    private var checkCells: ArrayList<CheckCell> = ArrayList()

    init
    {

    }

    fun addCheck(text: String, onCheck: (() -> Unit)? = null)
    {
        val checkCell = CheckCell(context).apply {
            this.text = text

            setOnClickListener {
                if (this.isChecked) return@setOnClickListener

                onCheck?.invoke()
                check(this.text)
            }
        }
        addView(checkCell)

        checkCells.add(checkCell)
    }

    fun check(text: String, animated: Boolean = true)
    {
        val checkedCell = checkCells.find {
            it.isChecked
        }
        checkedCell?.setChecked(false, animated)

        val toCheck = checkCells.find {
            it.text == text
        }
        toCheck?.setChecked(true, animated)
    }

    override fun invalidate()
    {
        super.invalidate()

        checkCells.forEach {
            it.invalidate()
        }
    }

    fun moveCheckedOnTop()
    {
        val checkedCell = checkCells.find { it.isChecked }
        if ( indexOfChild(checkedCell) == 0 ) return

        val prevChecked = getChildAt(0) as CheckCell
        removeView(prevChecked)
        addView(prevChecked, checkCells.indexOf(prevChecked))

        removeView(checkedCell)
        addView(checkedCell, 0)
    }

    fun currentChecked(): String = checkCells.find {
        it.isChecked
    }!!.text

    fun size(): Int = checkCells.size
}





































//