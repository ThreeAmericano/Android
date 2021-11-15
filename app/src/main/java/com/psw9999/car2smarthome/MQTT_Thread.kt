package com.psw9999.car2smarthome

import android.app.ProgressDialog
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.LoginActivity.Companion.loginFailFlag
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase
import com.psw9999.car2smarthome.LoginActivity.Companion.signInStatus
import com.psw9999.car2smarthome.MainActivity.Companion.guideText
import com.psw9999.car2smarthome.ThirdFragment.Companion.scheduleDatas
import com.psw9999.car2smarthome.data.Appliance
import com.psw9999.car2smarthome.data.alarmData
import com.psw9999.car2smarthome.data.mode
import com.psw9999.car2smarthome.data.scheduleData
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer

open class MQTT_Thread : Thread() {
    protected val sendJSONData  = org.json.JSONObject()
    private val factory = ConnectionFactory()
    protected val consumerTag = "PSW_android"
    protected lateinit var message : String
    protected val firebaseDB = Firebase.firestore

    init{
        // Connection 정보 생성
        factory.host = "211.179.42.130"
        factory.port = 5672
        factory.username = "rabbit"
        factory.password = "MQ321"
    }

    // Connection : Application과 RabbitMQ Broker 사이의 TCP 연결
    // 미리 연결시키는 것이 아닌 객체 생성시 그 때부터 연결 시작
    protected val connection by lazy { factory.newConnection()!! }
    // Channel : connection 내부에 정의된 가상의 연결, queue에서 데이터를 손볼 때 생기는 일종의 통로 같은 개념
    protected val channel by lazy { connection.createChannel()!! }

    protected val cancelCallback = CancelCallback { consumerTag : String? ->
        Log.d("Consume", "$consumerTag was canceled" )
    }
}

class SigninThread(uid : String) : MQTT_Thread() {
    init {
        sendJSONData.put("Producer", "android")
        sendJSONData.put("command", "signin")
        sendJSONData.put("UID", uid)
    }

    val signInCallback = DeliverCallback { consumerTag, delivery ->
        message = String(delivery.body)

        var userName = JSONObject(message).getString("name")

        //메세지를 수신완료 했으면 mqtt 브로커와 TCP 연결을 끊음.
        channel.close()
        connection.close()

        signInStatus += 1
        Log.d("signInStatus","$signInStatus")
        Log.d("Login Success","$userName")

        // 매개변수로 context 전달받아 intent 수행.
        //mainIntent.putExtra("userName",userName)
        MainActivity.userName = userName
    }

    // 예외 상황에 관한 try catch문 추가가 필요할듯 (ex : 통신이 끊겼을 때?)
    override fun run() {
        // 1. uid를 MQTT서버로 송신한다.
        // exchange
        // routing key
        // props : other properties for the message - routing headers etc.
        // body : the message body
        channel.basicPublish(
            "webos.topic",
            "webos.server.info",
            null,
            sendJSONData.toString().toByteArray()
        )

        // 2. "webos.android" 큐에 접근하여 이름을 가져오는 콜백함수를 실행시킴.
        channel.basicConsume(
            "webos.android",
            true,
            consumerTag,
            signInCallback,
            cancelCallback
        )

        //3. firebase에 접근하여 날씨데이터를 취합하여 Weather data class에 저장함.
        realtimeFirebase.child("sensor").get().addOnSuccessListener {
            MainActivity.weather = Weather(
                air_level = it?.child("openweather")!!.child("air_level")!!.value.toString(),
                description = it?.child("openweather")!!.child("description")!!.value.toString(),
                icon = it?.child("openweather")!!.child("icon")!!.value.toString(),
                temp = it?.child("openweather")!!.child("temp")!!.value.toString(),
                update = it?.child("openweather")!!.child("update")!!.value.toString(),
                humidity = it?.child("hometemp")!!.child("humi")!!.value.toString().toFloat(),
                temperature = it?.child("hometemp")!!.child("temp")!!.value.toString().toFloat(),
            )

            // 날씨 정보 정상 취합시 1 증가시킴.
            signInStatus += 1
            Log.d("signInStatus","$signInStatus")
            Log.d("firebase weather", "${MainActivity.weather}")
        }.addOnFailureListener {
            // 파이어베이스에서 데이터 수신 실패시
            Log.d("firebase weather", "Error")
            Log.e("firebase", "Error getting data", it)
        }

        // 4. 현재 모드 상태와 가전 동작 상태를 취합하여 ApplianceStatus에 저장함.
        realtimeFirebase.child("smarthome").get().addOnSuccessListener {
            var status = it?.child("status")!!.value.toString().chunked(1)
            MainActivity.applianceStatus = ApplianceStatus(
                mode = status[0].toInt(),
                airconEnabled = status[1].toInt(),
                windPower =  status[2].toInt(),
                lightEnabled = status[3].toInt(),
                lightBrightness = status[4].toInt(),
                lightColor = status[5].toInt(),
                lightMod = status[6].toInt(),
                windowStatus = status[7].toInt(),
                gasValveStatus = status[8].toInt(),
            )
            signInStatus += 1
            Log.d("signInStatus","$signInStatus")
            Log.d("FB applianceStatus", "${MainActivity.applianceStatus}")
        }.addOnFailureListener {
            // 파이어베이스에서 데이터 수신 실패시
            Log.e("firebase", "Error getting data", it)
            loginFailFlag = true
        }

        realtimeFirebase.child("server").get().addOnSuccessListener {
            guideText = it.child("notification").value.toString()
            signInStatus += 1
        }.addOnFailureListener {
            // 파이어베이스에서 데이터 수신 실패시
            Log.e("firebase", "Error getting data", it)
            loginFailFlag = true
        }



        // 5. 파이어베이스의 클라우드에서 모드 정보 가져오기
        firebaseDB.collection("modes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("firebaseStore", "${document.id} => ${document.data}")
                    SecondFragment.modeDatas.apply {
                        add(mode(
                                 modeName = document.data["modeName"].toString(),
                                 airconEnable = document.data["airconEnable"].toString().toBoolean(),
                                 airconWindPower = document.data["airconWindPower"].toString().toInt(),
                                 lightEnable = document.data["lightEnable"].toString().toBoolean(),
                                 lightBrightness = document.data["lightBrightness"].toString().toInt(),
                                 lightColor = document.data["lightColor"].toString().toInt(),
                                 lightMode = document.data["lightMode"].toString().toInt(),
                                 gasValveEnable = document.data["gasValveEnable"].toString().toBoolean(),
                                 windowOpen = document.data["windowOpen"].toString().toBoolean(),
                                 modeNum = document.data["modeNum"].toString().toInt()
                        ))
                    }
                }
                signInStatus += 1
                Log.d("signInStatus","$signInStatus")
            }
            .addOnFailureListener { exception ->
                Log.d("firebaseStore", "Error getting documents: ", exception)
            }

        // 6. 파이어베이스의 클라우드에서 스케줄 정보 가져오기
        firebaseDB.collection("schedule_mode")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    scheduleDatas.apply {
                        val scheduleData = document.toObject<scheduleData>()
                        add(scheduleData)
                    }
                }
                signInStatus += 1
                Log.d("signInStatus","$signInStatus")
            }

            .addOnFailureListener { exception ->
                Log.d("firebaseStore", "Error getting documents: ", exception)
            }

    }
}



class ControlThread : MQTT_Thread() {
    fun sendMQTT(message : String) {
        thread(true) {
            channel.basicPublish(
                "webos.topic",
                "webos.smarthome",
                null,
                message.toByteArray()
            )
        }
    }

}