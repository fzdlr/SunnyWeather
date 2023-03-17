package com.example.sunnyweather.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.*
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.example.sunnyweather.logic.network.WeatherService
import com.example.sunnyweather.ui.place.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    private val forecastLayout: LinearLayout by lazy { findViewById(R.id.forecastLayout) }
    private val ultravioletText : TextView by lazy { findViewById(R.id.ultravioletText) }
    private val carWashingText : TextView by lazy { findViewById(R.id.carWashingText) }
    private val coldRiskText : TextView by lazy { findViewById(R.id.coldRiskText) }
    private val dressingText : TextView by lazy { findViewById(R.id.dressingText) }
    private val weatherLayout : ScrollView by lazy { findViewById(R.id.weatherLayout) }
    private val swipeRefreshLayout : SwipeRefreshLayout by lazy { findViewById(R.id.swipeRefresh) }
    private val button : Button by lazy { findViewById(R.id.navBtn) }
    val drawerLayout : DrawerLayout by lazy { findViewById(R.id.drawerLayout) }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val controller = window.decorView.windowInsetsController
        controller?.hide(WindowInsets.Type.statusBars())
        if(viewModel.LocationLng.isEmpty())
        {
            viewModel.LocationLng = intent.getStringExtra("location_lng")?:""
        }
        if(viewModel.LocationLat.isEmpty())
        {
            viewModel.LocationLat = intent.getStringExtra("location_lat")?:""
        }
        if(viewModel.placeName.isEmpty())
        {
            viewModel.placeName = intent.getStringExtra("place_name")?:""
        }
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500)
        refreshWeather()
        swipeRefreshLayout.setOnRefreshListener {
            refreshWeather()
        }
        button.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener
        {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })
    }

  fun refreshWeather()
    {
        viewModel.refreshWeather(viewModel.LocationLng,viewModel.LocationLat)
        swipeRefreshLayout.isRefreshing = true
    }
    private fun showWeatherInfo(weather: Weather)
    {
        //now.xml

        val placeName = findViewById<TextView>(R.id.placeName)
        val currentTemp = findViewById<TextView>(R.id.currentTemp)
        val currentSky = findViewById<TextView>(R.id.currentSky)
        val currentAQI = findViewById<TextView>(R.id.currentAQI)
        val nowLayout = findViewById<RelativeLayout>(R.id.nowLayout)

        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()}℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
       currentAQI.text = currentPM25Text
       nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //forecast.xml
       forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days )
        {
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val sky = daily.skycon[i]
            val temperature = daily.temperature[i]
            val simpleformat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
            dateInfo.text = simpleformat.format(sky.date)
            val skycon = getSky(sky.value)
            skyInfo.text = skycon.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            skyIcon.setImageResource(skycon.icon)
            forecastLayout.addView(view)
        }
        //lifeindex.xml
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}