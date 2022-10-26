package test.weather.application

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import test.weather.application.API.ApiConfig
import test.weather.application.models.weekly.DetailModel
import test.weather.application.models.current.ModelClass
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var seeAll: TextView
    private lateinit var longitude: String
    private lateinit var latitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        seeAll = findViewById(R.id.seeAll)
        seeAll.setOnClickListener{
            val intent = Intent(this, DetailedForecast::class.java)
            intent.putExtra("longitude",longitude)
            intent.putExtra("latitude",latitude)
            intent.putExtra("apiKey", Api_Key)
            startActivity(intent)
        }
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESSS_LOCATION =100
        private const val Api_Key = "9af4ad5be293c3949a2b7c2006c1dd9e"
    }


    //current weather
    private fun getCurrentWeather(latitude: String, longitude: String) {
        ApiConfig.getApiInterface()?.getCurrentWeather(latitude,longitude,Api_Key)?.enqueue(object :
            Callback<ModelClass>{
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                if(response.isSuccessful){
                    setCurrentWeatherOnViews(response.body())
                }
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                Toast.makeText(applicationContext,"Error ${t.message}",Toast.LENGTH_LONG).show()
            }

        })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setCurrentWeatherOnViews(body: ModelClass?) {
        if (body != null) {
            val sdf = SimpleDateFormat("EEEE, dd LLL")
            val currentDate=sdf.format(Date())
            findViewById<TextView>(R.id.city).text = body.name
            findViewById<TextView>(R.id.country).text = ", ${body.sys.country}"
            findViewById<TextView>(R.id.weather).text = body.weather[0].main
            findViewById<TextView>(R.id.date).text = currentDate
            findViewById<TextView>(R.id.temperature).text = (kelvinToCelsius(body.main.temp)).toString()+"°"
            findViewById<TextView>(R.id.wind).text = (body.wind.speed*3.6).toBigDecimal().setScale(1,RoundingMode.UP).toString()+" km/h"
            findViewById<TextView>(R.id.feelsLike).text = (kelvinToCelsius(body.main.feels_like)).toString()+"°"
            findViewById<TextView>(R.id.humidity).text = body.main.humidity.toString()+"%"
            findViewById<TextView>(R.id.pressure).text = body.main.pressure.toString()+"mbar"
            weatherIcon(body.weather[0].id)
        }
    }


    //weekly weather
    private fun getDetailedWeather(latitude: String, longitude: String) {
        ApiConfig.getApiInterface()?.getSevenDayWeather(latitude,longitude,7, Api_Key)?.enqueue(object :
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
            val sevenDayRecycler = findViewById<RecyclerView>(R.id.sevenDayMini)
            sevenDayRecycler.layoutManager = LinearLayoutManager(
                null,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            val sevenDayRecyclerAdapter = SevenDayMiniRecyclerAdapter(body)
            sevenDayRecycler.adapter=sevenDayRecyclerAdapter
        }
    }


    //extra
    fun kelvinToCelsius(temp: Double): Int{
        var intTemp = temp
        intTemp = intTemp.minus(273.15)
        return intTemp.toBigDecimal().setScale(1,RoundingMode.UP).toInt()
    }

    //ikone su otprilike jer ima mnogo za kodirat za precizan prikaz
    private fun weatherIcon(icon: Int){
        if(icon in 200..232){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._11d)}
        if(icon in 300..321){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._9d)}
        if(icon in 500..531){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._10d)}
        if(icon in 600..622){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._13d)}
        if(icon in 701..781){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._50d)}
        if(icon in 801..804){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._4d)}
        if(icon == 800){findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable._1d)}
    }


    //location
    private fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                        task -> val location: Location? = task.result
                    if(location != null){
                        latitude=location.latitude.toString()
                        longitude=location.longitude.toString()
                        getCurrentWeather(location.latitude.toString(),location.longitude.toString())
                        getDetailedWeather(location.latitude.toString(),location.longitude.toString())
                    }
                }
            }
            else{
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else{
            requestPermission()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager= getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESSS_LOCATION
        )
    }

    private fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESSS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
        }
    }
}