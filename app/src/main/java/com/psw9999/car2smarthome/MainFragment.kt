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
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.psw9999.car2smarthome.databinding.FragmentMainBinding

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