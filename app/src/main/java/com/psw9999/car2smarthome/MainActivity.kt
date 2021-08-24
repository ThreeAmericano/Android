package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.json.JSONObject
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    // 전역 변수 생성
    companion object {
        lateinit var weather: Weather
        lateinit var applianceStatus: ApplianceStatus
    }



    private lateinit var userName : String

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 리스너 선언 및 초기화
        val mValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                  val post = dataSnapshot.child("status").value.toString().chunked(1)
//                Log.d("DataChange","$post")
                applianceStatus.airconEnabled =  post[0].toInt()
                applianceStatus.windPower =  post[1].toInt()
                applianceStatus.lightEnabled =  post[2].toInt()
                applianceStatus.lightBrightness =  post[3].toInt()
                applianceStatus.lightColor =  post[4].toInt()
                applianceStatus.lightMod = post[5].toInt()
                applianceStatus.windowStatus = post[6].toInt()
                applianceStatus.gasValveStatus = post[7].toInt()
                Log.d("DataChange", "${applianceStatus}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        // 리스너에 이벤트를 포함시킴.
        realtimeFirebase.child("smarthome").addValueEventListener(mValueEventListener)

        userName = intent.getStringExtra("userName")!!

            val fragment = MainFragment()
            fragment.arguments = Bundle().apply {
                putString("userName","$userName")
                putString("icon",weather.icon)
                putString("temp",weather.temp)
                putString("weather", weather.description)
                putString("airLevel", weather.air_level)
                //putInt("mode", applianceStatus.mode)
            }

            val fragmentList = listOf(fragment, SecondFragment(), ThirdFragment())

            // 어댑터 생성하고 앞에서 생성한 프래그먼트의 목록을 저장함. 어댑터의 첫번째 파라미터에는 항상 SupportFragmentManger를 사용
            val adapter = FragmentAdapter(this)
            adapter.fragmentList = fragmentList
            binding.container.adapter = adapter

            val tapTitles = listOf<String>("A","B","C")
            TabLayoutMediator(binding.tabLayout, binding.container) { tab, position ->
                tab.text = tapTitles[position]
            }.attach()
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
    var update : String? = ""
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
    var gasValveStatus : Int = 0
) {

}