package com.company.india1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.company.ChangeAvatar.ChangeAvatarActivity
import com.company.Friend.FriendActivity
import com.company.findFriend.FindFriendActivity
import com.company.india1.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode


class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var sceneView: ArSceneView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var modelNode: ArModelNode
    private var avatarName : String? = null
    private var currentUserUid = Firebase.auth.uid!!.toString()
    val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        binding.placeButton.setOnClickListener {
            modelNode = ArModelNode().apply {
//                avatarName = intent.getStringExtra("avatarValue")
                Firebase.database.reference.child(Key.DB_USERS).child(currentUserUid).addListenerForSingleValueEvent(
                    object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            avatarName = snapshot.child("avatar").getValue(String::class.java)
                            loadModelGlbAsync( // Sketchfab 사용하기
                                glbFileLocation = "models/$avatarName"
                            )
                            {
                                binding.sceneView.planeRenderer.isVisible = true
                            }
                            onAnchorChanged = {
                                binding.placeButton.isGone
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }
                )
            }

//                Toast.makeText(this@MainActivity,"$avatarName",Toast.LENGTH_SHORT).show()
//                loadModelGlbAsync( // Sketchfab 사용하기
//                    glbFileLocation = "models/$avatarName"
//                )
//                {
//                    binding.sceneView.planeRenderer.isVisible = true
//                }
//                onAnchorChanged = {
//                    binding.placeButton.isGone
//                }

            binding.sceneView.addChild(modelNode)

            placeModel()
        }
        binding.goToFriendButton.setOnClickListener {
            val intent = Intent(this , FriendActivity::class.java)
            startActivity(intent)
        }

        binding.deleteAuth.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://geonnuyasha.com/product"))
            startActivity(intent)
        }


        binding.bgmPlayButton.setOnClickListener { bgmPlay() }
        binding.bgmStopButton.setOnClickListener { bgmPause() }

        binding.findFriendButton.setOnClickListener {
            val intent = Intent(this , FindFriendActivity::class.java)
            startActivity(intent)
        }

        binding.changeAvatarButton.setOnClickListener {
            val avatar = mutableMapOf<String,Any>()
            avatar["hi_android"] = "hi_android.glb"
            avatar["normal_android"] = "normal_android.glb"
            avatar["mad_android"] = "mad_android.glb"

            Firebase.database.reference.child(Key.DV_AVATAR).updateChildren(avatar)
            intent = Intent(this , ChangeAvatarActivity::class.java)
            startActivity(intent)
        }

    }





    private fun placeModel() {
        modelNode.anchor()
        sceneView.planeRenderer.isVisible = false
    }

    private fun bgmPlay() {
        mediaPlayer = MediaPlayer.create(baseContext, R.raw.sns).apply {
            start()
        }
    }

    private fun bgmPause() {
        mediaPlayer?.pause()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                // 모든 권한이 허용되었을 때의 동작
            } else {
                // 권한 중 하나라도 허용되지 않았을 때의 동작

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    showPermissionRationalDialog()
                }
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.CAMERA
            )

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ||
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            ) {
                showPermissionRationalDialog()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(permissions)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("알림 권한이 없으면 알림을 받을 수 없습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.CAMERA
                    )
                )
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }


}



