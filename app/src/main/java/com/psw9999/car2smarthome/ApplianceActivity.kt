package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psw9999.car2smarthome.databinding.ActivityApplianceBinding

class ApplianceActivity : AppCompatActivity() {
    val binding by lazy { ActivityApplianceBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}