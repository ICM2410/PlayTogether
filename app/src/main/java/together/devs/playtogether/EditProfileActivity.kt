package together.devs.playtogether

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import together.devs.playtogether.firebase.UserManager
import together.devs.playtogether.model.User

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var imageViewProfile: ImageView
    private lateinit var userManager: UserManager
    private lateinit var teamsListView: ListView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
        userManager = UserManager(FirebaseDatabase.getInstance())

        userNameEditText = findViewById(R.id.userNameEditText)
        imageViewProfile = findViewById(R.id.profile_image)
        teamsListView = findViewById(R.id.teamsListView)

        loadUserProfile()

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            imageViewProfile.setImageURI(uri)
        }

        findViewById<FloatingActionButton>(R.id.pickImage).setOnClickListener {
            getContent.launch("image/*")
        }

        findViewById<MaterialButton>(R.id.saveButton).setOnClickListener {
            val userName = userNameEditText.text.toString()
            if (selectedImageUri != null) {
                userManager.uploadProfileImage(auth.currentUser!!.uid, selectedImageUri!!, userName)
            } else {
                userManager.updateUserProfile(auth.currentUser!!.uid, "", userName)
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
                val user = dataSnapshot.getValue<User>()
                user?.let { userData ->
                    userNameEditText.setText(userData.userName)
                    val teamsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userData.teams)
                    teamsListView.adapter = teamsAdapter
                    loadProfileImage(userData.profileImageUrl)
                }
            }
        }
    }

    private fun loadProfileImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageUrl).into(imageViewProfile)
        } else {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val profileImageRef = storageRef.child("profile_images/$userId.jpg")

                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(imageViewProfile)
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Error loading profile image", e)
                }
            }
        }
    }
}
