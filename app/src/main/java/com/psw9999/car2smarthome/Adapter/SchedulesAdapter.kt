package com.psw9999.car2smarthome.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.car2smarthome.R
import com.psw9999.car2smarthome.data.scheduleData

class SchedulesAdapter(val context: Context) : RecyclerView.Adapter<SchedulesAdapter.ScheduleViewHolder>() {

    var schedules = mutableListOf<scheduleData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val scheduleName : TextView = itemView.findViewById(R.id.textView_scheduleName)
        val scheduleTime : TextView = itemView.findViewById(R.id.textView_time)
        val scheduleButton : Button = itemView.findViewById(R.id.button_schedule)
        val oneTime : TextView = itemView.findViewById(R.id.textView_oneTime)
        val switch_scheduleEnable : Switch = itemView.findViewById(R.id.switch_scheduleEnable)
        val

        fun bind(item:scheduleData) {
            scheduleName.text = item.Title
            scheduleTime.text = item.Start_time?.slice(listOf(0,1)) + "시 " +  item.Start_time?.slice(listOf(2,3)) +"분"
            switch_scheduleEnable.isChecked = item.Enabled
            if(item.repeat == true){
                oneTime.text = ""
            }

        }

    }
}