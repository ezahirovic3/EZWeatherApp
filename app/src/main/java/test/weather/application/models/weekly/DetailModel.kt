package test.weather.application.models.weekly

import com.google.gson.annotations.SerializedName
import test.weather.application.models.weekly.DetailedWeather

data class DetailModel(
    @SerializedName("list") var list: List<DetailedWeather>
)
