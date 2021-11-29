package tv.ridal.HDRezka

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(val name: String, val posterUrl: String, val type: String, val url: String) : Parcelable