package tv.ridal.UI.Layout

import android.content.Context
import android.widget.LinearLayout
import tv.ridal.UI.Cells.CheckCell

class MultiCheckGroup(context: Context) : LinearLayout(context)
{
    private var checkCells: ArrayList<CheckCell> = ArrayList()

    init
    {
        orientation = VERTICAL
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
        for (checkedCell in checkedCells)
        {
            removeView(checkedCell)
            addView(checkedCell, it++)
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