package test.weather.application.models.weekly

import com.google.gson.annotations.SerializedName
import test.weather.application.models.Weather

data class DetailedWeather (
    @SerializedName("weather") val weather:List<Weather>,
    @SerializedName("temp") var temp: Temp
)

