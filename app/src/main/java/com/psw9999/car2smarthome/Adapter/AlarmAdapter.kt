package com.psw9999.car2smarthome.Adapter

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.car2smarthome.R
import com.psw9999.car2smarthome.data.alarmData
import com.psw9999.car2smarthome.data.scheduleData

class AlarmAdapter(val context : Context) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>()
{
    var alarms = mutableListOf<alarmData>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmAdapter.AlarmViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun onBindViewHolder(holder: AlarmAdapter.AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    inner class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val alarmImage : ImageView = itemView.findViewById(R.id.imageView_alarm)
        val alarmContent : TextView = itemView.findViewById(R.id.textView_alarmContent)
        val alarmTime : TextView = itemView.findViewById(R.id.textView_alarmTime)

        fun bind(item: alarmData) {
            var informContent : String? = item.inform
            alarmContent.text = informContent
            var dateArr = item.date!!.split(".")
            alarmTime.text = dateArr[1]+"월"+dateArr[2]+"일 "+dateArr[3]+":"+dateArr[4]
            when {
                informContent!!.contains("전등") -> alarmImage.setImageResource(R.drawable.light)
                informContent!!.contains("가스밸브") -> alarmImage.setImageResource(R.drawable.gasvalve)
                informContent!!.contains("창문") -> alarmImage.setImageResource(R.drawable.window)
                informContent!!.contains("에어컨") -> alarmImage.setImageResource(R.drawable.aircon)
                informContent!!.contains("실내") -> alarmImage.setImageResource(R.drawable.indoor)
                informContent!!.contains("외출") -> alarmImage.setImageResource(R.drawable.outdoor)
                informContent!!.contains("슬립") -> alarmImage.setImageResource(R.drawable.sleep)
                informContent!!.contains("에코") -> alarmImage.setImageResource(R.drawable.eco)
            }
        }

    }
}