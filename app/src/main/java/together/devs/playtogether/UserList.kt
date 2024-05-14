import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import together.devs.playtogether.UserChat
import together.devs.playtogether.databinding.ActivityUserListBinding

class UserList : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.reference.child("onlineUsers")
        subscribeToUserChanges()
    }

    private fun subscribeToUserChanges() {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.listUsers.removeAllViews() // Clear the list to avoid duplicate entries
                snapshot.children.forEach { child ->
                    val userId = child.key ?: return@forEach
                    val textView = TextView(baseContext)
                    textView.text = "User ID: $userId is online"
                    textView.setOnClickListener {
                        startChatActivity(userId)
                    }
                    binding.listUsers.addView(textView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserList", "Database access was cancelled or failed: ${error.message}")
            }
        }
        usersRef.addValueEventListener(listener)
    }

    private fun startChatActivity(userId: String) {
        val intent = Intent(this, UserChat::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
