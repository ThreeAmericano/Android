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

    // RecyclerView에는 ListView와는 다르게 클릭리스너가 내장되어 있지 않다.
    // 그래서 추가로 ClickListener 역할을 하는 interface를 만들어주어야 한다.
    interface OnScheduleItemClickListener{
        fun onItemClick(view:View, pos : Int)
    }


    // 클릭 리스너 선언
    lateinit var mItemClickListener: OnScheduleItemClickListener

    // 클릭 리스너 등록 메서드 (메인 액티비티에서 람다식 혹은 inner 클래스로 호출)
    fun setOnScheduleClickListener(itemClickListener: OnScheduleItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    // 전체 데이터의 개수를 리턴
    override fun getItemCount(): Int {
        return schedules.size
    }

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val scheduleName : TextView = itemView.findViewById(R.id.textView_scheduleName)
        val scheduleTime : TextView = itemView.findViewById(R.id.textView_time)
        val scheduleButton : Button = itemView.findViewById(R.id.button_schedule)
        val oneTime : TextView = itemView.findViewById(R.id.textView_oneTime)
        val switch_scheduleEnable : Switch = itemView.findViewById(R.id.switch_scheduleEnable)

        fun bind(item:scheduleData) {
            scheduleName.text = item.Title
            var startTime: Int = item.Start_time!!.toInt()
            if (startTime < 1200) {
                if (startTime < 100)
                    scheduleTime.text =
                        "오전 " + 12 + "시 " + item.Start_time?.slice(listOf(2, 3)) + "분"
                else
                    scheduleTime.text = "오전 " + (startTime / 100) + "시 " + item.Start_time?.slice(
                        listOf(
                            2,
                            3
                        )
                    ) + "분"
            } else {
                if (startTime < 1300)
                    scheduleTime.text =
                        "오후 " + 12 + "시 " + item.Start_time?.slice(listOf(2, 3)) + "분"
                else
                    scheduleTime.text =
                        "오후 " + ((startTime - 1200) / 100) + "시 " + item.Start_time?.slice(
                            listOf(
                                2,
                                3
                            )
                        ) + "분"
            }

            for (i in 0..6) {
                if (item.Daysofweek?.get(i) == true) {
                    itemView.findViewWithTag<TextView>(i.toString()).visibility = View.VISIBLE
                } else {
                    itemView.findViewWithTag<TextView>(i.toString()).visibility = View.GONE
                }
            }
            switch_scheduleEnable.isChecked = item.Enabled
            if (item.repeat == true) {
                oneTime.text = ""
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                scheduleButton.setOnClickListener {
                    mItemClickListener?.onItemClick(itemView, pos)
                }
            }
        }
    }
}