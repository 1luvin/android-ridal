package tv.ridal.ui.layout

import android.content.Context
import tv.ridal.ui.cell.CheckCell

class MultiCheckGroup(context: Context) : VLinearLayout(context)
{
    private var checkCells: ArrayList<CheckCell> = ArrayList()

    init
    {

    }

    fun addCheck(text: String)
    {
        val checkCell = CheckCell(context).apply {
            this.text = text

            setOnClickListener {
                check(this.text)
            }
        }
        addView(checkCell)

        checkCells.add(checkCell)
    }

    fun check(text: String, animated: Boolean = true)
    {
        val toCheck = checkCells.find {
            it.text == text
        }
        if (toCheck == null)
        {
            println("CheckCell with text = $text not found.")
            return
        }
        toCheck.setChecked( ! toCheck.isChecked, animated )
    }

    fun moveCheckedOnTop()
    {
        val checkedCells = checkCells.filter {
            it.isChecked
        }
        var it = 0
        for (cell in checkedCells)
        {
            removeView(cell)
            addView(cell, it++)
        }
    }

    fun currentChecked() : List<String>
    {
        return checkCells.filter {
            it.isChecked
        }.map {
            it.text
        }
    }

    fun size(): Int = checkCells.size
}