package com.psw9999.car2smarthome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.car2smarthome.Adapter.SchedulesAdapter
import com.psw9999.car2smarthome.data.scheduleData
import com.psw9999.car2smarthome.databinding.FragmentThirdBinding

private lateinit var schedulesAdapter: SchedulesAdapter
private lateinit var binding: FragmentThirdBinding

class ThirdFragment : Fragment() {
    lateinit var scheduleIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        val context : Context = requireContext()
        scheduleIntent = Intent(context, ScheduleActivity::class.java)
        binding.floatingActionButtonScheduleAdd.setOnClickListener {
            scheduleIntent.putExtra("curPos",-1)
            startActivity(scheduleIntent)
        }
        initRecyclerView(context)
        return binding.root
    }

    fun refreshAdapter(position : Int) {
        schedulesAdapter.notifyItemChanged(position)
    }

    private fun initRecyclerView(context : Context) {
        schedulesAdapter = SchedulesAdapter(context)
        schedulesAdapter.schedules = scheduleDatas
        schedulesAdapter.setOnScheduleClickListener(object : SchedulesAdapter.OnScheduleItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                scheduleIntent.putExtra("curPos",pos)
                startActivity(scheduleIntent)
            }
        })
        schedulesAdapter.notifyDataSetChanged()
        binding.recyclerViewScheduleList.adapter = schedulesAdapter
    }

    companion object {
        var scheduleDatas = mutableListOf<scheduleData>()
    }
}