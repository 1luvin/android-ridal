package tv.ridal.ui.recyclerview

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class EdgeColorEffect(private val Color: Int) : RecyclerView.EdgeEffectFactory()
{
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return EdgeEffect( view.context ).apply { color = Color }
    }
}