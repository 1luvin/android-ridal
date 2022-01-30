package tv.ridal.HDRezka

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import coil.imageLoader
import coil.request.ImageRequest
import coil.transition.TransitionTarget
import tv.ridal.Application.ApplicationLoader

class Loader
{
    companion object
    {
        private val context: Context
            get() = ApplicationLoader.instance().applicationContext

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