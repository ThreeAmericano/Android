package com.psw9999.car2smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.psw9999.car2smarthome.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    val database = Firebase.database
    val myRef = database.getReference("name")
    val myRef2 = database.getReference("time")
    val myRef3 = database.getReference("password")
    val gasStatus = database.getReference("Gascooker")
    val Appliance = database.getReference("Appliance")
    //val boilerStatus = database.getReference("Boiler")
    //val conditionerStatus = database.getReference("Airconditioner")
    //val cleanerStatus = database.getReference("AirCleaner")
    var test = mutableListOf<String>("","","") // 문자열로 된 빈 컬렉션을 생성
    var temp = ""

    // [START auth_fui_create_launcher]
//    private val signInLauncher = registerForActivityResult(
//        FirebaseAuthUIActivityResultContract()
//    ) { res ->
//        this.onSignInResult(res)
//    }
    // [END auth_fui_create_launcher]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        //myRef.setValue("PSW9999") // test
        setContentView(binding.root)
        binding.TestName.addTextChangedListener {
            test.set(0, it.toString())
            Log.d("EditText","입력된 이름=${test.get(0)}")

        }
        binding.TestTime.addTextChangedListener {
            test.set(1, it.toString())
            Log.d("EditText","입력된 시간=${test.get(1)}")
        }
        binding.TestPassword.addTextChangedListener {
            test.set(2, it.toString())
            Log.d("EditText","입력된 비밀번호=${test.get(2)}")
        }

        binding.checkGasvalve.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked) gasStatus.setValue("True")
            else gasStatus.setValue("False")
        }

        binding.Submit.setOnClickListener {
            myRef.setValue(test.get(0))
            myRef2.setValue(test.get(1))
            myRef3.setValue(test.get(2))
        }

        binding.Read.setOnClickListener {
            gasStatus.get().addOnSuccessListener{
                Log.d("Firebase","${it.value}")
            }
        }
    }
}