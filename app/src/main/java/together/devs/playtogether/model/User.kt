package together.devs.playtogether.model

data class User(
    val userName: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val teams: List<String> = listOf()
)
