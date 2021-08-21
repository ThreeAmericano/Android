package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.json.JSONObject
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private var userName : String? = null

    private lateinit var database : DatabaseReference

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        database = Firebase.database.reference

        userName = intent.getStringExtra("userName")

        database.child("sensor").child("openweather").get().addOnSuccessListener {
            var icon = it?.child("icon")!!.value
            var temp = it?.child("temp")!!.value
            var weather = it?.child("description")!!.value
            var air_level = it?.child("air_level")!!.value

            Log.d("firebaseData","icon = $icon, temp = $temp, weather = $weather, air_level = $air_level")

            val fragment = MainFragment()
            fragment.arguments = Bundle().apply {
                putString("userName","$userName")
                putString("icon","$icon")
                putString("temp","$temp")
                putString("weather","$weather")
                putString("airLevel","$air_level")
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
        }.addOnFailureListener {
            Log.d("firebase","Error")
            Log.e("firebase","Error getting data", it)
        }

//        val fragment = MainFragment()
//        fragment.arguments = Bundle().apply {
//            putString("userName","$userName")
//            Log.d("bundle","icon = $icon")
//            putString("icon","$icon")
//        }
//
//
//        val fragmentList = listOf(fragment, SecondFragment(), ThirdFragment())
//
//        // 어댑터 생성하고 앞에서 생성한 프래그먼트의 목록을 저장함. 어댑터의 첫번째 파라미터에는 항상 SupportFragmentManger를 사용
//        val adapter = FragmentAdapter(this)
//        adapter.fragmentList = fragmentList
//        binding.container.adapter = adapter
//
//        val tapTitles = listOf<String>("A","B","C")
//        TabLayoutMediator(binding.tabLayout, binding.container) { tab, position ->
//            tab.text = tapTitles[position]
//        }.attach()
    }
}

@IgnoreExtraProperties
data class Received(
    var airLevel : Int = 0,
    var temp : Int = 0,
    var icon : String? = "",
    var description : String? = "",
    var update : String = ""
)