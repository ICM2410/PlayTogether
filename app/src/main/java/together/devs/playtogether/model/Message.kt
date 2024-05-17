package together.devs.playtogether.model

data class Message(
    val message: String? = "",
    val senderId: String? = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "")
}
