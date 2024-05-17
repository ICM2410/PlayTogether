package together.devs.playtogether

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import together.devs.playtogether.adapter.ChatAdapter
import together.devs.playtogether.databinding.ActivityChatListBinding
import together.devs.playtogether.model.Chat
import together.devs.playtogether.model.User

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var emptyListMessage: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var chatRef: DatabaseReference

    private var chatList: ArrayList<Chat> = arrayListOf()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        chatRef = database.getReference("chats")

        chatList = ArrayList()
        adapter = ChatAdapter(this, chatList)

        chatRecyclerView = binding.userRecyclerView
        emptyListMessage = binding.emptyListMessage

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter

        initializeChatData() // Initialize some chat data for demonstration
        getAllChats()
    }

    private fun initializeChatData() {
        val chat1 = Chat("1", listOf("1", "2"), "Hello Jane!", System.currentTimeMillis())
        val chat2 = Chat("2", listOf("2", "3"), "Hi Bob!", System.currentTimeMillis())

        chatRef.child(chat1.chatId!!).setValue(chat1).addOnSuccessListener {
            Log.d("ChatListActivity", "Chat 1 added successfully")
        }.addOnFailureListener { e ->
            Log.e("ChatListActivity", "Error adding Chat 1", e)
        }

        chatRef.child(chat2.chatId!!).setValue(chat2).addOnSuccessListener {
            Log.d("ChatListActivity", "Chat 2 added successfully")
        }.addOnFailureListener { e ->
            Log.e("ChatListActivity", "Error adding Chat 2", e)
        }
    }

    private fun getAllChats() {
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (child in snapshot.children) {
                    val chat = child.getValue(Chat::class.java)
                    if (chat != null) {
                        chatList.add(chat)
                    }
                }
                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatListActivity, "Failed to load chats.", Toast.LENGTH_SHORT)
                    .show()
                Log.e("ChatListActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun updateUI() {
        if (chatList.isEmpty()) {
            chatRecyclerView.visibility = RecyclerView.GONE
            emptyListMessage.visibility = TextView.VISIBLE
        } else {
            chatRecyclerView.visibility = RecyclerView.VISIBLE
            emptyListMessage.visibility = TextView.GONE
        }
        adapter.notifyDataSetChanged()
    }
}
