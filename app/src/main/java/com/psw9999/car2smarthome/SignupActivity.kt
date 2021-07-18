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

class SignupActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var SignupName : String
    private lateinit var SignupID : String
    private lateinit var SignupPW : String
    private lateinit var SignupCheckPW : String

    val database = Firebase.database
    val myUsers = database.getReference("users")

    val binding by lazy { ActivitySignupBinding.inflate(layoutInflater)}

    // 이름, ID, PW 저장을 위한 해시맵 생성
    val hashMap:HashMap<String ,String> = HashMap<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_signup)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.NameInput.addTextChangedListener {
            SignupName = it.toString()
        }
        binding.SingupIDInput.addTextChangedListener {
            SignupID = it.toString()
        }
        binding.SignupPasswordInput.addTextChangedListener {
            SignupPW = it.toString()
        }
        binding.SignupPasswordCheckInput.addTextChangedListener {
            SignupCheckPW = it.toString()
        }


        // 등록버튼 눌렀을 시 -> 파이어베이스에 ID, PW를 저장한다.
        binding.RegisterButton.setOnClickListener {
            if(SignupPW.equals(SignupCheckPW)) {
                createAccount(SignupID,SignupPW)
            }
            else {
                Toast.makeText(baseContext, "비밀번호가 서로 다릅니다. 다시 입력하세요.",
                    Toast.LENGTH_SHORT).show()

                // 비밀번호 서로 다르면 해당 editText값 초기화시키기
                binding.SignupPasswordInput.setText(null)
                binding.SignupPasswordCheckInput.setText(null)
            }
        }

        auth = Firebase.auth

    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // 가입 성공시
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TEST", "createUserWithEmail:success")
                    val user = auth.currentUser
                    // 해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    user?.let {
                        val uid = user.uid
                    }

                    val uids = database.getReference(user!!.uid.toString())

                    //hashMap.put("uid",user!!.uid.toString())
                    hashMap.put("name",SignupName)
                    hashMap.put("email",SignupID)

                    uids.setValue(hashMap)

                    // 회원가입 성공시 로그인 창으로 돌아감.
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    startActivity(loginIntent)
                    Toast.makeText(baseContext, "회원가입 성공",
                       Toast.LENGTH_SHORT).show()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TEST", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "회원가입 실패",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
        // [END create_user_with_email]
    }
}