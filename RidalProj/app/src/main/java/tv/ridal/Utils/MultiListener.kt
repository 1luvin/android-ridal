package tv.ridal.Utils

class MultiListener
{
    private var listeners = ArrayList<() -> Unit>()

    fun addListener(listener: () -> Unit)
    {
        listeners.add(listener)
    }

    fun invokeAll()
    {
        for (listener in listeners)
        {
            listener.invoke()
        }
    }
}