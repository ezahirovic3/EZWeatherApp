package test.weather.application.models.current

import com.google.gson.annotations.SerializedName
import test.weather.application.models.Weather

data class ModelClass (
    @SerializedName("weather") val weather:List<Weather>,
    @SerializedName( "main") val main: Main,
    @SerializedName( "wind") val wind: Wind,
    @SerializedName( "sys") val sys: Sys,
    @SerializedName( "name") val name: String,
)



