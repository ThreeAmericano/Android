package com.psw9999.car2smarthome

import android.app.ProgressDialog
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.databinding.ActivityLoginBinding
import com.rabbitmq.client.*
import java.util.concurrent.ConcurrentLinkedDeque

import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    companion object  {
        lateinit var auth: FirebaseAuth
        // RealtimeFirebase 정보를 가져와 저장할 객체
        val realtimeFirebase : DatabaseReference by lazy { Firebase.database.reference }
        var signInStatus : Int = 0
        lateinit var progressDialog : ProgressDialog
        var loginFailFlag : Boolean? = false
    }

    init {
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스로 가져온다.
        // [START declare_auth]
        auth = Firebase.auth

    }

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    val mainIntent by lazy {Intent(this, MainActivity::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스르 가져온다.
        //auth = Firebase.auth
        // [END initialize_auth]
        setContentView(binding.root)
        val intent = Intent(this, SignupActivity::class.java)

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


        binding.LoginButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val ID_Input = binding.editTextID.text.toString()
                val PW_Input = binding.editTextPassword.text.toString()

                // editText는 비어있는 경우에 null 값이 아니므로 Empty 등의 방법을 활용하여 값을 체크할 것
                if (ID_Input.isEmpty() or PW_Input.isEmpty()) {
                    Toast.makeText(
                        baseContext, "아이디 혹은 비밀번호를 입력해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    signIn(ID_Input, PW_Input)
                }
            }
        })

        binding.testButton.setOnClickListener {
            signIn("psw1234@naver.com", "123412")
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
            progressDialog = ProgressDialog(this)

            with(progressDialog) {
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setMessage("로그인 중입니다...")
                setCancelable(false)
                show()
            }

            Thread {
                try {
                    while(signInStatus < 6) {
                        // 0.1초 마다 체크
                        if(loginFailFlag == true)
                        {
                            loginFailFlag = false
                            progressDialog.dismiss()
                            return@Thread
                        }
                        Thread.sleep(100)
                    }
                    Log.d("progressDialog","success")
                    signInStatus = 0
                    progressDialog.dismiss()
                    startActivity(mainIntent)
                }catch (e:Exception) {
                    e.printStackTrace()
                }
            }.start()

            // [START sign_in_with_email]
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        binding.editTextID.text = null
                        binding.editTextPassword.text = null


                        val signInThread = SigninThread(user!!.uid)
                        signInThread.start()

                    } else {
                        loginFailFlag = true
                        Toast.makeText(
                            baseContext, "로그인실패, ID나 Password를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }
                }
            // [END sign_in_with_email]
        }
    }

