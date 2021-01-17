package com.oth.archeology.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

enum class IMAGE{
    FIRST,SECOND,THIRD,FOURTH
}

@Parcelize
@Entity
data class SiteModel(@PrimaryKey(autoGenerate = true)
                     var id: Long = 0,
                     var fbId: String = "",
                     var title: String = "",
                     var description: String = "",
                     @Embedded var images: Images = Images(),
                     @Embedded var location: Location = Location(),
                     var date: Date = Date(1900,0,1),
                     var notes: String = "",
                     var visited: Boolean = false,
                     var favourite: Boolean = false,
                     var rating: Float = 2.5f) : Parcelable

//TODO make all changes to the whole model lol

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

@Parcelize
data class Images(var first: String = "",
                  var second: String = "",
                  var third: String = "",
                  var fourth: String = ""): Parcelable

@Parcelize
data class ImagePath(var path: String = "",): Parcelable