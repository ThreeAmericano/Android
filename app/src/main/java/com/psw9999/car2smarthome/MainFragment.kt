package com.psw9999.car2smarthome

import android.content.Context
import android.content.Intent
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

class MainFragment : Fragment() {

    lateinit var userName: String
    lateinit var icon: String
    lateinit var weather: String
    lateinit var temperature: String
    lateinit var airLevel: String

    lateinit var binding: FragmentMainBinding
    val ApplianceIntent by lazy { Intent(requireContext(), ApplianceActivity::class.java) }

    private val controlThread = ControlThread()

    var applianceControlMessage = "022255022"

    fun Boolean.toInt() = if (this) 1 else 0
    fun Boolean.onAndOFF() = if (this) "OFF" else "ON"

    fun Boolean.sendMessage() = if (this) 0 else 1

    private val modeClickListener = object : OnSingleClickListener() {
        override fun onSingleClick(view: View?) {
            var tagValue: Int = view!!.tag.toString().toInt() - 1
            var SendMessage: String = "${modeDatas[tagValue].modeNum}" +
                    "${modeDatas[tagValue].airconEnable.toInt()}" +
                    "${modeDatas[tagValue].airconWindPower}" +
                    "${modeDatas[tagValue].lightEnable.toInt()}" +
                    "${modeDatas[tagValue].lightBrightness}" +
                    "${modeDatas[tagValue].lightColor}" +
                    "${(modeDatas[tagValue].lightMode)?.rem(10)}" +
                    "${modeDatas[tagValue].windowOpen.toInt()}" +
                    "${modeDatas[tagValue].gasValveEnable.toInt()}"
            controlThread.sendMQTT(SendMessage)
            Log.d("Send", SendMessage)
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
            Log.d("Send", tempString)
            binding.modes.childrenAllOff()
        }
    }

    fun ThemedToggleButtonGroup.childrenAllOff() {
        children.forEach {
            if (it.isSelected) {
                binding.root.findViewById<ThemedToggleButtonGroup>(this.id).selectButton(it.id)
            }
        }
    }

    // ????????? ?????? ??? ?????????
    val eventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            guideText = dataSnapshot.value.toString()

            if (guideText != "none") {
                binding.textViewGuide.text = guideText
            } else {
                binding.textViewGuide.text = "????????? ???????????? ?????????!"
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
        }
    }

    // ????????? ?????? ??? ?????????
    val applianceListner = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val post = dataSnapshot.child("status").value.toString().chunked(1)
            MainActivity.applianceStatus.mode = post[0].toInt()
            MainActivity.applianceStatus.airconEnabled = post[1].toInt()
            MainActivity.applianceStatus.windPower = post[2].toInt()
            MainActivity.applianceStatus.lightEnabled = post[3].toInt()
            MainActivity.applianceStatus.lightBrightness = post[4].toInt()
            MainActivity.applianceStatus.lightColor = post[5].toInt()
            MainActivity.applianceStatus.lightMod = post[6].toInt()
            MainActivity.applianceStatus.windowStatus = post[7].toInt()
            MainActivity.applianceStatus.gasValveStatus = post[8].toInt()
            Log.d("DataChange", "${MainActivity.applianceStatus}")

            activity?.runOnUiThread {
                // ?????? status ????????? ????????? ?????? ?????? ???????????? ui ??????????????? ?????? ??????b
                when (MainActivity.applianceStatus.mode) {
                    1 -> {
                        if (!binding.inputMode.isSelected) binding.modes.selectButton(R.id.inputMode)
                    }
                    2 -> {
                        if (!binding.outGoingMode.isSelected) binding.modes.selectButton(R.id.outGoingMode)
                    }
                    3 -> {
                        if (!binding.toggleButtonSleepMode.isSelected) binding.modes.selectButton(R.id.toggleButtonSleepMode)
                    }
                    4 -> {
                        if (!binding.toggleButtonEcoMode.isSelected) binding.modes.selectButton(R.id.toggleButtonEcoMode)
                    }
                    else -> binding.modes.childrenAllOff()
                }
                if (MainActivity.applianceStatus.airconEnabled == 1) {
                    if (!binding.aircon.isSelected) {
                        binding.appliances.selectButton(R.id.aircon)
                    }
                } else {
                    if (binding.aircon.isSelected) {
                        binding.appliances.selectButton(R.id.aircon)
                    }
                }

                if (MainActivity.applianceStatus.gasValveStatus == 1) {
                    if (!binding.gasvalve.isSelected) {
                        binding.appliances.selectButton(R.id.gasvalve)
                    }
                } else {
                    if (binding.gasvalve.isSelected) {
                        binding.appliances.selectButton(R.id.gasvalve)
                    }
                }

                if (MainActivity.applianceStatus.lightEnabled == 1) {
                    if (!binding.light.isSelected) {
                        binding.appliances.selectButton(R.id.light)
                    }
                } else {
                    if (binding.light.isSelected) {
                        binding.appliances.selectButton(R.id.light)
                    }
                }

                if (MainActivity.applianceStatus.windowStatus == 1) {
                    if (!binding.window.isSelected) {
                        binding.appliances.selectButton(R.id.window)
                    }
                } else {
                    if (binding.window.isSelected) {
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
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString("userName") as String
            icon = it.getString("icon") as String
            weather = it.getString("weather") as String
            temperature = it.getString("temp") as String
            airLevel = it.getString("airLevel") as String
            Log.d("onCreate", "userName = $userName, icon = $icon, weather = $weather")
        }
        // ???????????? ???????????? ????????????.
        realtimeFirebase.child("smarthome").addValueEventListener(applianceListner)
        realtimeFirebase.child("server").child("notification").addValueEventListener(eventListener)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

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

        binding.textViewApplianceControl.setOnClickListener {
            startActivity(ApplianceIntent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("onViewCreated", "true")
        arguments?.takeIf { it.containsKey("userName") }?.apply {
            binding.loginNameText.text = "${userName}??? ????????????????"
        }

        arguments?.takeIf { it.containsKey("icon") and it.containsKey("temp") }?.apply {
            when (icon) {
                "01" -> {
                    binding.weatherView.setImageResource(R.drawable.sunny)
                    binding.textViewWeather.text = "?????? : ??????, $temperature??C"
                }
                "02" -> {
                    binding.weatherView.setImageResource(R.drawable.littlecloudy)
                    binding.textViewWeather.text = "?????? : ?????? ??????, $temperature??C"
                }
                "03" -> {
                    binding.weatherView.setImageResource(R.drawable.cloudy)
                    binding.textViewWeather.text = "?????? : ?????? ??????, $temperature??C"
                }
                "04" -> {
                    binding.weatherView.setImageResource(R.drawable.darkcloudy)
                    binding.textViewWeather.text = "?????? : ??????, $temperature??C"
                }
                "09" -> {
                    binding.weatherView.setImageResource(R.drawable.rain)
                    binding.textViewWeather.text = "?????? : ???, $temperature??C"
                }
                "10" -> {
                    binding.weatherView.setImageResource(R.drawable.rainsunny)
                    binding.textViewWeather.text = "?????? : ??? ?????? ???, $temperature??C"
                }
                "11" -> {
                    binding.weatherView.setImageResource(R.drawable.thunder)
                    binding.textViewWeather.text = "?????? : ??????, $temperature??C"
                }
                "13" -> {
                    binding.weatherView.setImageResource(R.drawable.snow)
                    binding.textViewWeather.text = "?????? : ???, $temperature??C"
                }
                "50" -> {
                    binding.weatherView.setImageResource(R.drawable.fog)
                    binding.textViewWeather.text = "?????? : ??????, $temperature??C"
                }
                else -> {
                    binding.weatherView.setImageResource(R.drawable.sunny)
                    binding.textViewWeather.text = "?????? : ??????, $temperature??C"
                }
            }
        }

        arguments?.takeIf { it.containsKey("airLevel") }?.apply {
            when (airLevel) {
                "1" -> binding.textViewAirLevel.text = "???????????? : ????????????"
                "2" -> binding.textViewAirLevel.text = "???????????? : ??????"
                "3" -> binding.textViewAirLevel.text = "???????????? : ??????"
                "4" -> binding.textViewAirLevel.text = "???????????? : ??????"
                "5" -> binding.textViewAirLevel.text = "???????????? : ?????? ??????"
            }
        }

        if (MainActivity.applianceStatus.airconEnabled != 0) {
            binding.appliances.selectButton(R.id.aircon)
        }
        if (MainActivity.applianceStatus.lightEnabled != 0) {
            binding.appliances.selectButton(R.id.light)
        }
        if (MainActivity.applianceStatus.windowStatus != 0) {
            binding.appliances.selectButton(R.id.window)
        }
        if (MainActivity.applianceStatus.gasValveStatus != 0) {
            binding.appliances.selectButton(R.id.gasvalve)
        }

        binding.humiProgressBar.progress = MainActivity.weather.humidity.toInt()
        binding.textViewHumidityValue.text = "${MainActivity.weather.humidity} %"

        binding.tempProgressBar.progress = MainActivity.weather.temperature.toInt()
        binding.textViewTemperatureValue.text = "${MainActivity.weather.temperature}??C"

        if (guideText != "none") {
            binding.textViewGuide.text = guideText
        } else {
            binding.textViewGuide.text = "????????? ???????????? ?????????!"
        }

    }
}

// ?????? ?????? ????????? ?????? ????????? ??????
abstract class OnSingleClickListener : View.OnClickListener {

    // ?????? ?????? ?????? ?????? ?????? (?????? ?????? ????????? ?????? ?????? ??????)
    private val MIN_CLICK_INTERVAL : Int = 600

    // ????????? ?????? ??????
    var mLastClickTime : Long = 0
    abstract fun onSingleClick(v : View?)

    override fun onClick(v: View?) {
        // ?????? ????????? ??????
        var currentClickTime: Long = SystemClock.uptimeMillis()
        // ????????? ????????? ????????? ?????? ????????? ??????
        var elapsedTime: Long = currentClickTime - mLastClickTime
        // ????????? ?????? ?????? ????????????
        mLastClickTime = currentClickTime

        //?????? ?????? ?????????????????? ????????? ??????????????? ??????????????? ??????????????? return
        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return

        onSingleClick(v)
    }
}