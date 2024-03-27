package com.company.india1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.india1.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {// 회원가입 성공
                    Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show()
                } else {// 회원가입 실패
                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signInButton.setOnClickListener {// 로그인 기능을 구현한다.
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    val currentUser = Firebase.auth.currentUser
                    if (task.isSuccessful && currentUser != null) {
                        val userId = Firebase.auth.currentUser!!.uid
                        Log.d("test" , userId)
                        Firebase.messaging.token.addOnCompleteListener {
                            Log.d("test" , "토근성공함")

                            val token = it.result
                            val userItem = mutableMapOf<String,Any>()
                            userItem["userUid"] = userId
                            userItem["userName"] = email
                            userItem["userState"] = "대기"
                            userItem["messageFcmToken"] = token
                            Firebase.database.reference.child(Key.DB_USERS).child(userId)
                                .updateChildren(userItem)
                            val intent = Intent(this , MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        Firebase.messaging.token.addOnFailureListener {
                            Log.d("test" , "토근에러났음")

                        }
  
                        
                        } else {
                        Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Log.d("test" , "에러났음")
                }
            }



    }
//
//    override fun onStart() {
//        super.onStart()
//        if (Firebase.auth.currentUser != null) {
//            val intentt = Intent(this@LoginActivity , MainActivity::class.java)
//            startActivity(intentt)
//        }
//    }



}