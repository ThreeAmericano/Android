package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var userName : String? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userName = intent.getStringExtra("userName")
        Log.d("test","$userName")
        val fragmentList = listOf(MainFragment(),SecondFragment(),ThirdFragment())

        // 어댑터 생성하고 앞에서 생성한 프래그먼트의 목록을 저장함. 어댑터의 첫번째 파라미터에는 항상 SupportFragmentManger를 사용
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        val tapTitles = listOf<String>("A","B","C")
        TabLayoutMediator(binding.tabLayout,binding.viewPager) { tab, position ->
            tab.text = tapTitles[position]
        }.attach()
    }
}