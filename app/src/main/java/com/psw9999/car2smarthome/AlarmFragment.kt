package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.psw9999.car2smarthome.Adapter.AlarmAdapter
import com.psw9999.car2smarthome.Adapter.SchedulesAdapter
import com.psw9999.car2smarthome.data.alarmData
import com.psw9999.car2smarthome.databinding.FragmentAlarmBinding

private lateinit var alarmAdapter: AlarmAdapter
private lateinit var binding : FragmentAlarmBinding

class AlarmFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        initRecyclerView(requireContext())
        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initRecyclerView(context : Context) {
        alarmAdapter = AlarmAdapter(context)
        alarmAdapter.alarms = alarmDatas
        alarmAdapter.notifyDataSetChanged()
        binding.recyclerviewAlarm.adapter = alarmAdapter
    }

    companion object {
        var alarmDatas = mutableListOf<alarmData>()
    }

}