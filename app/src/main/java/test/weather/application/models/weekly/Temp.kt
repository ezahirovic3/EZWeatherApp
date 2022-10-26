package test.weather.application.models.weekly

import com.google.gson.annotations.SerializedName

data class Temp(
    @SerializedName("min") var min: Double,
    @SerializedName("max") var max: Double
)
