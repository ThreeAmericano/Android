package com.psw9999.car2smarthome

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.LoginActivity.Companion.realtimeFirebase
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.timer

// 코틀린은 open 키워드가 없는 경우 다른 곳에서 상속 받지 못하는 final class로 정의됨.
open class MQTT_Thread : Thread() {
    protected val sendJSONData  = org.json.JSONObject()
    private val factory = ConnectionFactory()
    protected val consumerTag = "PSW_android"
    protected lateinit var message : String
    protected val timerEscape : Int = 30
    protected var timerCount : Int = 0

    // 초기화 영역, 코틀린은 별도의 생성자 영역이 없기 때문에 init 영역에서 초기화를 해주어야 한다.
    init{
        // Connection 정보 생성
        factory.host = "211.179.42.130"
        factory.port = 5672
        factory.username = "rabbit"
        factory.password = "MQ321"
    }

    // Connection : Application과 RabbitMQ Broker 사이의 TCP 연결
    // 미리 연결시키는 것이 아닌 객체 생성시 그 때부터 연결 시작
    protected val connection by lazy {factory.newConnection()!!}
    // Channel : connection 내부에 정의된 가상의 연결, queue에서 데이터를 손볼 때 생기는 일종의 통로 같은 개념
    // !! : 변수가 null이라면 null을 출력하는 것이 아니라 오류를 발생하여 프로그래머에게 알려준다.
    // 변수가 null일 시 KotlinNullPointerException 오류(예외)를 발생시킴.
    protected val channel by lazy { connection.createChannel()!! }

    protected val cancelCallback = CancelCallback { consumerTag : String? ->
        // 매개변수 context 문제로 인해 오류 발생함.
//        Toast.makeText(
//            activity, "ID 혹은 PW 값을 입력해주세요!",
//            Toast.LENGTH_SHORT
//        ).show()
        Log.d("Consume", "$consumerTag was canceled" )
    }
}

class SigninThread(uid : String, mContext : Context) : MQTT_Thread() {

    //private var weatherInfomation : DatabaseReference = Firebase.database.reference
    private val mainIntent = Intent(mContext, MainActivity::class.java)

    val context = mContext

    var signInStatus : Int = 0

    init {
        sendJSONData.put("Producer", "android")
        sendJSONData.put("command", "signin")
        sendJSONData.put("UID", uid)
    }


    // 추후 사용자 이름, 날씨, 스케줄 한꺼번에 가져오도록 수정
    val signInCallback = DeliverCallback { consumerTag, delivery ->
        message = String(delivery.body)

        var userName = JSONObject(message).getString("name")

        //메세지를 수신완료 했으면 mqtt 브로커와 TCP 연결을 끊음.
        channel.close()
        connection.close()

        // 이름을 정상적으로 수신시 Status 값을 1 증가시킴.
        // 추후 상태값이 일정 값 이상이면 스레드를 종료시키도록 구현 예정
        signInStatus += 1
        Log.d("Login Success","$userName")

        // 매개변수로 context 전달받아 intent 수행.
        mainIntent.putExtra("userName",userName)
        if(signInStatus == 2) {
            context.startActivity(mainIntent)
        }
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
        realtimeFirebase.child("sensor").child("openweather").get().addOnSuccessListener {
            MainActivity.weather = Weather(
                air_level = it?.child("air_level")!!.value.toString(),
                description = it?.child("description")!!.value.toString(),
                icon = it?.child("icon")!!.value.toString(),
                temp = it?.child("temp")!!.value.toString(),
                update = it?.child("update")!!.value.toString()
            )
            // 날씨 정보 정상 취합시 1 증가시킴.
            signInStatus += 1
            if (signInStatus == 3) {
                context.startActivity(mainIntent)
            }
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
                mode = it?.child("mode")!!.value.toString().toInt(),
                airconEnabled = status[0].toInt(),
                windPower =  status[1].toInt(),
                lightEnabled = status[2].toInt(),
                lightBrightness = status[3].toInt(),
                lightColor = status[4].toInt(),
                lightMod = status[5].toInt(),
                windowStatus = status[6].toInt(),
                gasValveStatus = status[7].toInt(),
            )
            signInStatus += 1
            if (signInStatus == 3) {
                context.startActivity(mainIntent)
            }
            Log.d("FB applianceStatus", "${MainActivity.applianceStatus}")
        }.addOnFailureListener {
            // 파이어베이스에서 데이터 수신 실패시
            Log.d("firebase weather", "Error")
            Log.e("firebase", "Error getting data", it)
        }
    }
}