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
        // 객체로 선언할지 변수로 선언할지 고민..
        // 다른 액티비티에서 객체 생성마다 firebase와 연결시도?
        lateinit var auth: FirebaseAuth
        // RealtimeFirebase 정보를 가져와 저장할 객체
        val realtimeFirebase : DatabaseReference by lazy { Firebase.database.reference }
    }

    init {
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스로 가져온다.
        // [START declare_auth]
        auth = Firebase.auth
    }

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private lateinit var ID_value: String
    private lateinit var PW_value: String

    private val controlThread = ControlThread()

    // [START declare_auth]
    //private lateinit var auth: FirebaseAuth



    val mainIntent by lazy {Intent(this, MainActivity::class.java)}
    //val mainIntent by lazy {Intent(baseContext, MainActivity::class.java)}

    // [END declare_auth]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스르 가져온다.
        //auth = Firebase.auth
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

                        // firebase에서
                        // 여기서 전달하는 this는 어떤 것?
                        val signInThread = SigninThread(user!!.uid, this)
                        signInThread.start()

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

