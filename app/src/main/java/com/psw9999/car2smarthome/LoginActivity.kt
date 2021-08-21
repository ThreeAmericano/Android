package com.psw9999.car2smarthome

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.ListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.databinding.ActivityLoginBinding
import com.rabbitmq.client.*
import java.util.concurrent.ConcurrentLinkedDeque

import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    private lateinit var ID_value: String
    private lateinit var PW_value: String

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    private val singInJSONData = org.json.JSONObject()
    private val factory = ConnectionFactory()
    private val consumerTag = "PSW_android"

    private var userName : String? = null

    val mainIntent by lazy {Intent(this, MainActivity::class.java)}
    //val mainIntent by lazy {Intent(baseContext, MainActivity::class.java)}

    // [END declare_auth]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스르 가져온다.
        auth = Firebase.auth
        // [END initialize_auth]
        setContentView(binding.root)

        val intent = Intent(this, SignupActivity::class.java)

        // binding.SignupText.setText(spannable, TextView.BufferType.SPANNABLE)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(
                    baseContext, "회원가입",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val spannableText = binding.SignupText.text as Spannable
        // 글씨 색과 굵기를 setSpan 한번으로 설정 가능한지 확인

        spannableText.setSpan(
            ForegroundColorSpan(Color.RED),
            14,
            18,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannableText.setSpan(StyleSpan(Typeface.BOLD), 14, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableText.setSpan(clickSpan, 14, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.SignupText.setOnClickListener {
            startActivity(intent)
        }

        binding.LoginButton.setOnClickListener {
            ID_value = binding.IDInput.text.toString()
            PW_value = binding.PasswordInput.text.toString()

             //EditText null 값 입력방지
            if(ID_value.isEmpty() or PW_value.isEmpty()){
                Toast.makeText(
                    baseContext, "ID 혹은 PW 값을 입력해주세요!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else {
                signIn(ID_value, PW_value)
            }
        }

    }

        public override fun onStart() {
            super.onStart()
            // 2. 활동을 초기화할 때 사용자가 현재 로그인 중인지 확인한다.
            val currentUser = auth.currentUser
            if (currentUser != null) {
                //reload()
            }
        }

        private fun signIn(email: String, password: String) {
            // [START sign_in_with_email]
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d("Login", "$user:success")
                        Toast.makeText(
                            baseContext, "로그인 성공!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.IDInput.text = null
                        binding.PasswordInput.text = null

                        // 여기서 전달하는 this는 어떤 것?
                        val signInThread = SigninThread(user!!.uid, this)
                        signInThread.start()

//                        thread(start = true){
//                            try {
//                                singInJSONData.put("Producer", "android")
//                                singInJSONData.put("command", "signin")
//                                singInJSONData.put("UID", user!!.uid)
//
//                                factory.host = "211.179.42.130"
//                                factory.port = 5672
//                                factory.username = "rabbit"
//                                factory.password = "MQ321"
//
//                                val connection = factory.newConnection()
//                                val channel = connection.createChannel()
//
//                                channel.basicPublish(
//                                    "webos.topic",
//                                    "webos.server.info",
//                                    null,
//                                    singInJSONData.toString().toByteArray()
//                                )
//
//                                val deliverCallback = DeliverCallback { consumerTag, delivery ->
//                                    // val meesage?
//                                    var message : String = String(delivery.body)
//                                    Log.d("message","$message")
//                                    val userInfo = JSONObject(message)
//                                    userName = userInfo.getString("name")
//                                    Log.d("Consume","$userName")
//                                    mainIntent.putExtra("userName",userName)
//                                    startActivity(mainIntent)
//                                }
//
//                                val cancelCallback = CancelCallback { consumerTag : String? ->
//                                    Log.d("Consume","[$consumerTag] was canceled")
//                                }
//
//
//                                channel.basicConsume("webos.android",true,consumerTag,deliverCallback,cancelCallback)
//
//                                // 나중에 수정 필요!, onresume시 끊기도록 수정할 것
//                                //channel.close()
//                                //connection.close()
//                                // Thread 실행 중 문제 발생한 경우 다음 catch문 실행
//                            }catch(e : InterruptedException){
//                                e.printStackTrace()
//                            }catch(e : Exception) {
//                                try {
//                                    Thread.sleep(5000) // 네트워크등의 문제로 메시지가 다시 돌아오면, 5초 sleep 후 다시 시도
//                                } catch (e1: InterruptedException) {
//                                    e1.printStackTrace()
//                                }
//                            }
//
//                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("debug", "fail")
                        //Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "로그인 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }
                }
            // [END sign_in_with_email]
        }
    }

