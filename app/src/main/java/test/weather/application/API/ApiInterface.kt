package test.weather.application.API


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import test.weather.application.models.weekly.DetailModel
import test.weather.application.models.current.ModelClass

interface ApiInterface {

    @GET("weather")
    fun getCurrentWeather (
        @Query("lat") latitude:String,
        @Query("lon") longitude:String,
        @Query("appid") api_key:String
    ): Call<ModelClass>


    @GET("forecast/daily")
    fun getSevenDayWeather (
        @Query("lat") latitude:String,
        @Query("lon") longitude:String,
        @Query("cnt") count:Int,
        @Query("appid") api_key:String
    ): Call<DetailModel>
}