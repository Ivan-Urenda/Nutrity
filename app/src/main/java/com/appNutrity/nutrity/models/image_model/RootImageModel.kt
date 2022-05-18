package com.appNutrity.nutrity.models.image_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RootImageModel(
    @SerializedName("LARGE")
    @Expose()
    private var largeImage: LargeImage,

    @SerializedName("SMALL")
    @Expose()
    private var smallImage: SmallImage,

    @SerializedName("THUMBNAIL")
    @Expose()
    private var thumbNail: ThumbNail,

    @SerializedName("REGULAR")
    @Expose()
    private var regularImage: RegularImage

)