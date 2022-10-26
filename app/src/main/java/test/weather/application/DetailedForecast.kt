package test.weather.application

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import test.weather.application.API.ApiConfig
import test.weather.application.models.weekly.DetailModel

class DetailedForecast : AppCompatActivity() {

    private lateinit var longitude: String
    private lateinit var latitude: String
    private lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_forecast)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        if (extras != null) {
            longitude = extras.getString("longitude")!!
            latitude = extras.getString("latitude")!!
            apiKey = extras.getString("apiKey")!!

            getDetailedWeather(latitude, longitude, apiKey)
        }

    }

    private fun getDetailedWeather(latitude: String, longitude: String, apiKey: String) {
        ApiConfig.getApiInterface()?.getSevenDayWeather(latitude,longitude,8, apiKey)?.enqueue(object :
            Callback<DetailModel> {
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                if(response.isSuccessful){
                    setWeeklyWeatherOnDetailViews(response.body())
                }
            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                Toast.makeText(applicationContext,"Error ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun setWeeklyWeatherOnDetailViews(body: DetailModel?) {
        if (body != null){
            val sevenDayRecycler = findViewById<RecyclerView>(R.id.sevenDay)
            sevenDayRecycler.layoutManager = LinearLayoutManager(
                null,
                LinearLayoutManager.VERTICAL,
                false
            )
            val sevenDayRecyclerAdapter = SevenDayRecyclerAdapter(body)
            sevenDayRecycler.adapter=sevenDayRecyclerAdapter
        }
    }
}