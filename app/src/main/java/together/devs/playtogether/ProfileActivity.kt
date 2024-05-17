package together.devs.playtogether

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import together.devs.playtogether.firebase.UserManager
import together.devs.playtogether.info.CourtInfo
import together.devs.playtogether.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var userProfileImageView: ImageView

    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userManager = UserManager(Firebase.database)
        userProfileImageView = findViewById(R.id.userProfileImageView)

        val addCourtButton: Button = findViewById(R.id.addCourtButton)
        val friendsButton: Button = findViewById(R.id.friendsButton)
        val createEventButton: Button = findViewById(R.id.createEventButton)
        val createTeamButton: Button = findViewById(R.id.createTeamButton)
        val viewHistoryButton: Button = findViewById(R.id.viewHistoryButton)
        val editProfileButton: Button = findViewById(R.id.editProfileButton)
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)

        // Initialize the ActivityResultLauncher for EditProfileActivity
        editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Reload the profile image
                val userId = Firebase.auth.currentUser?.uid
                if (userId != null) {
                    loadProfileImage(userId)
                }
                // Reload user name and email
                loadUserProfile()
            }
        }

        // Load user profile data
        loadUserProfile()

        friendsButton.setOnClickListener {
            intent = Intent(this, ContactListActivity::class.java)
            startActivity(intent)
        }

        createEventButton.setOnClickListener {
            intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
        }

        addCourtButton.setOnClickListener {
            intent = Intent(this, AddCourt::class.java)
            startActivity(intent)
        }

        createTeamButton.setOnClickListener {
            intent = Intent(this, CreateTeam::class.java)
            startActivity(intent)
        }

        viewHistoryButton.setOnClickListener {
            intent = Intent(this, CourtInfo::class.java)
            startActivity(intent)
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }
    }

    private fun loadUserProfile() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            userManager.getUser(it.uid) { user ->
                user?.let { userData ->
                    findViewById<TextView>(R.id.userNameTextView).text = userData.userName
                    loadProfileImage(it.uid)
                }
            }
        }
    }

    private fun loadProfileImage(userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(userProfileImageView)
        }.addOnFailureListener { e ->
            // Handle any errors
        }
    }
}
