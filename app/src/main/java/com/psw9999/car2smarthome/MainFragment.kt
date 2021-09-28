package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.core.view.forEach
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshot.Index
import com.psw9999.car2smarthome.databinding.FragmentMainBinding
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase
import com.psw9999.car2smarthome.MainActivity.Companion.guideText
import com.psw9999.car2smarthome.SecondFragment.Companion.modeDatas
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
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

    var applianceControlMessage  = "022255022"

    fun Boolean.toInt() = if (this) 1 else 0

    fun Boolean.onAndOFF() = if(this) "OFF" else "ON"

    fun Boolean.sendMessage() = if(this) 0 else 1

    private val modeClickListener = object : OnSingleClickListener() {
        override fun onSingleClick(view: View?) {
            var tagValue : Int = view!!.tag.toString().toInt()-1
            var SendMessage : String = "${modeDatas[tagValue].modeNum}" +
                    "${modeDatas[tagValue].airconEnable.toInt()}" +
                    "${modeDatas[tagValue].airconWindPower}" +
                    "${modeDatas[tagValue].lightEnable.toInt()}" +
                    "${modeDatas[tagValue].lightBrightness}" +
                    "${modeDatas[tagValue].lightColor}" +
                    "${(modeDatas[tagValue].lightMode)?.rem(10)}" +
                    "${modeDatas[tagValue].windowOpen.toInt()}" +
                    "${modeDatas[tagValue].gasValveEnable.toInt()}"
            controlThread.sendMQTT(SendMessage)
            Log.d("Send",SendMessage)
        }
    }

    private val applianceClickListener = object : OnSingleClickListener() {
        override fun onSingleClick(view: View?) {
            //var tagValue : Int = view!!.tag.toString().toInt()-1
            var tempString = ""
            var post = applianceControlMessage.chunked(1).toMutableList()
            post[view!!.tag.toString().toInt()] = view.isSelected.sendMessage().toString()

            post.forEach {
                tempString += it
            }

            controlThread.sendMQTT(tempString)
            Log.d("Send",tempString)
            binding.modes.childrenAllOff()
        }
    }

    fun ThemedToggleButtonGroup.childrenAllOff(){
        children.forEach {
            if(it.isSelected){
                binding.root.findViewById<ThemedToggleButtonGroup>(this.id).selectButton(it.id)
            }
        }
    }

    // 리스너 선언 및 초기화
    val eventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            guideText = dataSnapshot.value.toString()

            if(guideText != "none") {
                binding.textViewGuide.text = guideText
            }else{
                binding.textViewGuide.text = "오늘도 좋은하루 되세요!"
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    }

    // 리스너 선언 및 초기화
    val applianceListner = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val post = dataSnapshot.child("status").value.toString().chunked(1)
            MainActivity.applianceStatus.mode = post[0].toInt()
            MainActivity.applianceStatus.airconEnabled =  post[1].toInt()
            MainActivity.applianceStatus.windPower =  post[2].toInt()
            MainActivity.applianceStatus.lightEnabled =  post[3].toInt()
            MainActivity.applianceStatus.lightBrightness =  post[4].toInt()
            MainActivity.applianceStatus.lightColor =  post[5].toInt()
            MainActivity.applianceStatus.lightMod = post[6].toInt()
            MainActivity.applianceStatus.windowStatus = post[7].toInt()
            MainActivity.applianceStatus.gasValveStatus = post[8].toInt()
            Log.d("DataChange", "${MainActivity.applianceStatus}")

            activity?.runOnUiThread {
                // 추후 status 변수를 만들어 해당 값과 비교하여 ui 변경하도록 수정 필요b
                when(MainActivity.applianceStatus.mode) {
                    1 -> {  if(!binding.inputMode.isSelected) binding.modes.selectButton(R.id.inputMode) }
                    2 -> {  if(!binding.outGoingMode.isSelected) binding.modes.selectButton(R.id.outGoingMode) }
                    3 -> {  if(!binding.toggleButtonSleepMode.isSelected) binding.modes.selectButton(R.id.toggleButtonSleepMode) }
                    4 -> { if(!binding.toggleButtonEcoMode.isSelected) binding.modes.selectButton(R.id.toggleButtonEcoMode) }
                    else -> binding.modes.childrenAllOff()
                }
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

                if(MainActivity.applianceStatus.lightEnabled == 1) {
                    if(!binding.light.isSelected) {
                        binding.appliances.selectButton(R.id.light)
                    }
                }else{
                    if(binding.light.isSelected) {
                        binding.appliances.selectButton(R.id.light)
                    }
                }

                if(MainActivity.applianceStatus.windowStatus == 1) {
                    if(!binding.window.isSelected) {
                        binding.appliances.selectButton(R.id.window)
                    }
                }else{
                    if(binding.window.isSelected) {
                        binding.appliances.selectButton(R.id.window)
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


        realtimeFirebase.child("smarthome").addValueEventListener(applianceListner)
        realtimeFirebase.child("server").child("notification").addValueEventListener(eventListener)

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

//        binding.appliances.children.forEachIndexed { index, appliance ->
//            hashMap[appliance.tag.toString()] = index
//        }

        with(binding) {
            inputMode.setOnClickListener(modeClickListener)
            outGoingMode.setOnClickListener(modeClickListener)
            toggleButtonSleepMode.setOnClickListener(modeClickListener)
            toggleButtonEcoMode.setOnClickListener(modeClickListener)

            gasvalve.setOnClickListener(applianceClickListener)
            aircon.setOnClickListener(applianceClickListener)
            light.setOnClickListener(applianceClickListener)
            window.setOnClickListener(applianceClickListener)
        }
        return binding.root
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            Log.d("onViewCreated", "true")
            arguments?.takeIf { it.containsKey("userName") }?.apply {
                binding.loginNameText.text = "${userName}님 안녕하세요?"
            }

            arguments?.takeIf { it.containsKey("icon") and it.containsKey("temp")}?.apply {
                when (icon) {
                    "01" -> {binding.weatherView.setImageResource(R.drawable.sunny)
                             binding.textViewWeather.text = "날씨 : 맑음, $temperature°C"}
                    "02" -> {binding.weatherView.setImageResource(R.drawable.littlecloudy)
                             binding.textViewWeather.text = "날씨 : 구름 조금, $temperature°C"}
                    "03" -> {binding.weatherView.setImageResource(R.drawable.cloudy)
                             binding.textViewWeather.text = "날씨 : 구름 많음, $temperature°C"}
                    "04" -> {binding.weatherView.setImageResource(R.drawable.darkcloudy)
                             binding.textViewWeather.text = "날씨 : 흐림, $temperature°C"}
                    "09" -> {binding.weatherView.setImageResource(R.drawable.rain)
                             binding.textViewWeather.text = "날씨 : 비, $temperature°C"}
                    "10" -> {binding.weatherView.setImageResource(R.drawable.rainsunny)
                             binding.textViewWeather.text = "날씨 : 비 온후 갬, $temperature°C"}
                    "11" -> {binding.weatherView.setImageResource(R.drawable.thunder)
                             binding.textViewWeather.text = "날씨 : 뇌우, $temperature°C"}
                    "13" -> {binding.weatherView.setImageResource(R.drawable.snow)
                             binding.textViewWeather.text = "날씨 : 눈, $temperature°C"}
                    "50" -> {binding.weatherView.setImageResource(R.drawable.fog)
                             binding.textViewWeather.text = "날씨 : 안개, $temperature°C"}
                    else -> {binding.weatherView.setImageResource(R.drawable.sunny)
                             binding.textViewWeather.text = "날씨 : 맑음, $temperature°C"}
                }
            }

            arguments?.takeIf { it.containsKey("airLevel") }?.apply {
                when (airLevel) {
                    "1" -> binding.textViewAirLevel.text = "미세먼지 : 매우좋음"
                    "2" -> binding.textViewAirLevel.text = "미세먼지 : 좋음"
                    "3" -> binding.textViewAirLevel.text = "미세먼지 : 보통"
                    "4" -> binding.textViewAirLevel.text = "미세먼지 : 나쁨"
                    "5" -> binding.textViewAirLevel.text = "미세먼지 : 매우 나쁨"
                }
            }

            if(MainActivity.applianceStatus.airconEnabled != 0) {
                binding.appliances.selectButton(R.id.aircon)
            }
            if(MainActivity.applianceStatus.lightEnabled != 0) {
                binding.appliances.selectButton(R.id.light)
            }
            if(MainActivity.applianceStatus.windowStatus != 0) {
                binding.appliances.selectButton(R.id.window)
            }
            if(MainActivity.applianceStatus.gasValveStatus != 0) {
                binding.appliances.selectButton(R.id.gasvalve)
            }

            binding.humiProgressBar.progress = MainActivity.weather.humidity.toInt()
            binding.textViewHumidityValue.text = "${MainActivity.weather.humidity} %"

            binding.tempProgressBar.progress = MainActivity.weather.temperature.toInt()
            binding.textViewTemperatureValue.text = "${MainActivity.weather.temperature}°C"

            if(guideText != "none") {
                binding.textViewGuide.text = guideText
            }else{
                binding.textViewGuide.text = "오늘도 좋은하루 되세요!"
            }

        }


    companion object {
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