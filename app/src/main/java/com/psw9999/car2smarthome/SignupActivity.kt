package com.psw9999.car2smarthome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.databinding.ActivitySignupBinding

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.lang.Exception
import kotlin.concurrent.thread


class SignupActivity : AppCompatActivity() {
    private lateinit var SignupName : String
    private lateinit var SignupID : String
    private lateinit var SignupPW : String
    private lateinit var SignupCheckPW : String

    private val factory = ConnectionFactory()
    private val jobj = org.json.JSONObject()

    val database = Firebase.database

    val binding by lazy { ActivitySignupBinding.inflate(layoutInflater)}

    // 이름, ID, PW 저장을 위한 해시맵 생성
    val hashMap:HashMap<String ,String> = HashMap<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // 등록버튼 눌렀을 시 -> 파이어베이스에 ID, PW를 저장한다.
        binding.RegisterButton.setOnClickListener {
            SignupName = binding.NameInput.text.toString()
            SignupID = binding.SingupIDInput.text.toString()
            SignupPW = binding.SignupPasswordInput.text.toString()
            SignupCheckPW = binding.SignupPasswordCheckInput.text.toString()

            if (SignupName.isEmpty() or SignupID.isEmpty() or SignupPW.isEmpty() or SignupCheckPW.isEmpty()) {
                Toast.makeText(
                    baseContext, "항목을 모두 입력해주세요!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (SignupPW == SignupCheckPW) {
                    createAccount(SignupID, SignupPW)
                } else {
                    Toast.makeText(
                        baseContext, "비밀번호가 서로 다릅니다. 다시 입력하세요.",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.SignupPasswordInput.text = null
                    binding.SignupPasswordCheckInput.text = null
                }
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        val signUpAuth : FirebaseAuth = Firebase.auth
        // [START create_user_with_email]
        signUpAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // 가입 성공시
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TEST", "createUserWithEmail:success")
                    val user = signUpAuth.currentUser
                    thread(start = true){
                        try {
                            jobj.put("Producer", "android")
                            jobj.put("command", "signup")
                            jobj.put("UID", user!!.uid)
                            jobj.put("name", SignupName)

                            factory.host = "211.179.42.130"
                            factory.port = 5672
                            factory.username = "rabbit"
                            factory.password = "MQ321"
                            val connection = factory.newConnection()
                            val channel = connection.createChannel()

                            channel.basicPublish(
                                "webos.topic",
                                "webos.server.info",
                                null,
                                jobj.toString().toByteArray()
                            )
                            channel.close()
                            connection.close()
                        // Thread 실행 중 문제 발생한 경우 다음 catch문 실행
                        }catch(e : InterruptedException){
                            e.printStackTrace()
                        }catch(e : Exception) {
                            try {
                                Thread.sleep(5000) // 네트워크등의 문제로 메시지가 다시 돌아오면, 5초 sleep 후 다시 시도
                            } catch (e1: InterruptedException) {
                                e1.printStackTrace()
                            }
                        }
                    }
                    Toast.makeText(this, "회원가입 성공",
                       Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TEST", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "회원가입 실패",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }
}