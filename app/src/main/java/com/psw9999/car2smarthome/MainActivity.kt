package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.psw9999.car2smarthome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        setContentView(binding.root)
        binding.TestName.addTextChangedListener {
            Log.d("EditText","입력된 이름=${it.toString()}")
        }
        binding.TestTime.addTextChangedListener {
            Log.d("EditText","입력된 시간=${it.toString()}")
        }
        binding.TestPassword.addTextChangedListener {
            Log.d("EditText","입력된 비밀번호=${it.toString()}")
        }
    }
}