package com.psw9999.car2smarthome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.psw9999.car2smarthome.databinding.FragmentMainBinding
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = "abc"

    private var userName : String? = null
    private var icon : String? = null
    private var weather : String? = null
    private var temperature : String? = null
    private var airLevel : String? = null

    lateinit var binding: FragmentMainBinding

    private val controlThread = ControlThread()

    // 리스너 선언 및 초기화
    val mValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val post = dataSnapshot.child("status").value.toString().chunked(1)
//                Log.d("DataChange","$post")
            MainActivity.applianceStatus.airconEnabled =  post[0].toInt()
            MainActivity.applianceStatus.windPower =  post[1].toInt()
            MainActivity.applianceStatus.lightEnabled =  post[2].toInt()
            MainActivity.applianceStatus.lightBrightness =  post[3].toInt()
            MainActivity.applianceStatus.lightColor =  post[4].toInt()
            MainActivity.applianceStatus.lightMod = post[5].toInt()
            MainActivity.applianceStatus.windowStatus = post[6].toInt()
            MainActivity.applianceStatus.gasValveStatus = post[7].toInt()
            Log.d("DataChange", "${MainActivity.applianceStatus}")

            activity?.runOnUiThread {
                binding.airconToggle.isChecked = MainActivity.applianceStatus.airconEnabled == 1
                binding.gasToggle.isChecked = MainActivity.applianceStatus.gasValveStatus == 1
                binding.lightToggle.isChecked = MainActivity.applianceStatus.lightEnabled == 1
                binding.windowToggle.isChecked = MainActivity.applianceStatus.windowStatus == 1

            }

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        userName = arguments?.getString("userName")
        super.onCreate(savedInstanceState)
        arguments?.let {
            // arguments가 null이 아닐때만 실행된다.
            userName = it.getString("userName")
            icon = it.getString("icon")
            weather = it.getString("weather")
            temperature = it.getString("temp")
            airLevel = it.getString("airLevel")
            Log.d("onCreate","userName = $userName, icon = $icon, weather = $weather")
        }
        // 리스너에 이벤트를 포함시킴.
        realtimeFirebase.child("smarthome").addValueEventListener(mValueEventListener)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
//            weather = it.getString("weather")
            userName = it.getString("userName")
//            icon = it.getString("icon")
            Log.d("onAttach","$userName")
            Log.d("onAttach","$icon")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. 뷰 바인딩 설정
        binding = FragmentMainBinding.inflate(inflater, container, false)
        // 3.  프래그먼트 레이아웃 뷰 반환
        binding.airconToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                Toast.makeText(
                    activity, "에어컨 ON",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // The toggle is disabled
                Toast.makeText(
                    activity, "에어컨 OFF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.gasToggle.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                Toast.makeText(
                    activity, "가스밸브 ON",
                    Toast.LENGTH_SHORT
                ).show()
                controlThread.sendMQTT("022222221")
            } else {
                // The toggle is disabled
                Toast.makeText(
                    activity, "가스밸브 OFF",
                    Toast.LENGTH_SHORT
                ).show()
                controlThread.sendMQTT("022222220")
            }
        }

            return binding.root
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            Log.d("onViewCreated", "true")
            arguments?.takeIf { it.containsKey("userName") }?.apply {
                binding.loginNameText.text = "${userName}님 안녕하세요?"
            }

            arguments?.takeIf { it.containsKey("icon") }?.apply {
                when (icon) {
                    "01d" -> binding.weatherView.setImageResource(R.drawable.sunny)
                    "02d" -> binding.weatherView.setImageResource(R.drawable.littlecloudy)
                    "03d" -> binding.weatherView.setImageResource(R.drawable.cloudy)
                    "04d" -> binding.weatherView.setImageResource(R.drawable.darkcloudy)
                    "09d" -> binding.weatherView.setImageResource(R.drawable.rain)
                    "10d" -> binding.weatherView.setImageResource(R.drawable.rainsunny)
                    "11d" -> binding.weatherView.setImageResource(R.drawable.thunder)
                    "13d" -> binding.weatherView.setImageResource(R.drawable.snow)
                    "50d" -> binding.weatherView.setImageResource(R.drawable.fog)
                    else -> binding.weatherView.setImageResource(R.drawable.fog)
                }
            }
            arguments?.takeIf { it.containsKey("weather") }?.apply {
                binding.weather.text = "$weather"
            }

            arguments?.takeIf { it.containsKey("temp") }?.apply {
                binding.temperature.text = "$temperature°C"
            }

            arguments?.takeIf { it.containsKey("airLevel") }?.apply {
                when (airLevel) {
                    "1" -> binding.airLevel.text = "공기질 : 매우좋음"
                    "2" -> binding.airLevel.text = "공기질 : 좋음"
                    "3" -> binding.airLevel.text = "공기질 : 보통"
                    "4" -> binding.airLevel.text = "공기질 : 나쁨"
                    "5" -> binding.airLevel.text = "공기질 : 매우 나쁨"
                }
            }

            if(MainActivity.applianceStatus.airconEnabled != 0) {
                binding.airconToggle.isChecked = true
            }
            if(MainActivity.applianceStatus.lightEnabled != 0) {
                binding.lightToggle.isChecked = true
            }
            if(MainActivity.applianceStatus.windowStatus != 0) {
                binding.windowToggle.isChecked = true
            }
            if(MainActivity.applianceStatus.gasValveStatus != 0) {
                binding.gasToggle.isChecked = true
            }
        }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MainFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance(param1 : String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString("userName",param1)
                }
            }
        }
}