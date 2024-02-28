package together.devs.playtogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import together.devs.playtogether.databinding.ActivityFriendsListBinding

class FriendsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mockFriends = arrayOf(
            "Juan Muñoz",
            "Gabriel Espitia",
            "Felipe Gonzalez",
            "Nicolás Cerón"
        )

        binding.friendsList.adapter =
            ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, mockFriends)
    }
}