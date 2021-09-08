package com.psw9999.car2smarthome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.psw9999.car2smarthome.databinding.FragmentMainBinding
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase
import com.psw9999.car2smarthome.SecondFragment.Companion.modeDatas
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import java.util.*

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
    private var userName : String? = null
    private var icon : String? = null
    private var weather : String? = null
    private var temperature : String? = null
    private var airLevel : String? = null

    lateinit var binding: FragmentMainBinding

    private val controlThread = ControlThread()

    fun Boolean.toInt() = if (this) 1 else 0

//    val modeClickListener = requireView().setOnClickListener(object : OnSingleClickListener(){
//        override fun onSingleClick(view: View?) {
//            var tagValue : Int = view!!.tag.toString().toInt()
//            Log.d("indoor","${modeDatas[tagValue].modeNum}" +
//                    "${modeDatas[tagValue].airconEnable.toInt()}" +
//                    "${modeDatas[tagValue].airconWindPower}" +
//                    "${modeDatas[tagValue].lightEnable.toInt()}" +
//                    "${modeDatas[tagValue].lightBirghtness}" +
//                    "${modeDatas[tagValue].lightColor}" +
//                    "${modeDatas[tagValue].lightMode}" +
//                    "${modeDatas[tagValue].gasValveEnable.toInt()}" +
//                    "${modeDatas[tagValue].windowOpen.toInt()}"
//            )
//        }
//    })

    val modeClickListener = object : OnSingleClickListener() {
        override fun onSingleClick(view: View?) {
            var tagValue : Int = view!!.tag.toString().toInt()
            var SendMessage : String = "${modeDatas[tagValue].modeNum}" +
                    "${modeDatas[tagValue].airconEnable.toInt()}" +
                    "${modeDatas[tagValue].airconWindPower}" +
                    "${modeDatas[tagValue].lightEnable.toInt()}" +
                    "${modeDatas[tagValue].lightBirghtness}" +
                    "${modeDatas[tagValue].lightColor}" +
                    "${(modeDatas[tagValue].lightMode)?.rem(10)}" +
                    "${modeDatas[tagValue].windowOpen.toInt()}" +
                    "${modeDatas[tagValue].gasValveEnable.toInt()}"
            controlThread.sendMQTT(SendMessage)
            Log.d("Send",SendMessage)
        }
    }


    // 리스너 선언 및 초기화
    val mValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val post = dataSnapshot.child("status").value.toString().chunked(1)
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
                // 추후 status 변수를 만들어 해당 값과 비교하여 ui 변경하도록 수정 필요
                if(MainActivity.applianceStatus.airconEnabled == 1) {
                    if(!binding.aircon.isSelected) {
                        binding.appliances.selectButton(R.id.aircon)
                    }
                }else{
                    if(binding.aircon.isSelected) {
                        binding.appliances.selectButton(R.id.aircon)
                    }
                }
                if(MainActivity.applianceStatus.gasValveStatus == 1) {
                    if(!binding.gasvalve.isSelected) {
                        binding.appliances.selectButton(R.id.gasvalve)
                    }
                }else{
                    if(binding.gasvalve.isSelected) {
                        binding.appliances.selectButton(R.id.gasvalve)
                    }
                }
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
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.inputMode.setOnClickListener(modeClickListener)
        binding.outGoingMode.setOnClickListener(modeClickListener)
        binding.testMode.setOnClickListener(modeClickListener)
        binding.test2Mode.setOnClickListener(modeClickListener)

        binding.gasvalve.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if(!binding.gasvalve.isSelected) {
                    Toast.makeText(
                        activity, "가스밸브 ON",
                        Toast.LENGTH_SHORT
                    ).show()
                    controlThread.sendMQTT("022222221")
                }
                else {
                    Toast.makeText(
                        activity, "가스밸브 OFF",
                        Toast.LENGTH_SHORT
                    ).show()
                    controlThread.sendMQTT("022222220")
                }
            }
        })
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
                binding.weather.text = "날씨 : $weather, $temperature°C"
            }

//            arguments?.takeIf { it.containsKey("temp") }?.apply {
//                binding.temperature.text = "$temperature°C"
//            }

            arguments?.takeIf { it.containsKey("airLevel") }?.apply {
                when (airLevel) {
                    "1" -> binding.airLevel.text = "미세먼지 : 매우좋음"
                    "2" -> binding.airLevel.text = "미세먼지 : 좋음"
                    "3" -> binding.airLevel.text = "미세먼지 : 보통"
                    "4" -> binding.airLevel.text = "미세먼지 : 나쁨"
                    "5" -> binding.airLevel.text = "미세먼지 : 매우 나쁨"
                }
            }

            if(MainActivity.applianceStatus.airconEnabled != 0) {
                //binding.airconToggle.isChecked = true
                binding.appliances.selectButton(R.id.aircon)
            }
            if(MainActivity.applianceStatus.lightEnabled != 0) {
                //binding.lightToggle.isChecked = true
                binding.appliances.selectButton(R.id.light)
            }
            if(MainActivity.applianceStatus.windowStatus != 0) {
                //binding.windowToggle.isChecked = true
                binding.appliances.selectButton(R.id.window)
            }
            if(MainActivity.applianceStatus.gasValveStatus != 0) {
                //binding.gasToggle.isChecked = true
                binding.appliances.selectButton(R.id.gasvalve)
            }

            binding.humiProgressBar.progress = MainActivity.weather.humidity.toInt()
            binding.humiTextView.text = "${MainActivity.weather.humidity}%"

            binding.tempProgressBar.progress = MainActivity.weather.temperature.toInt()
            binding.tempTextView.text = "${MainActivity.weather.temperature}°C"

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
        fun newInstance(param1 : String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString("userName",param1)
                }
            }
        }
}

// 중복 클릭 방지를 위한 클래스 구현
public abstract class OnSingleClickListener : View.OnClickListener {

    // 중복 클릭 방지 시간 설정 (해당 시간 이후에 다시 클릭 가능)
    private val MIN_CLICK_INTERVAL : Int = 600

    // 마지막 클릭 시간
    var mLastClickTime : Long = 0
    abstract fun onSingleClick(v : View?)

    override fun onClick(v: View?) {
        // 현재 클릭한 시간
        var currentClickTime: Long = SystemClock.uptimeMillis()
        // 이전에 클릭한 시간과 현재 시간의 차이
        var elapsedTime: Long = currentClickTime - mLastClickTime
        // 마지막 클릭 시간 업데이트
        mLastClickTime = currentClickTime

        //내가 정한 중복클릭시간 차이를 안넘었으면 클릭이벤트 발생못하게 return
        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return

        onSingleClick(v)
    }
}