package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.Adapter.AlarmAdapter
import com.psw9999.car2smarthome.Adapter.SchedulesAdapter
import com.psw9999.car2smarthome.data.alarmData
import com.psw9999.car2smarthome.data.mode
import com.psw9999.car2smarthome.data.scheduleData
import com.psw9999.car2smarthome.databinding.FragmentAlarmBinding



private lateinit var alarmAdapter: AlarmAdapter
private lateinit var binding : FragmentAlarmBinding
private val firebaseDB = Firebase.firestore

class AlarmFragment : Fragment() {
    private var alarmDatas = ArrayDeque<alarmData>()
    private var initialFlag : Boolean = true
    //TODO : 프래그먼트 두 번 호출되는 현상 있음. 확인필요
    private var befAlarmData : alarmData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("onCreateView","onCreateView")
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

         firebaseDB.collection("appliance_alarm")
             .addSnapshotListener { snapshots, e ->
                 if (e != null) {
                     Log.w("AlarmFragment", e)
                     return@addSnapshotListener
                 }
                 for (dc in snapshots!!.documentChanges) {
                     if (dc.type == DocumentChange.Type.ADDED) {
                         if (initialFlag) {
                             alarmDatas.add(dc.document.toObject())
                         } else {
                             if (befAlarmData != dc.document.toObject()) {
                                 alarmDatas.addFirst(dc.document.toObject())
                             }
                             befAlarmData = dc.document.toObject()
                         }
                     }
                 }
                 initialFlag = false
                 alarmAdapter.notifyDataSetChanged()
             }
        initialFlag = true
        initRecyclerView(requireContext())
        return binding.root
    }

    private fun initRecyclerView(context : Context) {
        alarmAdapter = AlarmAdapter(context)
        alarmAdapter.alarms = alarmDatas
        binding.recyclerviewAlarm.adapter = alarmAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        initialFlag = false
    }
}