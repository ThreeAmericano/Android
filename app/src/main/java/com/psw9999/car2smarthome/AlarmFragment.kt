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
import com.psw9999.car2smarthome.databinding.FragmentAlarmBinding



private lateinit var alarmAdapter: AlarmAdapter
private lateinit var binding : FragmentAlarmBinding
private val firebaseDB = Firebase.firestore

class AlarmFragment : Fragment() {
    private var alarmDatas = mutableListOf<alarmData>()
    private var initialFlag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 7. 파이어베이스의 클라우드에서 알람 정보 가져오기
        firebaseDB.collection("appliance_alarm")
            .addSnapshotListener { snapshots, e ->
                if( e != null) {
                    Log.w("AlarmFragment",e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        if (!initialFlag)
                        {
                            alarmDatas.add(dc.document.toObject<alarmData>())
                        }
                        else {
                            alarmDatas.add(0, dc.document.toObject<alarmData>())
                            alarmAdapter.notifyItemChanged(0)
                        }
                    }
                }
                initialFlag = true
                alarmAdapter.notifyDataSetChanged()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
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