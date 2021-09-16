package com.psw9999.car2smarthome

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.psw9999.car2smarthome.databinding.ActivityScheduleBinding

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}