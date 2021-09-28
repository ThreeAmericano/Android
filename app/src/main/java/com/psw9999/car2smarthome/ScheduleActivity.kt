package com.psw9999.car2smarthome

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.Adapter.ModeAdapter
import com.psw9999.car2smarthome.Adapter.SchedulesAdapter
import com.psw9999.car2smarthome.ThirdFragment.Companion.scheduleDatas
import com.psw9999.car2smarthome.data.scheduleData
import com.psw9999.car2smarthome.databinding.ActivityScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    val binding by lazy {ActivityScheduleBinding.inflate(layoutInflater)}
    val scheduleModeAdapter = ModeAdapter(this)
    protected var curpos : Int = 0
    private var lastTitle : String? = null

    val cal = Calendar.getInstance() // 캘린더뷰 만들기


    // 날짜 및 시간 형식 지정
    var simpleDataFormat = SimpleDateFormat("yyyy.MM.dd")
    var date = Date()
    var curTime : String = simpleDataFormat.format(date)
    var timeDataSplit = curTime.split(".")
    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    private fun ChipGroup.getChildrenTag() : Int {
        children.forEach {
            if(it is Chip) {
                if(it.isChecked) return it.tag.toString().toInt()
            }
        }
        return 0
    }

    private fun loadSavedData() {
        binding.switchScheduleEnable.isChecked = scheduleDatas[curpos].enabled
        if(scheduleDatas[curpos].title != null)
            binding.editTextTitle.setText("${scheduleDatas[curpos].title}")
        binding.timePickerScheduleTime.hour =  scheduleDatas[curpos].startTime!!.slice(listOf(0, 1)).toInt()
        binding.timePickerScheduleTime.minute = scheduleDatas[curpos].startTime!!.slice(listOf(2, 3)).toInt()
        binding.switchScheduleRepeat.isChecked = scheduleDatas[curpos].repeat!!
        if(scheduleDatas[curpos].repeat!!){
            for (i in 0..6) {
                binding.root.findViewWithTag<Chip>(i.toString()).isChecked = scheduleDatas[curpos].daysOfWeek[i]
            }
        }else{
            if(scheduleDatas[curpos].activeDate != null) {
                var date = scheduleDatas[curpos].activeDate!!.split(".")
                binding.textViewDate.text = date[0] + "년 " + date[1] + "월 " + date[2] + "일"
                cal.set(date[0].toInt(),date[1].toInt()-1,date[2].toInt())
            }else{
                binding.textViewDate.text = timeDataSplit[0] + "년 " + timeDataSplit[1] + "월 " + timeDataSplit[2] + "일"
                cal.set(timeDataSplit[0].toInt(),timeDataSplit[1].toInt()-1,timeDataSplit[2].toInt())
            }
        }

        binding.switchModeEnable.isChecked = scheduleDatas[curpos].modeNum != 0
        scheduleModeAdapter.modeSelected(scheduleDatas[curpos].modeNum!!)
        binding.switchAircon.isChecked = scheduleDatas[curpos].airconEnable!!
        binding.seekBarAirconPower.progress = ((scheduleDatas[curpos].airconWindPower!!) * 10)
        binding.switchLight.isChecked = scheduleDatas[curpos].lightEnable!!
        binding.seekBarLightBrightness.progress = ((scheduleDatas[curpos].lightBrightness!!) * 10)
        binding.root.findViewWithTag<Chip>(((scheduleDatas[curpos].lightColor)?.plus(7)).toString()).isChecked = true
        binding.root.findViewWithTag<Chip>(((scheduleDatas[curpos].lightMode)?.plus(15)).toString()).isChecked = true
        binding.switchGasValve.isChecked = scheduleDatas[curpos].gasValveEnable!!
        binding.switchWindow.isChecked = scheduleDatas[curpos].windowOpen!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
        setContentView(binding.root)

        curpos = intent.getIntExtra("curPos",0)

        binding.textViewDate.text = curTime
        binding.switchScheduleRepeat.setOnCheckedChangeListener { compoundButton, ischecked ->
            if(ischecked){
                binding.horizontalScrollViewScheduleDay.visibility = View.VISIBLE
                binding.textViewDate.visibility = View.GONE
                binding.ImageViewDate.visibility = View.GONE
            }else {
                binding.horizontalScrollViewScheduleDay.visibility = View.GONE
                binding.textViewDate.visibility = View.VISIBLE
                binding.ImageViewDate.visibility = View.VISIBLE
            }
        }

        binding.ImageViewDate.setOnClickListener {

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                binding.textViewDate.text =
                    year.toString()+"년 "+ (month+1).toString() + "월 " + day.toString() + "일"
                var month_String = ""
                var day_String = ""
                if((month+1) < 10) month_String = "0${month+1}"
                else month_String = "${month+1}"
                if(day < 10) day_String = "0$day"
                else day_String = "$day"
                scheduleDatas[curpos].activeDate = "$year.$month_String.$day_String"
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.switchModeEnable.setOnCheckedChangeListener { compoundButton, ischecked ->
            if(ischecked) {
                binding.horizontalModeSelcet.visibility = View.VISIBLE
                binding.constraintLayoutApplianceControl.visibility = View.GONE
            }else{
                binding.horizontalModeSelcet.visibility = View.GONE
                binding.constraintLayoutApplianceControl.visibility = View.VISIBLE
            }
        }

        if(curpos == -1) {
            curpos = scheduleDatas.count()
            scheduleDatas.add(scheduleData())
            loadSavedData()
        }else{
            loadSavedData()
            lastTitle = scheduleDatas[curpos].title!!
        }

        binding.buttonScheduleCancel.setOnClickListener {
            this.finish()
        }

        binding.buttonScheduleRegister.setOnClickListener {
            if (binding.editTextTitle.text!!.isEmpty()) {
                Toast.makeText(
                    baseContext, "스케줄 제목을 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                scheduleDatas[curpos].name = MainActivity.userName
                scheduleDatas[curpos].enabled = binding.switchScheduleEnable.isChecked
                scheduleDatas[curpos].title = binding.editTextTitle.text.toString()
                var hour = binding.timePickerScheduleTime.hour.toString()
                if(hour.length < 2) hour = "0$hour"
                var minute = binding.timePickerScheduleTime.minute.toString()
                if(minute.length < 2) minute = "0$minute"
                scheduleDatas[curpos].startTime = hour + minute
                scheduleDatas[curpos].repeat = binding.switchScheduleRepeat.isChecked
                if (scheduleDatas[curpos].repeat!!) {
                    for (i in 0..6) {
                        scheduleDatas[curpos].daysOfWeek[i] = binding.root.findViewWithTag<Chip>(i.toString()).isChecked
                    }
                }

                if (binding.switchModeEnable.isChecked){
                    scheduleDatas[curpos].modeNum = scheduleModeAdapter.getSelectedMode()
                }else{
                    scheduleDatas[curpos].modeNum = 0
                    scheduleDatas[curpos].airconEnable = binding.switchAircon.isChecked
                    scheduleDatas[curpos].airconWindPower = (binding.seekBarAirconPower.progress/10)
                    scheduleDatas[curpos].lightEnable = binding.switchLight.isChecked
                    scheduleDatas[curpos].lightBrightness = (binding.seekBarLightBrightness.progress/10)
                    scheduleDatas[curpos].lightColor =  binding.chipGroupColor.getChildrenTag()!!-7
                    scheduleDatas[curpos].lightMode =  binding.chipGroupLightMode.getChildrenTag()!!-15
                    scheduleDatas[curpos].gasValveEnable = binding.switchGasValve.isChecked
                    scheduleDatas[curpos].windowOpen = binding.switchWindow.isChecked
                }
                if((lastTitle != null) and (lastTitle != scheduleDatas[curpos].title)) {
                    db.collection("schedule_mode").document(lastTitle!!).delete()
                }
                db.collection("schedule_mode").document("${scheduleDatas[curpos].title}").set(scheduleDatas[curpos])
                ThirdFragment().refreshAdapter(curpos)
                Toast.makeText(
                    baseContext, "스케줄 등록 완료",
                    Toast.LENGTH_SHORT
                ).show()
                this.finish()
            }
        }

    }

    private fun initRecyclerView() {
        scheduleModeAdapter.modes = SecondFragment.modeDatas
        scheduleModeAdapter.setOnItemClickListener(object : ModeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
            }
        })
        scheduleModeAdapter.notifyDataSetChanged()
        binding.recyclerviewScheduleMode.adapter = scheduleModeAdapter
    }
}