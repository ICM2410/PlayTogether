package together.devs.playtogether.model

data class User(
    val uid: String? = "",
    val userName: String? = "",
    val email: String? = "",
    val profileImageUrl: String? = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "")
}
