package together.devs.playtogether.model

data class Chat(
    val chatId: String? = "",
    val participants: List<String>? = listOf(),
    val lastMessage: String? = "",
    val timestamp: Long? = 0L
) {
    // No-argument constructor required by Firebase
    constructor() : this("", listOf(), "", 0L)
}
