package together.devs.playtogether.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import together.devs.playtogether.ChatActivity
import together.devs.playtogether.R
import together.devs.playtogether.model.Chat
import together.devs.playtogether.model.User

class ChatAdapter(val context: Context, private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_preview, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        val userIds = chat.participants ?: listOf()

        holder.textName.text = "" // Clear previous text

        // Fetch usernames from the database
        for (userId in userIds) {
            userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    val userName = user?.userName ?: "Unknown User"
                    holder.textName.append("$userName ")
                }

                override fun onCancelled(error: DatabaseError) {
                    holder.textName.append("Unknown User ")
                }
            })
        }

        holder.lastMessage.text = chat.lastMessage
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            context.startActivity(intent)
        }
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.txt_name)
        val lastMessage: TextView = itemView.findViewById(R.id.txt_last_message)
    }
}
