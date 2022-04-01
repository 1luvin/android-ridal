package tv.ridal.HDRezka

import android.content.Context
import android.graphics.drawable.Drawable
import coil.imageLoader
import coil.request.ImageRequest
import tv.ridal.Application.App

class Loader
{
    companion object
    {
        private val context: Context
            get() = App.instance().applicationContext

        fun loadImage(imageUrl: CharSequence, onLoaded: (d: Drawable?) -> Unit)
        {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .target {result ->
                    onLoaded.invoke(result)
                }
                .build()

            context.imageLoader.enqueue(request)
        }

    }
}





































//