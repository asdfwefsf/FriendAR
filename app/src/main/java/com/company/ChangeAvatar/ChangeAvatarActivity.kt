package com.company.ChangeAvatar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.india1.Key
import com.company.india1.MainActivity
import com.company.india1.databinding.ActivityChangeAvatarBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.sceneview.ar.node.ArModelNode

class ChangeAvatarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeAvatarBinding
    private lateinit var modelNode: ArModelNode
    private var currentUserUid = Firebase.auth.uid!!.toString()
    private var avatar: String? = null // 클래스의 멤버 변수로 선언
    private val Avatar = mutableMapOf<String , Any>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeAvatarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.hulkTestTextView.setOnClickListener {
            modelNode = ArModelNode().apply {
                loadModelGlbAsync( // Sketchfab 사용하기
                    glbFileLocation = "models/hi_android.glb"
                )
                {
                    binding.sceneView1.planeRenderer.isVisible = true
                }

            }
            binding.sceneView1.addChild(modelNode)
        }
        binding.batmanTestTextView.setOnClickListener {
            modelNode = ArModelNode().apply {
                loadModelGlbAsync( // Sketchfab 사용하기
                    glbFileLocation = "models/normal_android.glb"
                )
                {
                    binding.sceneView1.planeRenderer.isVisible = true
                }

            }
            binding.sceneView1.addChild(modelNode)
        }
        binding.madAndroidTestTextView.setOnClickListener {
            modelNode = ArModelNode().apply {
                loadModelGlbAsync( // Sketchfab 사용하기
                    glbFileLocation = "models/mad_android.glb"
                )
                {
                    binding.sceneView1.planeRenderer.isVisible = true
                }

            }
            binding.sceneView1.addChild(modelNode)
        }
        binding.hulkSetTextView.setOnClickListener {
            Firebase.database.reference.child(Key.DV_AVATAR).child("hi_android").addListenerForSingleValueEvent(object :
                ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val avatarValue = snapshot.getValue(String::class.java)
                        val intent = Intent(this@ChangeAvatarActivity, MainActivity::class.java)
//                        intent.putExtra("avatarValue", avatarValue)
                        Avatar["avatar"] = avatarValue.toString()
                        Firebase.database.reference.child(Key.DB_USERS).child(currentUserUid).updateChildren(Avatar)
                        startActivity(intent)
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                }
            )
        }
        binding.batmanSetTextView.setOnClickListener {
            Firebase.database.reference.child(Key.DV_AVATAR).child("normal_android").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val avatarValue = snapshot.getValue(String::class.java)
                    val intent = Intent(this@ChangeAvatarActivity, MainActivity::class.java)
//                    intent.putExtra("avatarValue", avatarValue)
                    Avatar["avatar"] = avatarValue.toString()
                    Firebase.database.reference.child(Key.DB_USERS).child(currentUserUid).updateChildren(Avatar)
                    startActivity(intent)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            }
            )
        }
        binding.madAndroidSetTextView.setOnClickListener {
            Firebase.database.reference.child(Key.DV_AVATAR).child("mad_android").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val avatarValue = snapshot.getValue(String::class.java)
                    val intent = Intent(this@ChangeAvatarActivity, MainActivity::class.java)
//                    intent.putExtra("avatarValue", avatarValue)
                    Avatar["avatar"] = avatarValue.toString()
                    Firebase.database.reference.child(Key.DB_USERS).child(currentUserUid).updateChildren(Avatar)
                    startActivity(intent)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            }
            )
        }


    }


}