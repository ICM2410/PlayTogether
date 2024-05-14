import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import together.devs.playtogether.Message
import together.devs.playtogether.databinding.ActivityUserChatBinding

class UserChat : AppCompatActivity() {

    private lateinit var binding: ActivityUserChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var chatRef: DatabaseReference
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeFirebase()

        val otherUserId = intent.getStringExtra("userId") ?: return
        val currentUserId = auth.currentUser?.uid ?: return
        setupChat(currentUserId, otherUserId)
        setupMessageSending()
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    private fun setupChat(currentUserId: String, otherUserId: String) {
        chatRef = database.reference.child("chats").child(currentUserId).child(otherUserId)
        adapter = MessageAdapter(mutableListOf())
        binding.recyclerViewMessages.adapter = adapter
        subscribeToMessages()
    }

    private fun subscribeToMessages() {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                adapter.messages.clear()
                adapter.messages.addAll(newMessages)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Failed to read chat messages: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupMessageSending() {
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = Message(text = messageText, senderId = auth.currentUser?.uid ?: "", timestamp = System.currentTimeMillis())
                chatRef.push().setValue(message).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.editTextMessage.text.clear()
                    } else {
                        Toast.makeText(this, "Failed to send message: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
