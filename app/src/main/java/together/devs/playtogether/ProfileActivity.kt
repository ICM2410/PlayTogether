package together.devs.playtogether

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import together.devs.playtogether.firebase.UserManager
import together.devs.playtogether.info.CourtInfo

class ProfileActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var userProfileImageView: ImageView

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
        val userEmailTextView: TextView = findViewById(R.id.userEmailTextView)

        // Load user name and email
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            userManager.getUser(it.uid) { user ->
                user?.let { userData ->
                    userNameTextView.text = userData.userName
                    userEmailTextView.text = currentUser.email
                    loadProfileImage(currentUser.uid)
                }
            }
        }

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
            Firebase.auth.signOut()
            intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
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
