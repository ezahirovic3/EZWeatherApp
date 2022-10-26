package test.weather.application.models.current

import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("country") val country: String
    )
