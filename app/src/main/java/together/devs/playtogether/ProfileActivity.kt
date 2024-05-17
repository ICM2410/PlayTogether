package together.devs.playtogether

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import together.devs.playtogether.firebase.UserManager
import together.devs.playtogether.info.CourtInfo

class ProfileActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userManager = UserManager(Firebase.database)

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
                user?.let {
                    userNameTextView.text = user.userName
                    userEmailTextView.text = currentUser.email
                }
            }
        }

        friendsButton.setOnClickListener {
            intent = Intent(this, ContactListActivity::class.java)
            startActivity(intent)
        }

        createEventButton.setOnClickListener {
            intent = Intent(this, CreacionEvento::class.java)
            startActivity(intent)
        }

        addCourtButton.setOnClickListener {
            intent = Intent(this, AddCourt::class.java)
            startActivity(intent)
        }

        createTeamButton.setOnClickListener {
            intent = Intent(this, CrearEquipo::class.java)
            startActivity(intent)
        }

        viewHistoryButton.setOnClickListener {
            intent = Intent(this, CourtInfo::class.java)
            startActivity(intent)
        }

        editProfileButton.setOnClickListener {
            Firebase.auth.signOut()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
