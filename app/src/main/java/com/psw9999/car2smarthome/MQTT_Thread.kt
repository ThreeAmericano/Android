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
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import org.json.JSONObject

// 코틀린은 open 키워드가 없는 경우 다른 곳에서 상속 받지 못하는 final class로 정의됨.
open class MQTT_Thread : Thread() {
    protected val sendJSONData  = org.json.JSONObject()
    private val factory = ConnectionFactory()
    protected val consumerTag = "PSW_android"
    protected lateinit var message : String

    // 초기화 영역, 코틀린은 별도의 생성자 영역이 없기 때문에 init 영역에서 초기화를 해주어야 한다.
    init{
        // Connection 정보 생성
        factory.host = "211.179.42.130"
        factory.port = 5672
        factory.username = "rabbit"
        factory.password = "MQ321"
    }

    protected val connection by lazy {factory.newConnection()!!}

    protected val channel by lazy { connection.createChannel()!! }

    // Connection : Application과 RabbitMQ Broker 사이의 TCP 연결
    //private val connection = factory.newConnection()

    // Channel : connection 내부에 정의된 가상의 연결, queue에서 데이터를 손볼 때 생기는 일종의 통로 같은 개념
    //protected val channel = connection.createChannel()!!
    // !! : 변수가 null이라면 null을 출력하는 것이 아니라 오류를 발생하여 프로그래머에게 알려준다.
    // 변수가 null일 시 KotlinNullPointerException 오류(예외)를 발생시킴.

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

    private var weatherInfomation : DatabaseReference = Firebase.database.reference
    private val mainIntent = Intent(mContext, MainActivity::class.java)

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
        //channel.close()
        //connection.close()

        // 이름을 정상적으로 수신시 Status 값을 1 증가시킴.
        signInStatus +=1
        Log.d("Login Success","$userName")

        // 매개변수로 context 전달받아 intent 수행.
        //mainIntent.putExtra("userName",userName)
        //mContext.startActivity(mainIntent)
    }

    // 예외 상황에 관한 try catch문 추가가 필요할듯
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

        channel.basicConsume("webos.android",true,consumerTag,signInCallback,cancelCallback)

    }
}

// data class : 데이터 보관 목적으로 만든 클래스
@IgnoreExtraProperties
data class weather(
    var air_level : String? = "",
    var description: String? = "",
    var icon : String? = "",
    var temp : String? = "",
    var update : String? = ""
) {
}