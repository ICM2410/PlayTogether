import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import together.devs.playtogether.Message
import together.devs.playtogether.databinding.ItemMessageBinding

class MessageAdapter(val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Define the ViewHolder for the message
    class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textViewMessage.text = message.text
            // Additional bindings can be done here based on your Message data class
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflate the layout
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // Bind the message at the position to the holder
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        // Add a message to the list and notify the adapter
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
