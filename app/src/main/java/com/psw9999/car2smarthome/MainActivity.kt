package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.psw9999.car2smarthome.Adapter.FragmentAdapter
import com.psw9999.car2smarthome.SecondFragment.Companion.modeDatas
import com.psw9999.car2smarthome.ThirdFragment.Companion.scheduleDatas
import com.psw9999.car2smarthome.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    // 전역 변수 생성
    companion object {
        lateinit var weather: Weather
        lateinit var applianceStatus: ApplianceStatus
        lateinit var userName : String
        lateinit var guideText : String
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            val fragment = MainFragment()
            fragment.arguments = Bundle().apply {
                putString("userName","$userName")
                putString("icon",weather.icon!!.slice(listOf(0,1)))
                putString("temp",weather.temp)
                putString("weather", weather.description)
                putString("airLevel", weather.air_level)
            }

            val fragmentList = listOf(fragment, SecondFragment(), ThirdFragment(), AlarmFragment())

            // 어댑터 생성하고 앞에서 생성한 프래그먼트의 목록을 저장함. 어댑터의 첫번째 파라미터에는 항상 SupportFragmentManger를 사용
            val adapter = FragmentAdapter(this)
            adapter.fragmentList = fragmentList
            binding.container.adapter = adapter

            var tabImageView = listOf<Int>(R.drawable.home,R.drawable.mode,R.drawable.event,R.drawable.bell)
            TabLayoutMediator(binding.tabLayout, binding.container) { tab, position ->
                tab.setIcon(tabImageView[position])
            }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        modeDatas.removeAll(modeDatas)
        scheduleDatas.removeAll(scheduleDatas)

    }
}



// data class : 데이터 보관 목적으로 만든 클래스
// kotlin의 data class는 생성자, getter&setter 심지어 canonical methods까지 알아서 생성해준다.
@IgnoreExtraProperties
data class Weather(
    var air_level : String? = "",
    var description: String? = "",
    var icon : String? = "",
    var temp : String? = "",
    var update : String? = "",
    var humidity : Float,
    var temperature : Float
) {

}

// data class : 데이터 보관 목적으로 만든 클래스
// kotlin의 data class는 생성자, getter&setter 심지어 canonical methods까지 알아서 생성해준다.
// 데이터 오수신이나 미수신시 잘못된 명령을 내릴 수 있으므로 default 값은 0 (OFF)로 설정한다.
@IgnoreExtraProperties
data class ApplianceStatus(
    var mode : Int? = 0,
    var airconEnabled : Int = 0,
    var windPower : Int = 1,
    var lightEnabled : Int = 0,
    var lightBrightness : Int = 1,
    var lightColor : Int = 0,
    var lightMod : Int = 0,
    var windowStatus : Int = 0,
    var gasValveStatus : Int = 0,
) {

}