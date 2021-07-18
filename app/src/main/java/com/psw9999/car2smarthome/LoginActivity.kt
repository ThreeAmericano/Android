package com.psw9999.car2smarthome

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.psw9999.car2smarthome.databinding.ActivityLoginBinding
import java.util.concurrent.ConcurrentLinkedDeque

class LoginActivity : AppCompatActivity() {

    val binding by lazy {ActivityLoginBinding.inflate(layoutInflater)}
    //
    private lateinit var ID_value : String
    private lateinit var PW_value : String

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // 1. 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스르 가져온다.
        auth = Firebase.auth
        // [END initialize_auth]
        //setContentView(R.layout.activity_login)

        setContentView(binding.root)

        val intent = Intent(this, SignupActivity::class.java)

        // 바뀔때마다 변수값이 변경되므로 개선 필요함.
       binding.IDInput.addTextChangedListener {
            ID_value = it.toString()
        }
       binding.PasswordInput.addTextChangedListener{
            PW_value  = it.toString()
        }

       // binding.SignupText.setText(spannable, TextView.BufferType.SPANNABLE)
        val clickSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(baseContext, "회원가입",
                    Toast.LENGTH_SHORT).show()
            }
        }
        val spannableText = binding.SignupText.text as Spannable
        // 글씨 색과 굵기를 setSpan 한번으로 설정 가능한지 확인

        spannableText.setSpan(ForegroundColorSpan(Color.RED), 14, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableText.setSpan(StyleSpan(Typeface.BOLD),14,18,Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableText.setSpan(clickSpan,14,18,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.SignupText.setOnClickListener {
            startActivity(intent)
        }

        binding.LoginButton.setOnClickListener {
//            binding.IDInput.addTextChangedListener {
//                ID_value = it.toString()
//            }
//
//            binding.PasswordInput.addTextChangedListener {
//                PW_value = it.toString()
//            }
            Log.d("IDPW","ID:${ID_value},PW:${PW_value}")
            signIn(ID_value, PW_value)
        }

    }

    public override fun onStart() {
        super.onStart()
        // 2. 활동을 초기화할 때 사용자가 현재 로그인 중인지 확인한다.
        val currentUser = auth.currentUser
        if(currentUser != null) {
            //reload()
        }
    }

    // NULL에 대한 처리 필요
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.d("debug", "$user:success")
                    Toast.makeText(baseContext, "로그인 성공!",
                        Toast.LENGTH_SHORT).show()
                    binding.IDInput.setText(null)
                    binding.PasswordInput.setText(null)
                    val MainIntent = Intent(this, MainActivity::class.java)
                    startActivity(MainIntent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("debug", "fail")
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "로그인 실패",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }



}